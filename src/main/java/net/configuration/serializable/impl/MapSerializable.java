package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MapSerializable implements SerializableObject {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<MapSerializable> CREATOR = new SimpleCreatorImpl<>(MapSerializable.class);

    private Collection<TupleSerializable> tuples;
    private transient SerializableType type;

    @SuppressWarnings("unused") //called via reflection API
    private MapSerializable(){} //Hide implicit

    public MapSerializable(@NotNull SerializableType type, @NotNull Map<?, ?> map){
        this.tuples = new ArrayList<>();
        this.type = type;
        for(var e : map.entrySet()){
            TupleSerializable tuple = new TupleSerializable(type, e.getKey(), e.getValue());
            this.tuples.add(tuple);
        }

    }

    /**
     * Retrieve the map object from the serialized version.
     *
     * @param classOfK The type of the key elements.
     * @param classOfV The type of the value elements.
     * @return A java map representation of the serialized tuple list from this object.
     */
    @SuppressWarnings("unchecked")
    public <K,V> Map<K,V> getMap(@NotNull Class<K> classOfK, @NotNull Class<V> classOfV){
        Map<K,V> map = new HashMap<>();
        for(var tuple : this.tuples){
            K key = (K) tuple.getKey(type, classOfK).orElseThrow();
            V value = (V) tuple.getValue(type, classOfV).orElseThrow();

            map.put(key, value);
        }
        return map;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setList("map", this.tuples);
    }

    @Override
    public @NotNull MapSerializable read(@NotNull SerializedObject src) {
        this.tuples = new ArrayList<>();
        this.type = SerializableType.fromImplementationClass(src.getClass());
        for(var elem : src.getList("map", TupleSerializable.class).orElse(new ArrayList<>())){
            this.tuples.add((TupleSerializable) elem);
        }
        return this;
    }
}
