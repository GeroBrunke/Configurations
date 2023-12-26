package net.configuration.serializable.impl;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class EnumSerializable implements SerializableObject  {

    private String enumClass;
    private String enumValue;

    public EnumSerializable(@NotNull Object enumValue){
        if(!enumValue.getClass().isEnum())
            throw new SerializationException("Not an enum: " + enumValue.getClass());

        try {
            this.enumClass = enumValue.getClass().getName();
            this.enumValue = this.getEnumClass().getMethod("name").invoke(enumValue).toString();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SerializationException(e);
        }
    }

    @SuppressWarnings("unused")
    private EnumSerializable(){/*called by simple creator*/}

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(this.enumClass);
        dest.setString(this.enumValue);
    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        Optional<String> classOpt = src.getString();
        if(classOpt.isEmpty())
            throw new SerializationException("Could not find class value");
        this.enumClass = classOpt.get();

        Optional<String> valueOpt = src.getString();
        if(valueOpt.isEmpty())
            throw new SerializationException("Could not find enum value");
        this.enumValue = valueOpt.get();

        return this;
    }

    @NotNull
    public Class<?> getEnumClass(){
        try {
            return Class.forName(this.enumClass);
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @NotNull
    public Object getEnumValue(){
        try {
            return this.getEnumClass().getDeclaredMethod("valueOf", String.class).invoke(null, this.enumValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SerializationException(e);
        }
    }
}
