package net.configuration.serializable.api;

import net.configuration.serializable.impl.types.ByteSerializedObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum SerializableType {

    BYTE(ByteSerializedObject.class, ".data"),
    JSON(ByteSerializedObject.class, ".json"),
    YAML(ByteSerializedObject.class, ".yml"),
    XML(ByteSerializedObject.class, ".xml"),
    TEXT(ByteSerializedObject.class, ".txt"),
    SQL(ByteSerializedObject.class, ".sql");

    @NotNull private final Class<? extends SerializedObject> implClass;
    @NotNull private final String fileExtension;

    SerializableType(@NotNull Class<? extends SerializedObject> implClass, @NotNull String fileExtension){
        this.implClass = implClass;
        this.fileExtension = fileExtension;
    }

    @NotNull
    public SerializedObject createEmpty(@NotNull Class<?> forClass){
        try {
            Constructor<? extends SerializedObject> con = this.implClass.getDeclaredConstructor(Class.class);
            return con.newInstance(forClass);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new SerializationException(e);
        }
    }

    public @NotNull String getFileExtension() {
        return fileExtension;
    }
}
