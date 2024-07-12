package net.configuration.serializable.api;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface SerializableObject extends Serializable {

    /**
     * Write the current object into its serialized version into the given object.
     *
     * @param dest The serialized object to write this object to.
     */
    void write(@NotNull SerializedObject dest);

    /**
     * Read the object serialized in the given serialized object.
     *
     * @param src The serialized version of the object to read from.
     * @return The deserialized version of the object.
     */
    @NotNull SerializableObject read(@NotNull SerializedObject src);

}
