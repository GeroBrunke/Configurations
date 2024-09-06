package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.jetbrains.annotations.NotNull;

/**
 * Allows for generic classes to be serialized correctly iff the provides generic type is itself also serializable.
 * The generic type T is considered serializable iff {@link ObjectSerializable#isInvalid(Object)} does not return true for
 * an instance of T. Furthermore, this class has to be extended by a user defined subclass in order to serialize generic
 * classes and objects.
 *
 * @param <T> The serializable generic type parameter.
 * @see ObjectSerializable#isInvalid(Object)
 */
public class GenericSerializable<T> implements SerializableObject  {

    @SerializationAPI
    @SuppressWarnings({"unused","rawtypes"})
    private static final Creator<GenericSerializable> CREATOR = new SimpleCreatorImpl<>(GenericSerializable.class);

    protected T value; //the isValid check in the constructor ensures that the value is serializable
    protected String classOfT;

    public GenericSerializable(@NotNull T value){
        if(ObjectSerializable.isInvalid(value))
            throw new SerializationException("Cannot serialize generic value " + value);

        this.classOfT = value.getClass().getName();
        this.value = value;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString("classOfT", classOfT);
        dest.setObject("value", value);
    }

    @SuppressWarnings("unused") //called via reflection API
    protected GenericSerializable(){} //Hide implicit

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull GenericSerializable<T> read(@NotNull SerializedObject src) {
        try{
            this.classOfT = src.getString("classOfT").orElse(null);
            this.value = (T) src.getObject("value", Class.forName(classOfT)).orElse(null);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return this;
    }
}
