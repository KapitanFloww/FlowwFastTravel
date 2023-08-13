package de.flowwindustries.flowfasttravel.config;

import de.flowwindustries.flowfasttravel.FlowFastTravel;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util class to set the default configuration.
 */
@Log
public class DefaultConfiguration {

    public static final String PATH_DB_REMOTE = "database.remote";
    public static final String PATH_DB_SHOW_SQL = "database.show-sql";
    public static final String PATH_DB_DDL = "database.ddl-auto";
    public static final String PATH_DB_HOST = "database.host";
    public static final String PATH_DB_PORT = "database.port";
    public static final String PATH_DB_DATABASE = "database.database";
    public static final String PATH_DB_USERNAME = "database.username";
    public static final String PATH_DB_PASSWORD = "database.password";
    public static final String PATH_DB_DRIVER = "database.driverClass";

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
        configuration.addDefault(PATH_DB_DDL, "update");

        configuration.addDefault(PATH_DB_HOST, "localhost");
        configuration.addDefault(PATH_DB_PORT, 3306);
        configuration.addDefault(PATH_DB_DATABASE, "sa");
        configuration.addDefault(PATH_DB_USERNAME, "fasttravel");
        configuration.addDefault(PATH_DB_PASSWORD, "MyPassw0rd!");
        configuration.addDefault(PATH_DB_DRIVER, "org.postgresql.Driver");

        //Save configuration
        configuration.options().copyDefaults(true);
        FlowFastTravel.getInstance().saveConfig();
    }
}
