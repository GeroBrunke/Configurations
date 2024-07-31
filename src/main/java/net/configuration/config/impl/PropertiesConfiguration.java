package net.configuration.config.impl;

import net.configuration.config.ConfigurationException;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.PropertiesSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PropertiesConfiguration extends FileConfiguration {

    protected Properties config;

    protected PropertiesConfiguration(File file) throws IOException {
        super(file);

        try (FileReader reader = new FileReader(file)){
            this.config = new Properties();
            this.config.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean save() {
        try (FileOutputStream fos = new FileOutputStream(this.file)){
            this.config.store(fos, null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reload() {
        try (FileReader reader = new FileReader(file)){
            this.config = new Properties();
            this.config.load(reader);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return this.config.containsKey(path);
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Byte.parseByte(this.config.getProperty(path)));
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Integer.parseInt(this.config.getProperty(path)));
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Long.parseLong(this.config.getProperty(path)));
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Short.parseShort(this.config.getProperty(path)));
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Float.parseFloat(this.config.getProperty(path)));
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Double.parseDouble(this.config.getProperty(path)));
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getProperty(path).charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Boolean.parseBoolean(this.config.getProperty(path)));
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.getProperty(path));
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.config.setProperty(path, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!(this.hasMember(path)))
            return Optional.empty();

        String elem = this.config.getProperty(path);
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            return Optional.of(this.fromStringList(elem, classOfT));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            try{
                List<T> list = new ArrayList<>();
                String array = this.getString(path).orElse("");
                if(array.isBlank())
                    return Optional.empty();

                array = array.substring(1, array.length() - 1);
                for(String entry : array.split("\\[<br>]")){
                    if(entry.isEmpty())
                        continue;

                    PropertiesSerializedObject nested = new PropertiesSerializedObject(classOfT, entry);
                    SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(nested);
                    list.add((T) val);
                }

                return Optional.of(list);

            }catch(IOException e){
                e.printStackTrace();
            }

        }else if(classOfT.isEnum()){
            List<String> names = this.fromStringList(elem, String.class);
            return Optional.of(this.getEnumList(names, classOfT));

        }else{
            throw new ConfigurationException("Could not read list. Invalid element type " + classOfT);
        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setList(@NotNull String path, List<T> list) {
        if(list == null || list.isEmpty() || list.get(0) == null)
            throw new ConfigurationException("Cannot write an empty list or a list with null elements");

        Class<T> classOfT = (Class<T>) list.get(0).getClass();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            this.setString(path, this.toStringList(list));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            StringBuilder str = new StringBuilder("[");
            for(var e : list){
                PropertiesSerializedObject obj = new PropertiesSerializedObject(classOfT);
                ((SerializableObject) e).write(obj);
                obj.flush();

                str.append("[<br>]").append(obj);
            }
            str.append("]");
            this.setString(path, str.toString());

        }else if(classOfT.isEnum()){
            List<String> names = new ArrayList<>();
            for(var e : list){
                Enum<?> en = (Enum<?>) e;
                names.add(en.name());
            }
            this.setString(path, this.toStringList(names));

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!this.hasMember(path))
            return Optional.empty();

        String elem = this.config.getProperty(path);
        if(elem.equalsIgnoreCase("null"))
            return Optional.empty();

        //read a valid object
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            Object prim = Objects.requireNonNull(SerializationHelper.extractPrimitive(elem, classOfT));
            return Optional.of((T) prim);

        }else if(List.class.isAssignableFrom(classOfT)){
            throw new ConfigurationException("Cannot get a list this way. Use getList(..) instead.");

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            try{
                PropertiesSerializedObject obj = new PropertiesSerializedObject(classOfT, elem);
                SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
                return Optional.of((T) val);

            }catch(IOException e){
                e.printStackTrace();
            }

        }else if(classOfT.isEnum()){
            return Optional.of(this.convertToEnum(elem, classOfT));

        }else if(classOfT.isArray()){
            throw new ConfigurationException("Cannot get an array this way. Use getList(..) instead.");

        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(@NotNull String path, T value) {
        if(value == null){
            this.setString(path, "null");
            return;
        }

        Class<T> classOfT = (Class<T>) value.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            this.setString(path, String.valueOf(value));

        }else if(List.class.isAssignableFrom(classOfT)){
            this.setList(path, (List<?>) value);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            PropertiesSerializedObject obj = new PropertiesSerializedObject(classOfT);
            ((SerializableObject) value).write(obj);
            obj.flush();

            this.config.setProperty(path, obj.toString());

        }else if(classOfT.isEnum()){
            this.setString(path, String.valueOf(value));

        }else if(classOfT.isArray()){
            this.setArray(path, (Object[]) value);

        }else{
            throw new ConfigurationException("Not a serializable object: " + classOfT);
        }
    }

    private String toStringList(@NotNull List<?> list){
        StringBuilder entry = new StringBuilder();
        for(var e : list){
            entry.append(", ").append(e);
        }
        entry = new StringBuilder(entry.substring(2));
        return entry.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> fromStringList(@NotNull String elem, @NotNull Class<T> classOfT){
        String[] d = elem.split(", ");
        List<T> res = new ArrayList<>();
        for(String e : d){
            T val = (T) SerializationHelper.extractPrimitive(e, classOfT);
            res.add(val);
        }

        return res;
    }

}
