package net.configuration.config.impl;

import net.configuration.config.ConfigurationException;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.types.YamlSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class YmlConfiguration extends FileConfiguration {

    protected YamlFile config;

    protected YmlConfiguration(File file) throws IOException {
        super(file);

        this.config = YamlFile.loadConfiguration(file);
    }

    @Override
    public boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reload() {
        try {
            this.config = YamlFile.loadConfiguration(file);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return this.config.contains(path);
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of((byte) this.config.getInt(path));
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getInt(path));
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getLong(path));
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of((short) this.config.getInt(path));
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of((float) this.config.getDouble(path));
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getDouble(path));
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getString(path).charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getBoolean(path));
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.config.set(path, value);
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getString(path));
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.config.set(path, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!(this.hasMember(path) || this.hasMember(path + ".0")))
            return Optional.empty();

        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            List<?> l = this.config.getList(path);
            List<T> res = new ArrayList<>(l.size());
            for(var e : l){
                res.add((T) e);
            }

            return Optional.of(res);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<T> res = new ArrayList<>();
            for(int idx = 0; this.hasMember(path + "." + idx); idx++){
                Optional<T> opt = this.get(path + "." + idx, classOfT);
                opt.ifPresent(res::add);
            }

            return Optional.of(res);

        }else if(classOfT.isEnum()){
            List<String> names = this.config.getStringList(path);
            List<T> res = new ArrayList<>(names.size());
            for(var e : names){
                try {
                    T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, e);
                    res.add(val);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new ConfigurationException(ex);
                }
            }

            return Optional.of(res);

        }else{
            throw new ConfigurationException("Could not read list. Invalid element type " + classOfT);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setList(@NotNull String path, List<T> list) {
        if(list == null || list.isEmpty() || list.get(0) == null)
            throw new ConfigurationException("Cannot write an empty list or a list with null elements");

        Class<T> classOfT = (Class<T>) list.get(0).getClass();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            this.config.set(path, list);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            int idx = 0;
            for(var e : list){
                this.set(path + "." + idx++, e);
            }

        }else if(classOfT.isEnum()){
            List<String> names = new ArrayList<>();
            for(var e : list){
                names.add(e.toString());
            }

            this.config.set(path, names);

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
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

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!this.hasMember(path))
            return Optional.empty();

        if(this.config.get(path).toString().equalsIgnoreCase("null"))
            return Optional.empty();

        //read a valid object
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            T val = classOfT.cast(this.config.get(path));
            return Optional.of(val);

        }else if(List.class.isAssignableFrom(classOfT)){
            throw new ConfigurationException("Cannot get a list this way. Use getList(..) instead.");

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            YamlSerializedObject obj = new YamlSerializedObject((YamlConfiguration) this.config.getConfigurationSection(path), classOfT);
            SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
            return Optional.of((T) val);

        }else if(classOfT.isEnum()){
            try {
                T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, this.config.getString(path));
                return Optional.of(val);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ConfigurationException(e);
            }

        }else if(classOfT.isArray()){
            throw new ConfigurationException("Cannot get an array this way. Use getList(..) instead.");

        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(@NotNull String path, T value) {
        if(value == null){
            this.config.set(path, null);
            return;
        }

        Class<T> classOfT = (Class<T>) value.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            this.config.set(path, value);

        }else if(List.class.isAssignableFrom(classOfT)){
            this.setList(path, (List<?>) value);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            YamlSerializedObject obj = new YamlSerializedObject(classOfT);
            ((SerializableObject) value).write(obj);
            obj.flush();

            try {
                YamlConfiguration nestedConfig = YamlConfiguration.loadConfigurationFromString(obj.toString());
                this.config.set(path, nestedConfig.getConfigurationSection(""));
            } catch (IOException e) {
                throw new ConfigurationException(e);
            }

        }else if(classOfT.isEnum()){
            this.config.set(path, value);

        }else if(classOfT.isArray()){
            this.setArray(path, (Object[]) value);

        }else{
            throw new ConfigurationException("Not a serializable object: " + classOfT);
        }
    }
}
