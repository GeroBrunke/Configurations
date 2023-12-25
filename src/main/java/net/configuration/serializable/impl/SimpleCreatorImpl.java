package net.configuration.serializable.impl;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

public class SimpleCreatorImpl<T extends SerializableObject> implements Creator<T> {

    @Override
    public @NotNull T read(@NotNull SerializedObject src) {
        throw new SerializationException("Could not read just from the serialized object in a simple creator. ");
    }
}
