package net.configuration.serializable.impl;

import com.google.gson.JsonParser;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.types.JsonSerializedObject;
import net.configuration.serializable.impl.types.PropertiesSerializedObject;
import net.configuration.serializable.impl.types.YamlSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

public class TupleSerializable implements SerializableObject {

    private static final String INVALID = "INVALID";

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<TupleSerializable> CREATOR = src -> {
        TupleSerializable ser = new TupleSerializable();
        return ser.read(src);
    };

    private String key;
    private String value;

    private TupleSerializable(){} //Hide implicit

    public TupleSerializable(@NotNull SerializableType type, @NotNull Object key, @NotNull Object value){
        this.initKey(type, key);
        this.initValue(type, value);
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString("key", this.key);
        dest.setString("value", this.value);
    }

    @Override
    public @NotNull TupleSerializable read(@NotNull SerializedObject src) {
        this.key = src.getString("key").orElse(INVALID);
        this.value = src.getString("value").orElse(INVALID);
        return this;
    }

    @NotNull
    public Optional<Object> getKey(@NotNull SerializableType type, @NotNull Class<?> clazz){
        return this.deserializeString(type, clazz, this.key);
    }

    @NotNull
    public Optional<Object> getValue(@NotNull SerializableType type, @NotNull Class<?> clazz){
        return this.deserializeString(type, clazz, this.value);
    }

    private void initKey(@NotNull SerializableType type, @NotNull Object key){
        if(ClassUtils.isPrimitiveOrWrapper(key.getClass()) || key.getClass() == String.class){
            //primitive key
            this.key = String.valueOf(key);

        }else if(key instanceof SerializableObject keySer){
            //complex key
            SerializedObject ser = type.createEmpty(key.getClass());
            keySer.write(ser);
            this.key = ser.toString();

        }else{
            throw new SerializationException("Could not serialize key type " + key.getClass());
        }
    }

    private void initValue(@NotNull SerializableType type, @NotNull Object value){
        if(ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class){
            //primitive key
            this.value = String.valueOf(value);

        }else if(value instanceof SerializableObject keySer){
            //complex key
            SerializedObject ser = type.createEmpty(value.getClass());
            keySer.write(ser);
            this.value = ser.toString();

        }else{
            throw new SerializationException("Could not serialize key type " + value.getClass());
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private Optional<Object> deserializeString(@NotNull SerializableType type, @NotNull Class<?> clazz, @NotNull String data){
        if(data.equals(INVALID))
            return Optional.empty();

        try{
            if(ClassUtils.isPrimitiveOrWrapper(clazz)){
                Method m = clazz.getDeclaredMethod("valueOf", String.class);
                m.setAccessible(true);
                return Optional.of(m.invoke(null, data));

            }else if(clazz == String.class){
                return Optional.of(data);

            }else if(SerializableObject.class.isAssignableFrom(clazz)){
                SerializedObject src = this.create(type, clazz, data);
                Class<? extends SerializableObject> serClass = (Class<? extends SerializableObject>) clazz;
                return Optional.of(Creator.getCreator(serClass).read(src));

            }else{
                throw new SerializationException("Invalid key type. Not serializable");
            }

        }catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private SerializedObject create(@NotNull SerializableType type, @NotNull Class<?> clazz, @NotNull String data)
            throws IOException {

        switch(type){
            case JSON -> {
                return new JsonSerializedObject(JsonParser.parseString(data).getAsJsonObject(), clazz);
            }

            case YAML -> {
                return new YamlSerializedObject(YamlConfiguration.loadConfigurationFromString(data));
            }

            case PROPERTIES -> {
                return new PropertiesSerializedObject(clazz, data);
            }

            default -> throw new UnsupportedOperationException("Implement me");
        }
    }
}
