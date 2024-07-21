package net.configuration.config;

import net.configuration.serializable.api.SerializableObject;
import org.jetbrains.annotations.NotNull;

public interface Configurable extends SerializableObject {

    /**
     * Get the config instance for this object.
     *
     * @return The config for this object.
     */
    @NotNull Configuration getConfig();

    /**
     * Load the config from the given path. If the config is a SQL table then the path is interpreted as the table name, otherwise
     * as a file path.
     *
     * @param path The path to the config.
     * @param externalFile If the file is outside the running Jar-File.
     */
    void loadConfig(@NotNull String path, boolean externalFile);

    /**
     * Check if the current path is read-only.
     *
     * @param path The path to check.
     * @return If the value for this path is read-only.
     */
    boolean isReadOnly(@NotNull String path);

}
