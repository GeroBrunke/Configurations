package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.NullSerializable;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Logger;

public class PropertiesSerializedObject extends AbstractSerializedObject{

    private final Properties data;
    private static final char NESTED_BEGIN = '{';
    private static final char NESTED_END = '}';

    @SuppressWarnings("unused") //called via reflection API
    public PropertiesSerializedObject(@NotNull Class<?> clazz){
        this("", clazz);
    }

    public PropertiesSerializedObject(@NotNull Class<?> clazz, @NotNull String data) throws IOException {
        super(clazz);
        this.xmlPrefix = clazz.getSimpleName() + "_";

        this.data = new Properties();
        String serializedData = data.substring(1);
        this.deserializeStringToProperties(serializedData.substring(0, serializedData.length() - 1), this.data);

    }

    public PropertiesSerializedObject(@NotNull String prevPrefix, @NotNull Class<?> clazz) {
        super(clazz);
        this.xmlPrefix = prevPrefix + clazz.getSimpleName() + "_";
        this.data = new Properties();
    }

    public PropertiesSerializedObject(@NotNull String prevPrefix, @NotNull Properties properties, @NotNull Class<?> forClass){
        super(forClass);
        this.xmlPrefix = prevPrefix + forClass.getSimpleName() + "_";
        this.data = properties;
    }

    public PropertiesSerializedObject(@NotNull String prevPrefix, @NotNull Properties data){
        this.xmlPrefix = prevPrefix;
        this.data = data;
    }

    @SuppressWarnings("unused")
    public PropertiesSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
        this.data = new Properties();
        this.xmlPrefix = clazz.getSimpleName() + "_";
    }

    @SuppressWarnings("unused")
    protected PropertiesSerializedObject(){
        super();
        this.data = new Properties();
        this.xmlPrefix = "";
    }


    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        return Optional.of(Byte.parseByte(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setByte(@NotNull String name, byte value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        return Optional.of(Short.parseShort(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setShort(@NotNull String name, short value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }
    @Override

    public Optional<Integer> getInt(@NotNull String name) {
        return Optional.of(Integer.parseInt(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setInt(@NotNull String name, int value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        return Optional.of(Long.parseLong(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setLong(@NotNull String name, long value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        return Optional.of(Float.parseFloat(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setFloat(@NotNull String name, float value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        return Optional.of(Double.parseDouble(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setDouble(@NotNull String name, double value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        return Optional.of(this.data.getProperty(this.xmlPrefix + name).charAt(0));
    }

    @Override
    public void setChar(@NotNull String name, char value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        return Optional.of(this.data.getProperty(this.xmlPrefix + name));
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.data.setProperty(this.xmlPrefix + name, value);
    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        return Optional.of(Boolean.parseBoolean(this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.data.setProperty(this.xmlPrefix + name, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        return Optional.of(Enum.valueOf((Class<T>) classOfT, this.data.getProperty(this.xmlPrefix + name)));
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.data.setProperty(this.xmlPrefix + name, value.name());
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(!this.data.containsKey(this.xmlPrefix + name))
            return Optional.empty();

        try{
            Properties prop = new Properties();
            String serializedData = this.data.getProperty(this.xmlPrefix + name).substring(1);
            this.deserializeStringToProperties(serializedData.substring(0, serializedData.length() - 1), prop);


            PropertiesSerializedObject obj = new PropertiesSerializedObject(this.xmlPrefix, prop, classOfT);
            T val = Creator.getCreator(classOfT).read(obj);
            return Optional.of(val);

        }catch(Exception e){
            throw new SerializationException(e);
        }
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        PropertiesSerializedObject nested = new PropertiesSerializedObject(this.xmlPrefix, value.getClass());
        value.write(nested);
        nested.flush();

        this.data.setProperty(this.xmlPrefix + name, nested.toString());

    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        try {
            Properties prop = new Properties();
            String serializedData = this.data.getProperty(this.xmlPrefix + name).substring(1);
            this.deserializeStringToProperties(serializedData.substring(0, serializedData.length() - 1), prop);

            return Optional.of(new PropertiesSerializedObject(this.xmlPrefix, prop));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof PropertiesSerializedObject)){
            throw new SerializationException("Cannot serialize a non-properties object into a properties object");
        }

        this.data.setProperty(this.xmlPrefix + name, value.toString());
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        String val = this.data.getProperty(this.xmlPrefix + name);
        if(val == null)
            return Optional.empty();

        if(val.equals(NullSerializable.CODON)){
            return Optional.of(new NullSerializable(name));
        }

        return Optional.empty();
    }

    @Override
    public void setNull(@NotNull String name) {
        this.data.setProperty(this.xmlPrefix + name, NullSerializable.CODON);
    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        Object read = this.data.get(this.xmlPrefix + name);
        boolean nullValue = read == null || read.toString().equals(NullSerializable.CODON);

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }


    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        if(!this.data.containsKey(this.xmlPrefix + name))
            return Optional.empty();

        try{
            List<SerializableObject> list = new ArrayList<>();
            String array = this.getString(name).orElse("");
            if(array.isBlank())
                return Optional.empty();

            array = array.substring(1, array.length() - 1);
            for(String entry : array.split("\\[<br>]")){
                if(entry.isEmpty())
                    continue;


                Properties prop = new Properties();
                this.deserializeStringToProperties(entry.substring(1, entry.length() - 1), prop);
                PropertiesSerializedObject nested = new PropertiesSerializedObject(this.xmlPrefix, prop);
                nested.setForClass(clazz);
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
            PropertiesSerializedObject nested = new PropertiesSerializedObject(obj.getClass());
            obj.write(nested);
            nested.flush();

            str.append("[<br>]").append(nested);
        }
        str.append("]");
        this.setString(name, str.toString());

    }

    @Override
    public byte @NotNull [] toByteArray() {
        return new byte[0];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(String.valueOf(NESTED_BEGIN));
        int max = this.data.size();
        int i = 0;
        for(var e : this.data.entrySet()){
            str.append(e.getKey()).append("=").append(e.getValue()).append((i < max - 1) ? "<br>" : "");
            i++;
        }
        str.append(NESTED_END);

        return str.toString();
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {
        try {
            this.data.store(stream, null);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(this.data.containsKey(this.xmlPrefix + name)){
            return Optional.of(this.data.get(this.xmlPrefix + name));
        }

        return Optional.empty();
    }

    private void deserializeStringToProperties(@NotNull String data, @NotNull Properties prop) throws IOException {
        List<String> nestedStrings = new LinkedList<>();
        int l = 0;
        int id = 0;
        int nestedIdx = data.indexOf(String.valueOf(NESTED_BEGIN), l);
        while(l < data.length() && nestedIdx != -1){
            String nested = this.readNested(data.substring(nestedIdx));
            l += nested.length();
            nestedIdx = data.indexOf(String.valueOf(NESTED_BEGIN), l);
            nestedStrings.add(nested);
            data = data.replace(nested, String.valueOf(id++));
            nestedIdx -= nested.length();
        }

        data = data.replace("<br>", "\n");
        for(int i = 0; i < nestedStrings.size(); i++){
            data = data.replace(String.valueOf(NESTED_BEGIN) + i + NESTED_END, NESTED_BEGIN + nestedStrings.get(i) + NESTED_END);
        }

        prop.load(new StringReader(data));

    }

    private String readNested(@NotNull String data){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < data.length(); i++){
            char c = data.charAt(i);
            if(c == NESTED_BEGIN)
                return readNested(data.substring(i+1));
            else if(c == NESTED_END)
                return s.toString();
            else s.append(c);
        }

        return s.toString();
    }
}
