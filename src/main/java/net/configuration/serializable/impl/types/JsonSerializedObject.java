package net.configuration.serializable.impl.types;

import com.google.gson.*;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.TupleSerializable;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class JsonSerializedObject extends AbstractSerializedObject{

    @NotNull private final JsonObject data;

    @SuppressWarnings("unused") //called via reflection API
    public JsonSerializedObject(){
        this.data = new JsonObject();
    }

    public JsonSerializedObject(@NotNull JsonObject data){
        this.data = data;
    }

    public JsonSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);

        this.data = new JsonObject();
    }

    public JsonSerializedObject(@NotNull JsonObject data, @NotNull Class<?> clazz){
        super(clazz);

        this.data = data;
    }

    @SuppressWarnings("unused")
    public JsonSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);

        this.loadClassFields();
        this.data = new JsonObject();
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsByte());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Byte> getByte() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsByte());
        }

        return Optional.empty();
    }

    @Override
    public void setByte(@NotNull String name, byte value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setByte(byte value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsShort());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Short> getShort() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsShort());
        }

        return Optional.empty();
    }

    @Override
    public void setShort(@NotNull String name, short value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setShort(short value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsInt());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> getInt() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsInt());
        }

        return Optional.empty();
    }

    @Override
    public void setInt(@NotNull String name, int value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setInt(int value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsLong());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Long> getLong() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsLong());
        }

        return Optional.empty();
    }

    @Override
    public void setLong(@NotNull String name, long value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setLong(long value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsFloat());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Float> getFloat() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsFloat());
        }

        return Optional.empty();
    }

    @Override
    public void setFloat(@NotNull String name, float value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setFloat(float value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsDouble());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Double> getDouble() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsDouble());
        }

        return Optional.empty();
    }

    @Override
    public void setDouble(@NotNull String name, double value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setDouble(double value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsString().charAt(0));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Character> getChar() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsString().charAt(0));
        }

        return Optional.empty();
    }

    @Override
    public void setChar(@NotNull String name, char value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setChar(char value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsString());
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getString() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsString());
        }

        return Optional.empty();
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setString(@NotNull String value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsBoolean());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean() {
        String name = this.getFieldName(Byte.class);
        if(this.data.has(name)){
            return Optional.of(this.data.get(name).getAsBoolean());
        }

        return Optional.empty();
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.data.addProperty(name, value);
    }

    @Override
    public void setBoolean(boolean value) {
        String name = this.getFieldName(Byte.class);
        this.data.addProperty(name, value);
    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull String name, @NotNull Class<T> classOfT) {
        return this.getArrayHelper(name, classOfT);
    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull Class<T> classOfT) {
        String name = this.getFieldName(Byte.class);
        return this.getArray(name, classOfT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setArray(@NotNull String name, T @NotNull [] array) {
        if(array[0] == null)
            throw new SerializationException("Null value inside array");

        Class<T> elementType = (Class<T>) array[0].getClass(); //never primitive always wrapper
        this.setArray(name, array, elementType);
    }

    @Override
    public <T> void setArray(T @NotNull [] array) {
        String name = this.getFieldName(Byte.class);
        this.setArray(name, array);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        if(this.data.has(name)){
            try {
                T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, this.data.get(name).getAsString());
                return Optional.of(val);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    @Override
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull Class<? extends Enum<?>> classOfT) {
        String name = this.getFieldName(classOfT);
        return getEnum(name, classOfT);
    }


    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.data.addProperty(name, value.name());
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull T value) {
        String name = this.getFieldName(value.getClass());
        this.data.addProperty(name, value.name());
    }



    //######################################### complex objects ############################################


    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(!data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.empty();

        JsonObject jsonData = read.getAsJsonObject();
        try {
            SerializedObject serialized = new JsonSerializedObject(jsonData, classOfT);
            T val = Creator.getCreator(classOfT).read(serialized, classOfT);
            return Optional.of(val);

        } catch (SerializationException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(Class<T> classOfT) {
        String name = this.getFieldName(classOfT);
        return this.getSerializable(name, classOfT);
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        JsonSerializedObject nested = new JsonSerializedObject(value.getClass());
        value.write(nested);
        this.data.add(name, JsonParser.parseString(nested.toString()));
    }

    @Override
    public void setSerializable(@NotNull SerializableObject value) {
        String name = this.getFieldName(value.getClass());
        JsonSerializedObject nested = new JsonSerializedObject(value.getClass());
        value.write(nested);
        this.data.add(name, JsonParser.parseString(nested.toString()));
    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        if(!data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.empty();

        JsonObject jsonData = read.getAsJsonObject();
        return Optional.of(new JsonSerializedObject(jsonData));
    }

    @Override
    public Optional<SerializedObject> get() {
        String name = this.getFieldName(SerializedObject.class);
        return this.get(name);
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof JsonSerializedObject))
            throw new SerializationException("Cannot write a non-json object into a json object");

        this.data.add(name, JsonParser.parseString(value.toString()));
    }

    @Override
    public void set(@NotNull SerializedObject value) {
        String name = this.getFieldName(SerializedObject.class);
        this.set(name, value);
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        if(!this.data.has(name) || !this.data.get(name).isJsonPrimitive())
            return Optional.empty();

        String codon = this.data.get(name).getAsString();
        if(!codon.equals(NullSerializable.CODON))
            return Optional.empty();

        return Optional.of(new NullSerializable(name));
    }

    @Override
    public Optional<SerializableObject> getNull() {
        String name = this.getFieldName(Class.class);
        return this.getNull(name);
    }

    @Override
    public void setNull(@NotNull String name) {
        this.data.addProperty(name, NullSerializable.CODON);
    }

    @Override
    public void setNull() {
        this.setNull(this.getFieldName(Class.class));
    }

    @Override
    public boolean isNextNull(@NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        String name = this.getFieldName(type);
        return this.isNextNull(name, type);
    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        JsonElement read = this.data.get(name);

        boolean nullValue;
        if(read == null || read.isJsonNull()){
            nullValue = true;
        }else if(read.isJsonPrimitive()){
            nullValue = read.getAsString().equals(NullSerializable.CODON);
        }else{
            nullValue = false;
        }

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }

    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<Integer> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            list.add(jsonData.get(i).getAsInt());
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Collection<Integer>> getIntList() {
        String name = this.getFieldName(List.class);
        return this.getIntList(name);
    }

    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(listType != Integer.class)
                throw new IllegalArgumentException(field + " is not a int list");

        }

        JsonArray list = new JsonArray();
        for(int e : value){
            list.add(e);
        }
        this.data.add(name, list);
    }

    @Override
    public void setIntList(@NotNull Collection<Integer> value) {
        String name = this.getFieldName(List.class);
        this.setIntList(name, value);
    }

    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<Long> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            list.add(jsonData.get(i).getAsLong());
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Collection<Long>> getLongList() {
        String name = this.getFieldName(List.class);
        return this.getLongList(name);
    }

    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(listType != Long.class)
                throw new IllegalArgumentException(field + " is not a long list");

        }

        JsonArray list = new JsonArray();
        for(long e : value){
            list.add(e);
        }
        this.data.add(name, list);
    }

    @Override
    public void setLongList(@NotNull Collection<Long> value) {
        String name = this.getFieldName(List.class);
        this.setLongList(name, value);
    }

    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<Double> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            list.add(jsonData.get(i).getAsDouble());
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Collection<Double>> getDoubleList() {
        String name = this.getFieldName(List.class);
        return this.getDoubleList(name);
    }

    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(listType != Double.class)
                throw new IllegalArgumentException(field + " is not a double list");

        }

        JsonArray list = new JsonArray();
        for(double e : value){
            list.add(e);
        }
        this.data.add(name, list);
    }

    @Override
    public void setDoubleList(@NotNull Collection<Double> value) {
        String name = this.getFieldName(List.class);
        this.setDoubleList(name, value);
    }

    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<Byte> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            list.add(jsonData.get(i).getAsByte());
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Collection<Byte>> getByteList() {
        String name = this.getFieldName(List.class);
        return this.getByteList(name);
    }

    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(listType != Byte.class)
                throw new IllegalArgumentException(field + " is not a byte list");

        }

        JsonArray list = new JsonArray();
        for(byte e : value){
            list.add(e);
        }
        this.data.add(name, list);
    }

    @Override
    public void setByteList(@NotNull Collection<Byte> value) {
        String name = this.getFieldName(List.class);
        this.setByteList(name, value);
    }

    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<String> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            list.add(jsonData.get(i).getAsString());
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Collection<String>> getStringList() {
        String name = this.getFieldName(List.class);
        return this.getStringList(name);
    }

    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(listType != String.class)
                throw new IllegalArgumentException(field + " is not a string list");

        }

        JsonArray list = new JsonArray();
        for(String e : value){
            list.add(e);
        }
        this.data.add(name, list);
    }

    @Override
    public void setStringList(@NotNull Collection<String> value) {
        String name = this.getFieldName(List.class);
        this.setStringList(name, value);
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement read = this.data.get(name);
        if(read.isJsonNull())
            return Optional.of(new ArrayList<>());

        JsonArray jsonData = read.getAsJsonArray();
        List<SerializableObject> list = new ArrayList<>(jsonData.size());
        for(int i = 0; i < jsonData.size(); i++){
            JsonObject entry = jsonData.get(i).getAsJsonObject();
            try {
                SerializedObject serialized = new JsonSerializedObject(entry, clazz);
                var obj = Creator.getCreator(clazz).read(serialized);
                list.add(obj);

            } catch (SerializationException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.of(list);

    }

    @Override
    public Optional<Collection<SerializableObject>> getList(Class<? extends SerializableObject> clazz) {
        String name = this.getFieldName(List.class);
        return this.getList(name, clazz);
    }

    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        Optional<Field> fieldOpt = this.getField(name);
        if(fieldOpt.isPresent()){
            Field field = fieldOpt.get();
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];
            if(!SerializableObject.class.isAssignableFrom(listType))
                throw new IllegalArgumentException("Field " + field + " is not a fitting list, Type mismatch");

        }

        JsonArray jsonData = new JsonArray();
        for(var obj : value){
            JsonSerializedObject nested = new JsonSerializedObject(obj.getClass());
            obj.write(nested);
            jsonData.add(JsonParser.parseString(nested.toString()));
        }
        this.data.add(name, jsonData);

    }

    @Override
    public void setList(@NotNull Collection<? extends SerializableObject> value) {
        String name = this.getFieldName(List.class);
        this.setList(name, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K,V> Optional<Map<K, V>> getMap(@NotNull String name, @NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        var listOpt = this.getList(name, TupleSerializable.class);
        if(listOpt.isEmpty())
            return Optional.empty();

        Map<K, V> map = new HashMap<>();
        for(var e : listOpt.get()){
            if(e instanceof TupleSerializable tuple){
                Object key = tuple.getKey(SerializableType.JSON, keyClass).orElseThrow();
                Object value = tuple.getValue(SerializableType.JSON, valueClass).orElseThrow();
                map.put((K) key, (V) value);
            }else{
                return Optional.empty();
            }
        }

        return Optional.of(map);
    }

    @Override
    public <K,V> Optional<Map<K, V>> getMap(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        String name = this.getFieldName(Map.class);
        return this.getMap(name, keyClass, valueClass);
    }

    @Override
    public <K,V> void setMap(@NotNull String name, @NotNull Map<K, V> value) {
        List<TupleSerializable> list = new ArrayList<>();
        for(var entrySet : value.entrySet()){
            var ser = new TupleSerializable(SerializableType.JSON, entrySet.getKey(), entrySet.getValue());
            list.add(ser);
        }

        this.setList(name, list);
    }

    @Override
    public <K,V> void setMap(@NotNull Map<K, V> value) {
        String name = this.getFieldName(Map.class);
        this.setMap(name, value);
    }

    @Override
    public byte @NotNull [] toByteArray() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return data.toString();
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
    @SuppressWarnings("unchecked")
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(!this.data.has(name))
            return Optional.empty();

        JsonElement elem = this.data.get(name);
        if(elem.isJsonNull())
            return Optional.empty();

        if(elem.isJsonObject()){
            SerializedObject obj = new JsonSerializedObject(elem.getAsJsonObject(), Object.class);
            var opt = obj.getSerializable((Class<? extends SerializableObject>) classOfT);
            if(opt.isPresent())
                return Optional.of(opt.get());

        }else if(elem.isJsonArray()){
            throw new UnsupportedOperationException("Could not extract a list as a raw object.");

        }else if(elem.isJsonPrimitive()){
            return Optional.ofNullable(this.extractPrimitive(elem.getAsJsonPrimitive()));
        }

        return Optional.empty();
    }

    private Object extractPrimitive(@NotNull JsonPrimitive json){
        if(json.isString()){
            return json.getAsString();

        }else if(json.isNumber()){
            return json.getAsNumber();

        }else if(json.isBoolean()) {
            return json.getAsBoolean();

        }

        return null;
    }

}
