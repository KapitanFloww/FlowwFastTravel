package de.flowwindustries.flowwfasttravel.config;

import de.flowwindustries.flowwfasttravel.FlowFastTravel;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util class to set the default configuration.
 */
@Log
public class DefaultConfiguration {

    public static final String PATH_DB_REMOTE = "database.remote";
    public static final String PATH_DB_SHOW_SQL = "database.show-sql";

    public static final String PATH_JDBC_URL = "database.jdbc.url";
    public static final String PATH_JDBC_USER = "database.jdbc.user";
    public static final String PATH_JDBC_PASSWORD = "database.jdbc.password";
    public static final String PATH_JDBC_DRIVER = "database.jdbc.driverClass";

    public static final String PATH_HIBERNATE_DDL = "hibernate.ddl-auto";
    public static final String PATH_HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String PATH_HIBERNATE_PROVIDER = "hibernate.provider";

    private DefaultConfiguration() {

    }

    /**
     * Setup default configuration values for the given {@link FileConfiguration}.
     * @param configuration the configuration file
     */
    public static void setupDefaultConfiguration(FileConfiguration configuration) {
        log.info("Setting up default configuration values");
        //Setup values
        configuration.addDefault(PATH_DB_REMOTE, true);
        configuration.addDefault(PATH_DB_SHOW_SQL, false);

        configuration.addDefault(PATH_JDBC_URL, "jdbc:postgresql://localhost:5432/fasttravel");
        configuration.addDefault(PATH_JDBC_USER, "sa");
        configuration.addDefault(PATH_JDBC_PASSWORD, "MyPassw0rd!");
        configuration.addDefault(PATH_JDBC_DRIVER, "org.postgresql.Driver");

        configuration.addDefault(PATH_HIBERNATE_DDL, "update");
        configuration.addDefault(PATH_HIBERNATE_DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        configuration.addDefault(PATH_HIBERNATE_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        //Save configuration
        configuration.options().copyDefaults(true);
        FlowFastTravel.getInstance().saveConfig();
    }
}
