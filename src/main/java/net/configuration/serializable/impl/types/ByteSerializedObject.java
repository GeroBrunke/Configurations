package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.SerializationHelper;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class ByteSerializedObject extends AbstractSerializedObject {

    public static final int BUFFER_SIZE = (int) Math.pow(2, 20); //max read 1MB
    protected final Map<String, String> data = new HashMap<>();
    private ByteBuffer buffer;
    private boolean flushed = false;

    public ByteSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
    }

    public ByteSerializedObject(@NotNull ByteBuffer buffer) {
        this.load(buffer);
    }

    public ByteSerializedObject(@NotNull ByteBuffer buffer, @NotNull Class<?> clazz){
        super(clazz);
        this.load(buffer);
    }

    @SuppressWarnings("unused")
    public ByteSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
    }

    @SuppressWarnings("unused")
    protected ByteSerializedObject(){
        super();
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Byte.valueOf(this.data.get(name)));
    }

    @Override
    public void setByte(@NotNull String name, byte value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Short.valueOf(this.data.get(name)));
    }

    @Override
    public void setShort(@NotNull String name, short value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Integer.valueOf(this.data.get(name)));
    }

    @Override
    public void setInt(@NotNull String name, int value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Long.valueOf(this.data.get(name)));
    }

    @Override
    public void setLong(@NotNull String name, long value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Float.valueOf(this.data.get(name)));
    }

    @Override
    public void setFloat(@NotNull String name, float value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(Double.valueOf(this.data.get(name)));
    }

    @Override
    public void setDouble(@NotNull String name, double value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(this.data.get(name).charAt(0));
    }

    @Override
    public void setChar(@NotNull String name, char value) {
        this.data.put(name, String.valueOf(value));
    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(this.data.get(name));
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.data.put(name, value);
    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        return Optional.of(this.data.get(name).equals("1"));
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.data.put(name, value ? "1" : "0");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        try {
            T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, this.data.get(name));
            return Optional.of(val);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.data.put(name, value.name());
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        Optional<ByteSerializedObject> opt = createFromStr(this.getString(name).orElse(""));
        if(opt.isEmpty())
            return Optional.empty();

        ByteSerializedObject nested = opt.get();
        T val = Creator.getCreator(classOfT).read(nested);

        return Optional.of(val);
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        ByteSerializedObject nested = new ByteSerializedObject(value.getClass());
        value.write(nested);
        nested.flush();
        String str = this.arrayToStr(nested.toByteArray());

        this.setString(name, str);

    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        if(!this.data.containsKey(name)){
            return Optional.empty();
        }

        Optional<ByteSerializedObject> opt = createFromStr(this.getString(name).orElse(""));
        if(opt.isEmpty())
            return Optional.empty();

        return Optional.of(opt.get());
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof ByteSerializedObject))
            throw new SerializationException("Cannot write a non-byte object into a byte[]");

        String str = this.arrayToStr(value.toByteArray());
        this.setString(name, str);
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        String val = this.data.get(name);
        if(val == null)
            return Optional.empty();

        if(val.equals(NullSerializable.CODON)){
            return Optional.of(new NullSerializable(name));
        }

        return Optional.empty();
    }

    @Override
    public void setNull(@NotNull String name) {
        this.data.put(name, NullSerializable.CODON);
    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        Object read = this.data.get(name);
        boolean nullValue = read == null || read.toString().equals(NullSerializable.CODON);

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        List<SerializableObject> list = new ArrayList<>();
        String array = this.getString(name).orElse("");
        if(array.isBlank())
            return Optional.empty();

        array = array.substring(1, array.length()-1);
        for(String s : array.split(";")){
            Optional<ByteSerializedObject> opt = createFromStr(s);
            if(opt.isEmpty())
                continue;

            ByteSerializedObject nested = opt.get();
            SerializableObject obj = Creator.getCreator(clazz).read(nested);
            list.add(obj);
        }

        return Optional.of(list);
    }


    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        StringBuilder s = new StringBuilder("{");
        for(var e : value){
            ByteSerializedObject nested = new ByteSerializedObject(e.getClass());
            e.write(nested);
            nested.flush();

            String str = this.arrayToStr(nested.toByteArray());
            s.append(str).append(";");
        }
        s = new StringBuilder(s.substring(0, s.length() - 1) + "}");

        this.setString(name, s.toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        String elem = this.data.get(name);
        if(elem.equals(NullSerializable.CODON))
            return Optional.empty();

        if(elem.startsWith("[") && elem.endsWith("]")){ //nested byte array
            Optional<ByteSerializedObject> opt = createFromStr(elem);
            if(opt.isEmpty())
                return Optional.empty();

            ByteSerializedObject nested = opt.get();
            Optional<?> o = nested.getSerializable(name, (Class<? extends SerializableObject>) classOfT);
            if(o.isPresent())
                return Optional.of(o.get());

        }else {
            return Optional.ofNullable(SerializationHelper.extractPrimitive(elem, classOfT));
        }

        return Optional.empty();
    }

    @Override
    public void flush() {
        super.flush();

        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        for(var e : this.data.entrySet()){
            byte[] entry = (e.getKey() + "{;}" + e.getValue()).getBytes(StandardCharsets.UTF_8);
            buffer.putInt(entry.length);
            buffer.put(entry);
        }

        //Trim the byte array to the current size to avoid unused memory
        int pos = this.buffer.position();
        if(pos != 0){
            byte[] flushedData = new byte[pos];
            for(int i = 0; i < pos; i++){
                flushedData[i] = this.buffer.get(i);
            }
            this.buffer = ByteBuffer.wrap(flushedData);
            this.flushed = true;
        }

    }

    @Override
    public String toString() {
        if(!flushed)
            flush();

        flushed = false;
        return arrayToStr(buffer.array());
    }

    @Override
    public byte @NotNull [] toByteArray() {
        if(!this.flushed)
            this.flush();

        flushed = false;
        return this.buffer.array();
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {
        try {
            byte[] bytes = this.toByteArray();
            stream.write(bytes);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private String arrayToStr(byte[] data){
        return Arrays.toString(data);
    }

    private void load(ByteBuffer data){
        this.data.clear();

        while(data.position() < data.array().length){
            int length = data.getInt();
            byte[] mapEntry = new byte[length];
            for(int i = 0; i < length; i++){
                mapEntry[i] = data.get();
            }

            String[] e = new String(mapEntry, StandardCharsets.UTF_8).split("\\{;}");
            if(e.length != 2)
                throw new SerializationException("Invalid map entry");

            String key = e[0];
            String value = e[1];
            this.data.put(key, value);
        }
    }

    private Optional<ByteSerializedObject> createFromStr(@NotNull String str){
        if(str.isBlank())
            return Optional.empty();

        return Optional.of(new ByteSerializedObject(ByteBuffer.wrap(createArrayFromString(str))));
    }

    public static byte[] createArrayFromString(@NotNull String str){
        String[] d = str.substring(1, str.length() -1).split(", ");
        byte[] bytes = new byte[d.length];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = Byte.parseByte(d[i]);
        }

        return bytes;
    }
}
