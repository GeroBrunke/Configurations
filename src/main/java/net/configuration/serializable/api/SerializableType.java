package net.configuration.serializable.api;

import net.configuration.serializable.impl.types.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum SerializableType {

    BYTE(ByteSerializedObject.class, ".data"),
    JSON(JsonSerializedObject.class, ".json"),
    YAML(YamlSerializedObject.class, ".yml"),
    XML(XmlSerializedObject.class, ".xml"),
    TEXT(TextSerializedObject.class, ".txt"),
    PROPERTIES(PropertiesSerializedObject.class, ".properties"),
    SQL(SQLSerializedObject.class, ".sql");

    @NotNull private final Class<? extends SerializedObject> implClass;
    @NotNull private final String fileExtension;

    SerializableType(@NotNull Class<? extends SerializedObject> implClass, @NotNull String fileExtension){
        this.implClass = implClass;
        this.fileExtension = fileExtension;
    }

    /**
     * Create an empty {@link SerializedObject} to store one object of the given type into.
     *
     * @param forClass The type of the object serialized into the new {@link SerializedObject}.
     * @return A new instance of {@link SerializedObject}.
     */
    @NotNull
    public SerializedObject createEmpty(@NotNull Class<?> forClass){
        try {
            Constructor<? extends SerializedObject> con = this.implClass.getDeclaredConstructor(Class.class);
            con.setAccessible(true);
            return con.newInstance(forClass);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Create an empty {@link SerializedObject} to write data into. Note that this object will never be associated
     * with any class, so deserializing it into any object will result in a {@link SerializationException}. To
     * deserialize this object, every read method has to be invoked in the same order as the write methods before.
     *
     * @return An empty {@link SerializedObject}.
     */
    @NotNull
    public SerializedObject createEmpty(){
        try {
            Constructor<?> con = this.implClass.getDeclaredConstructor();
            con.setAccessible(true);
            return (SerializedObject) con.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException(e);
        }
    }

    public @NotNull String getFileExtension() {
        return fileExtension;
    }
}
