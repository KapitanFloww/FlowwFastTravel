package de.flowwindustries.flowfasttravel.config;

import de.flowwindustries.flowfasttravel.FlowFastTravel;
import lombok.extern.java.Log;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Utility class to provide easy access to the {@link FileConfiguration}.
 */
@Log
public class ConfigurationUtils {

    private final static FileConfiguration fileConfiguration = FlowFastTravel.getInstance().getConfig();

    /**
     * Write a value to the configuration.
     * @param path the key / path
     * @param payload the value
     */
    public static void write(String path, Object payload) {
        fileConfiguration.set(path, payload);
        FlowFastTravel.getInstance().saveConfig();
    }

    /**
     * Read configuration value.
     * @param clazz the return value type
     * @param path the key / path
     * @return the value
     * @param <T> the return value type
     */
    public static <T> T read(Class<T> clazz, String path) {
        Object payload = fileConfiguration.get(path);
        if(payload == null) {
            throw new RuntimeException("No configuration value found for path: " + path);
        }
        if(clazz.isInstance(payload)) {
            return (T) payload;
        }
        throw new RuntimeException("Invalid value type on path: " + path);
    }
}
