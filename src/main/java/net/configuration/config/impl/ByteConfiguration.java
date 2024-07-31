package net.configuration.config.impl;

import net.configuration.config.ConfigurationException;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.ByteSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ByteConfiguration extends FileConfiguration {

    protected Map<String, String> config;

    @SuppressWarnings("unused") // called via reflection API
    protected ByteConfiguration(File file) throws IOException {
        this(file, true);
    }

    protected ByteConfiguration(File file, boolean read) throws IOException {
        super(file);

        if(read){
            byte[] bytes = Files.readAllBytes(file.toPath());
            this.config = ByteSerializedObject.load(ByteBuffer.wrap(bytes));
        }
    }

    @Override
    public boolean save() {
        try(FileOutputStream fout = new FileOutputStream(this.file)){
            fout.write(this.toByteArray());
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reload() {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            this.config = ByteSerializedObject.load(ByteBuffer.wrap(bytes));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return config.containsKey(path);
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Byte.valueOf(this.config.get(path)));
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Integer.valueOf(this.config.get(path)));
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Long.valueOf(this.config.get(path)));
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Short.valueOf(this.config.get(path)));
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Float.valueOf(this.config.get(path)));
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Double.valueOf(this.config.get(path)));
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.get(path).charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Boolean.valueOf(this.config.get(path)));
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.config.put(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.config.get(path));
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.config.put(path, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!(this.hasMember(path)))
            return Optional.empty();

        String elem = this.getString(path).orElseThrow();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            return Optional.of(this.getPrimitiveList(elem, classOfT));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<T> res = new ArrayList<>();
            String[] data = elem.split("<br>");
            for(String e : data){
                ByteSerializedObject obj = new ByteSerializedObject(ByteBuffer.wrap(ByteSerializedObject.createArrayFromString(e)), classOfT);
                SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
                res.add((T) val);
            }

            return Optional.of(res);

        }else if(classOfT.isEnum()){
            List<String> names = this.getPrimitiveList(elem, String.class);
            return Optional.of(this.getEnumList(names, classOfT));

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
            this.setString(path, this.convertPrimitiveList(list));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            StringBuilder str = new StringBuilder();
            for(var e : list){
                ByteSerializedObject obj = new ByteSerializedObject(classOfT);
                ((SerializableObject) e).write(obj);
                obj.flush();

                str.append("<br>").append(obj);
            }
            str = new StringBuilder(str.substring(4));
            this.setString(path, str.toString());

        }else if(classOfT.isEnum()){
            this.setEnumList(path, list);

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!this.hasMember(path))
            return Optional.empty();

        String elem = this.getString(path).orElseThrow();
        if(elem.equalsIgnoreCase("null"))
            return Optional.empty();

        //read a valid object
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            Object prim = Objects.requireNonNull(SerializationHelper.extractPrimitive(elem, classOfT));
            return Optional.of((T) prim);

        }else if(List.class.isAssignableFrom(classOfT)){
            throw new ConfigurationException("Cannot get a list this way. Use getList(..) instead.");

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            ByteSerializedObject obj = new ByteSerializedObject(ByteBuffer.wrap(ByteSerializedObject.createArrayFromString(elem)), classOfT);
            SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
            return Optional.of((T) val);

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
            ByteSerializedObject obj = new ByteSerializedObject(classOfT);
            ((SerializableObject) value).write(obj);
            obj.flush();

            this.setString(path, obj.toString());

        }else if(classOfT.isEnum()){
            this.setString(path, value.toString());

        }else if(classOfT.isArray()){
            this.setArray(path, (Object[]) value);

        }else{
            throw new ConfigurationException("Not a serializable object: " + classOfT);
        }
    }

    private byte[] toByteArray(){
        ByteBuffer buffer = ByteBuffer.allocate(ByteSerializedObject.BUFFER_SIZE);
        for(var e : this.config.entrySet()){
            byte[] entry = (e.getKey() + "{;}" + e.getValue()).getBytes(StandardCharsets.UTF_8);
            buffer.putInt(entry.length);
            buffer.put(entry);
        }

        int pos = buffer.position();
        if(pos != 0){
            byte[] flushedData = new byte[pos];
            for(int i = 0; i < pos; i++){
                flushedData[i] = buffer.get(i);
            }
            buffer = ByteBuffer.wrap(flushedData);
            return buffer.array();
        }

        return buffer.array();

    }

    protected String convertPrimitiveList(@NotNull List<?> list){
        StringBuilder entry = new StringBuilder();
        for(var e : list){
            entry.append(", ").append(e);
        }
        entry = new StringBuilder(entry.substring(2));

        return entry.toString();
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getPrimitiveList(@NotNull String elem, @NotNull Class<T> classOfT){
        String[] d = elem.split(", ");
        List<T> res = new ArrayList<>();
        for(String e : d){
            res.add((T) SerializationHelper.extractPrimitive(e, classOfT));
        }

        return res;
    }

    protected void setEnumList(@NotNull String path, @NotNull List<?> list){
        List<String> names = new ArrayList<>();
        for(var e : list){
            Enum<?> en = (Enum<?>) e;
            names.add(en.name());
        }
        this.setString(path, this.convertPrimitiveList(names));
    }

}
