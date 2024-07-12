package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class EnumSerializable implements SerializableObject  {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<EnumSerializable> CREATOR = new SimpleCreatorImpl<>(EnumSerializable.class);
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
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnumValue(){
        try {
            Object obj = this.getEnumClass().getDeclaredMethod("valueOf", String.class).invoke(null, this.enumValue);
            return (T) obj;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Convert the object instance of an enum value into a correct enum value.
     *
     * @param enumSuspect The object instance of an enum.
     * @return The enum instance of that object.
     * @throws SerializationException If the given object is no enum.
     */
    //convert the enum object to a 'real' enum instance
    @NotNull
    public static <T extends Enum<T>> T toEnum(@NotNull Object enumSuspect){
        return SerializationHelper.toEnum(enumSuspect);
    }
}
