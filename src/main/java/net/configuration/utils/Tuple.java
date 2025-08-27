package net.configuration.utils;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

public class Tuple<K,V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public Tuple(K key, V value){
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    public K setKey(K key) {
        K old = this.key;
        this.key = key;
        return old;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V tmp = this.value;
        this.value = value;
        return tmp;
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.addProperty("key", key.toString());
        json.addProperty("value", value.toString());

        return json.toString();
    }

    //Check for GIT: Changed this class by adding these two methods: equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple<?, ?> tuple)) return false;
        return Objects.equals(key, tuple.key) && Objects.equals(value, tuple.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
