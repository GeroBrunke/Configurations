package net.configuration.config;

import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class FileConfiguration implements Configuration{

    @NotNull protected final File file;

    protected FileConfiguration(File file) throws IOException {
        if(!file.exists() && !file.createNewFile())
            throw new IOException("Configuration file could not be created.");

        this.file = file;
    }

    /**
     * Load a configuration on the given file based on this file's extension. If the given file does not exist then this
     * method will create the given file if possible.
     *
     * @param file The file to open.
     * @return A new {@link FileConfiguration} instance to modify this file.
     * @throws IllegalArgumentException If the configuration could not be loaded.
     * @see SerializableType#forFile(File)
     */
    @NotNull
    public static FileConfiguration loadConfig(File file){
        Configuration config = SerializableType.forFile(file);
        if(config instanceof FileConfiguration fc){
            return fc;
        }

        throw new IllegalArgumentException("Not a file config: " + config.getClass());
    }

    @Override
    @NotNull
    public String getName(){
        return file.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T[]> getArray(@NotNull String path, @NotNull Class<T> classOfT) {
        Optional<List<T>> opt = this.getList(path, classOfT);
        if(opt.isEmpty())
            return Optional.empty();

        return Optional.of(opt.get().toArray((T[]) Array.newInstance(classOfT, 0)));
    }

    @Override
    public <T> void setArray(@NotNull String path, T[] array) {
        this.setList(path, List.of(array));
    }


    /**
     * Fetch the data field of the given serialized object.
     *
     * @param obj The serialized object to retrieve the data field from.
     * @return THe instance of the data field inside the given serialized object.
     * @see FileConfiguration#getDataFiled(SerializedObject)
     */
    @SuppressWarnings("unchecked")
    protected <T> T getDataFieldFromSerializedObject(@NotNull SerializedObject obj){
        try{
            Field field = this.getDataFiled(obj);
            field.setAccessible(true);
            return (T) field.get(obj);

        }catch(Exception e){
            throw new ConfigurationException(e);
        }
    }

    /**
     * Convert the list of strings to a list of enums of given type. The list must contain the string representations
     * of the enum type.
     *
     * @param names The list of enum names.
     * @param classOfT The type of the enum the list is converted to.
     * @return A list containing the actual enum values from the name list.
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getEnumList(@NotNull List<String> names, @NotNull Class<T> classOfT){
        List<T> res = new ArrayList<>(names.size());
        for(var e : names){
            try {
                T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, e);
                res.add(val);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new ConfigurationException(ex);
            }
        }

        return res;
    }

    /**
     * Convert a string representation of an enum into the specific enum value.
     *
     * @param elem The string representation of the enum.
     * @param classOfT The type of the desired enum.
     * @return The actual enum representation based on the given string.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    protected <T> T convertToEnum(@NotNull String elem, @NotNull Class<T> classOfT){
        try {
            return (T) classOfT.getMethod("valueOf", String.class).invoke(null, elem);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Since the storage of a serialized object is held in a specific object like JsonObject, XMLDocument etc.,
     * we can retrieve this storage by using the reflection API. By default, the storage field is named data.
     *
     * @param obj The object to retrieve the storage object from.
     * @return The instance of the storage object in the given serialized object.
     * @throws NoSuchFieldException If the data field was not found.
     */
    private Field getDataFiled(@NotNull SerializedObject obj) throws NoSuchFieldException {
        try{
            return obj.getClass().getDeclaredField("data");
        }catch(NoSuchFieldException e){
            return obj.getClass().getField("data");
        }
    }

}
