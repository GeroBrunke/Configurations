package net.configuration.serializable.impl;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CollectionSerializable implements SerializableObject {

    public CollectionSerializable(@NotNull Collection<?> collection){

    }

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        return this;
    }
}
