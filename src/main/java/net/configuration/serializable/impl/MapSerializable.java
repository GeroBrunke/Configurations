package net.configuration.serializable.impl;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapSerializable implements SerializableObject {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<MapSerializable> CREATOR = new SimpleCreatorImpl<>(MapSerializable.class);

    @SuppressWarnings("unused") //called via reflection API
    private MapSerializable(){} //Hide implicit

    public MapSerializable(@NotNull Map<?,?> map){

    }

    public <K,V> Map<K,V> getMap(@NotNull Class<K> classOfK, @NotNull Class<V> classOfV){
        return null;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        return null;
    }
}
