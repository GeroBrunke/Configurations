package net.configuration.serializable.api;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ObjectStorage {

    private ObjectStorage(){} //Hide implicit

    /**
     * Serialize the given object into the provided format.
     *
     * @param data The object to serialize.
     * @param type The format of the serialized data.
     * @return A serialized version of the provided object or an empty optional, if any exception occurred during serialization.
     */
    @NotNull
    public static Optional<SerializedObject> serialize(@NotNull SerializableObject data, @NotNull SerializableType type){
        return Optional.empty();
    }

    /**
     * Restore a serialized object from the given serialized data in the provided representation.
     *
     * @param rwaData The serialized version of the object.
     * @param type The representation type of the serialized data.
     * @return The restored version of the serialized object. If any error occurred during the deserialization, then an
     * empty optional is returned.
     */
    @NotNull
    public static <T extends SerializableObject> Optional<T> deserialize(@NotNull SerializedObject rwaData, @NotNull SerializableType type){
        return Optional.empty();
    }

}
