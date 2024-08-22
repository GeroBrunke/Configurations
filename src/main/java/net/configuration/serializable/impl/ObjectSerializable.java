package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ObjectSerializable implements SerializableObject  {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<ObjectSerializable> CREATOR = new SimpleCreatorImpl<>(ObjectSerializable.class);

    @SuppressWarnings("unused") //called via reflection API
    private ObjectSerializable(){} //Hide implicit

    private transient Class<?> clazz;

    private String className;
    private Object object; //the isValid check ensures that the object is serializable

    public ObjectSerializable(@NotNull Object object){
        this.clazz = object.getClass();
        this.className = clazz.getName();
        this.object = object;

        if (isInvalid(object))
                throw new SerializationException("Cannot serialize object " + object);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getData(@NotNull Class<T> classOfT){
        if(this.object == null)
            return Optional.empty();

        if(classOfT.isAssignableFrom(this.clazz)){
            T val = (T) this.object;
            return Optional.of(val);
        }

        return Optional.empty();
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString("classVal", this.className);
        dest.setObject("value", this.object);
    }

    @Override
    public @NotNull ObjectSerializable read(@NotNull SerializedObject src) {
        try {
            this.className = src.getString("classVal").orElse("");
            this.clazz = Class.forName(this.className);
            this.object = src.getObject("value", this.clazz).orElse(null);

            return this;
        } catch (ClassNotFoundException e) {
            throw new org.apache.commons.lang3.SerializationException(e);
        }
    }

    public static boolean isInvalid(@NotNull Object object){
        return !(object instanceof SerializableObject) && !(object instanceof SerializedObject) && !(object instanceof Map<?, ?>) &&
                !(object instanceof List<?>) && !(object instanceof Enum<?>) && !(object instanceof String) &&
                !ClassUtils.isPrimitiveOrWrapper(object.getClass());

    }
}
