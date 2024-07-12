package net.configuration.serializable.api;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

interface ObjectSerializer {

    /**
     * Get the raw object mapped to the given name.
     *
     * @param name The name it is mapped to.
     * @param classOfT The type of the object.
     * @return The object mapped to the name or an empty optional if none was found.
     */
    Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT);

}
