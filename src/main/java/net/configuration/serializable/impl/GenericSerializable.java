package net.configuration.serializable.impl;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

public class GenericSerializable<T> implements SerializableObject  {

    @SerializationAPI
    @SuppressWarnings({"unused","rawtypes"})
    private static final Creator<GenericSerializable> CREATOR = new SimpleCreatorImpl<>(GenericSerializable.class);

    public GenericSerializable(@NotNull Class<T> classOfT){

    }

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @SuppressWarnings("unused") //called via reflection API
    private GenericSerializable(){} //Hide implicit

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        return this;
    }
}
