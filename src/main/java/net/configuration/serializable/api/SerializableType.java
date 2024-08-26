package net.configuration.serializable.api;

import net.configuration.config.Configuration;
import net.configuration.config.FileConfiguration;
import net.configuration.config.impl.*;
import net.configuration.serializable.impl.types.*;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum SerializableType {

    BYTE(ByteSerializedObject.class, ByteConfiguration.class, ".data"),
    JSON(JsonSerializedObject.class, JsonConfiguration.class, ".json"),
    YAML(YamlSerializedObject.class, YmlConfiguration.class, ".yml"),
    XML(XmlSerializedObject.class, XmlConfiguration.class, ".xml"),
    TEXT(TextSerializedObject.class, TextConfiguration.class, ".txt"),
    PROPERTIES(PropertiesSerializedObject.class, PropertiesConfiguration.class, ".properties"),
    SQL(SQLSerializedObject.class, SQLConfiguration.class, ".sqlData");

    @NotNull private final Class<? extends SerializedObject> implClass;
    @NotNull private final Class<? extends Configuration> configClass;
    @NotNull private final String fileExtension;

    SerializableType(@NotNull Class<? extends SerializedObject> implClass, @NotNull Class<? extends Configuration> configClass,
                     @NotNull String fileExtension){
        this.implClass = implClass;
        this.fileExtension = fileExtension;
        this.configClass = configClass;
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
            Constructor<? extends SerializedObject> con = this.implClass.getDeclaredConstructor();
            con.setAccessible(true);
            return con.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException(e);
        }
    }


    /**
     * Create a new {@link FileConfiguration} instance for the given File.
     *
     * @param file The file to open.
     * @return A new configuration on the given file.
     * @throws IllegalArgumentException If the configuration instance could not be created.
     */
    @NotNull
    public static Configuration forFile(File file){
        String extension = "." + FilenameUtils.getExtension(file.getName());
        for(SerializableType type : values()){
            if(extension.equals(type.fileExtension)){
                return createInstance(type.configClass, file);
            }
        }
        throw new IllegalArgumentException("No configuration found for file " + file.getName() + " (" + extension + ")");
    }

    /**
     * Create a new instance of the given class by invoking the constructor that only requires one file as input.
     *
     * @param clazz The class to instantiate.
     * @param file The file argument for the constructor.
     * @return A new instance of the given class.
     */
    @NotNull
    private static Configuration createInstance(Class<? extends Configuration> clazz, File file){
        try {
            Constructor<? extends Configuration> con = clazz.getDeclaredConstructor(File.class);
            con.setAccessible(true);
            return con.newInstance(file);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new IllegalArgumentException("Could not create configuration instance for file " + file.getName());
        }
    }

    /**
     * Get the serializable type based on a given implementation class.
     *
     * @param implClass The concrete implementation class of a {@link SerializedObject}.
     * @return The serializable type for the given implementation class or null if there is no type for the given class.
     */
    public static SerializableType fromImplementationClass(Class<?> implClass){
        for(SerializableType type : values()){
            if(type.implClass == implClass){
                return type;
            }
        }
        return null;
    }
}
