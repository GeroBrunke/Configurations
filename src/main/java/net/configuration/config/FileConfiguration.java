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

    private Field getDataFiled(@NotNull SerializedObject obj) throws NoSuchFieldException {
        try{
            return obj.getClass().getDeclaredField("data");
        }catch(NoSuchFieldException e){
            return obj.getClass().getField("data");
        }
    }
}
