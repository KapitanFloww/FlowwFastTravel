package de.flowwindustries.flowwfasttravel.repository;

import de.flowwindustries.flowwfasttravel.config.ConfigurationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_DB_REMOTE;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_DB_SHOW_SQL;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_HIBERNATE_PROVIDER;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_JDBC_URL;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_JDBC_USER;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_JDBC_PASSWORD;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_JDBC_DRIVER;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_HIBERNATE_DDL;
import static de.flowwindustries.flowwfasttravel.config.DefaultConfiguration.PATH_HIBERNATE_DIALECT;

/**
 * Abstract class to provide easy functionality to access the persistence layer.
 * @param <E> the entity class
 * @param <I> the identifier class
 */
@RequiredArgsConstructor
public abstract class AbstractRepository<E, I> {

    private final Class<E> entityClass;
    private static EntityManagerFactory entityManagerFactory;

    /**
     * Create and persist an entity.
     * @param entity the entity to persist.
     * @return the persisted entity
     */
    @Transactional
    public synchronized E create(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    /**
     * Edit and persisted entity.
     * @param entity the updated entity to persist
     * @return the updated entity
     */
    @Transactional
    public synchronized E edit(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    /**
     * Remove a persistent entity.
     * @param entity the entity to remove
     */
    @Transactional
    public synchronized void remove(E entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.remove(entityManager.merge(entity));

        entityTransaction.commit();
        entityManager.close();
    }

    /**
     * Find a persistent entity.
     * @param id id of the
     * @return the found entity inside an {@link Optional} or {@link Optional#empty()}
     */
    public synchronized Optional<E> find(I id) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        E e = entityManager.find(entityClass, id);

        entityTransaction.commit();
        entityManager.close();
        if (e == null) {
            return Optional.empty();
        }
        return Optional.of(e);
    }

    /**
     * Find all persistent entities.
     * @return all entities
     */
    public synchronized Collection<E> findAll() {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        Collection<E> e = getEntityManager().createQuery(cq).getResultList();

        entityTransaction.commit();
        entityManager.close();
        return e;
    }

    /**
     * Get the {@link EntityManager}.
     * @return the {@link EntityManager}
     * @throws RuntimeException if the {@link EntityManager} is not yet initialized
     */
    protected EntityManager getEntityManager() throws IllegalStateException {
        setupEntityManagerFactory();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            return entityManagerFactory.createEntityManager();
        } else {
            throw new IllegalStateException("Entity Manager not initialized!");
        }
    }

    private void setupEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            final boolean remoteDatabase = ConfigurationUtils.read(Boolean.class, PATH_DB_REMOTE);
            final boolean showSQL = ConfigurationUtils.read(Boolean.class, PATH_DB_SHOW_SQL);
            final String ddlAuto = ConfigurationUtils.read(String.class, PATH_HIBERNATE_DDL);
            final String hibernateConnectionProvider = ConfigurationUtils.read(String.class, PATH_HIBERNATE_PROVIDER);

            Properties properties = new Properties();
            properties.put("hibernate.show_sql", showSQL);
            properties.put("hibernate.hbm2ddl.auto", ddlAuto);
            properties.put("hibernate.connection.provider_class", hibernateConnectionProvider);

            if (remoteDatabase) {
                // Setup Remote DB Connection
                final String jdbcUrl = ConfigurationUtils.read(String.class, PATH_JDBC_URL);
                final String jdbcUser = ConfigurationUtils.read(String.class, PATH_JDBC_USER);
                final String jdbcPassword = ConfigurationUtils.read(String.class, PATH_JDBC_PASSWORD);
                final String jdbcDriver = ConfigurationUtils.read(String.class, PATH_JDBC_DRIVER);
                final String hibernateDialect = ConfigurationUtils.read(String.class, PATH_HIBERNATE_DIALECT);

                properties.put("jakarta.persistence.jdbc.driver", jdbcDriver);
                properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
                properties.put("jakarta.persistence.jdbc.user", jdbcUser);
                properties.put("jakarta.persistence.jdbc.password", jdbcPassword);
                properties.put("hibernate.dialect", hibernateDialect);

            } else {
                // Setup H2 Connection
                properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
                properties.put("javax.persistence.jdbc.url", "jdbc:h2:file:~/test;USER=sa;PASSWORD=password");
                properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            }

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
        }
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
