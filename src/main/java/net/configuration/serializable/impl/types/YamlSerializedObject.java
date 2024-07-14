package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.NullSerializable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

public class YamlSerializedObject extends AbstractSerializedObject{

    private final YamlConfiguration data;

    @SuppressWarnings("unused") //called via reflection API
    public YamlSerializedObject(){
        try {
            this.data = YamlConfiguration.loadConfigurationFromString("");
            this.ymlPrefix = "";
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public YamlSerializedObject(YamlConfiguration data){
        this.data = data;
        this.ymlPrefix = "";
    }

    public YamlSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
        try {
            this.data = YamlConfiguration.loadConfigurationFromString("");
            this.ymlPrefix = clazz.getSimpleName() + ".";
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public YamlSerializedObject(@NotNull YamlConfiguration config, @NotNull Class<?> forClass){
        super(forClass);
        this.data = config;
        this.ymlPrefix = clazz.getSimpleName() + ".";
    }

    @SuppressWarnings("unused")
    public YamlSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);

        try {
            this.data = YamlConfiguration.loadConfigurationFromString("");
            this.ymlPrefix = clazz.getSimpleName() + ".";
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        byte val = (byte) this.data.getInt(this.ymlPrefix + name);
        return Optional.of(val);
    }


    @Override
    public void setByte(@NotNull String name, byte value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Short> getShort(@NotNull String name) {
        short val = (short) this.data.getInt(this.ymlPrefix + name);
        return Optional.of(val);
    }


    @Override
    public void setShort(@NotNull String name, short value) {
        this.data.set(this.ymlPrefix + name, value);
    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        int val = this.data.getInt(this.ymlPrefix + name);
        return Optional.of(val);
    }


    @Override
    public void setInt(@NotNull String name, int value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Long> getLong(@NotNull String name) {
        long val = this.data.getLong(this.ymlPrefix + name);
        return Optional.of(val);
    }


    @Override
    public void setLong(@NotNull String name, long value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        float val = (float) this.data.getDouble(this.ymlPrefix + name);
        return Optional.of(val);
    }

    @Override
    public void setFloat(@NotNull String name, float value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        double val = this.data.getDouble(this.ymlPrefix + name);
        return Optional.of(val);
    }


    @Override
    public void setDouble(@NotNull String name, double value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Character> getChar(@NotNull String name) {
        char val = this.data.getString(this.ymlPrefix + name).charAt(0);
        return Optional.of(val);
    }

    @Override
    public void setChar(@NotNull String name, char value) {
        this.data.set(this.ymlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        String val = this.data.getString(this.ymlPrefix + name);
        return Optional.of(val);
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.data.set(this.ymlPrefix + name, value);
    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        boolean val = this.data.getBoolean(this.ymlPrefix + name);
        return Optional.of(val);
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.data.set(this.ymlPrefix + name, value);
    }



    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        String enumName = this.data.getString(this.ymlPrefix + name);
        return Optional.of(Enum.valueOf((Class<T>) classOfT, enumName));
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.data.set(this.ymlPrefix + name, value.name());
    }


    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(!this.data.contains(this.ymlPrefix + name))
            return Optional.empty();

        try {
            YamlSerializedObject obj = new YamlSerializedObject(YamlConfiguration.loadConfigurationFromString(this.data.getString(this.ymlPrefix + name)), classOfT);
            T val = Creator.getCreator(classOfT).read(obj, classOfT);
            return Optional.of(val);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        YamlSerializedObject nested = new YamlSerializedObject(value.getClass());
        value.write(nested);
        nested.flush();
        try {
            this.data.set(this.ymlPrefix + name, nested.toString());
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        if(!this.data.contains(this.ymlPrefix + name))
            return Optional.empty();

        try {
            YamlSerializedObject obj = new YamlSerializedObject(YamlConfiguration.loadConfigurationFromString(this.data.getString(this.ymlPrefix + name)));
            return Optional.of(obj);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();

    }


    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof YamlSerializedObject))
            throw new SerializationException("Cannot serialize a non-yml object into a yml configuration");

        this.data.set(this.ymlPrefix + name, value.toString());
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        if(!this.data.contains(this.ymlPrefix + name))
            return Optional.empty();

        String codon = this.data.getString(this.ymlPrefix + name);
        if(!codon.equals(NullSerializable.CODON))
            return Optional.empty();

        return Optional.of(new NullSerializable(this.ymlPrefix + name));
    }


    @Override
    public void setNull(@NotNull String name) {
        this.data.set(this.ymlPrefix + name, NullSerializable.CODON);
    }


    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        Object read = this.data.get(this.ymlPrefix + name);
        boolean nullValue = read == null || read.toString().equals(NullSerializable.CODON);

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }


    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        return Optional.ofNullable(this.data.getIntegerList(this.ymlPrefix + name));
    }


    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        return Optional.ofNullable(this.data.getLongList(this.ymlPrefix + name));
    }


    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        return Optional.ofNullable(this.data.getDoubleList(this.ymlPrefix + name));
    }


    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        return Optional.ofNullable(this.data.getByteList(this.ymlPrefix + name));
    }


    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        return Optional.ofNullable(this.data.getStringList(this.ymlPrefix + name));
    }


    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {
        this.data.set(this.ymlPrefix + name, value);
    }


    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        if(!this.data.contains(this.ymlPrefix + name))
            return Optional.empty();

        try{
            List<SerializableObject> list = new ArrayList<>();
            String array = this.data.get(this.ymlPrefix + name).toString().substring(1);
            for(String entry : array.split("\n- ")){
                if(entry.isEmpty())
                    continue;

                if(entry.endsWith("]"))
                    entry = entry.substring(0, entry.length() - 2);

                YamlSerializedObject nested = new YamlSerializedObject(YamlConfiguration.loadConfigurationFromString(entry), clazz);
                SerializableObject obj = Creator.getCreator(clazz).read(nested);
                list.add(obj);
            }

            return Optional.of(list);

        }catch(Exception e){
            throw new SerializationException(e);
        }

    }


    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        StringBuilder str = new StringBuilder("[");
        for(var obj : value){
            YamlSerializedObject nested = new YamlSerializedObject(obj.getClass());
            obj.write(nested);
            nested.flush();

            str.append("\n- ").append(nested);
        }
        str.append("\n]");

        this.data.set(this.ymlPrefix + name, str.toString());
    }


    @Override
    public byte @NotNull [] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {
        try {
            stream.write(this.toByteArray());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        File dummy = new File("dummy.yml");
        try{
            if(!dummy.createNewFile())
                throw new SerializationException("Could not create dummy file");

            this.data.save(dummy);

            try(FileInputStream fis = new FileInputStream(dummy)){
                for(String e : IOUtils.readLines(fis, StandardCharsets.UTF_8)){
                    str.append(e).append("\n");
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            Files.delete(dummy.toPath());
        } catch (IOException e) {
            return "ERROR WHILE SERIALIZING";
        }

        return str.toString();

    }

    @Override
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        return Optional.empty();
    }
}
