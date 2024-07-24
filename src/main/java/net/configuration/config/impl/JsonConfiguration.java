package net.configuration.config.impl;

import com.google.gson.*;
import net.configuration.config.ConfigurationException;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.types.JsonSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JsonConfiguration extends FileConfiguration {

    protected JsonObject config;
    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected JsonConfiguration(File file) throws IOException {
        super(file);
        this.config = JsonParser.parseReader(new FileReader(this.file)).getAsJsonObject();
    }

    @Override
    public boolean save() {
        try(FileOutputStream fos = new FileOutputStream(this.file)){
            String json = this.gson.toJson(this.config);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reload() {
        try {
            this.config = JsonParser.parseReader(new FileReader(this.file)).getAsJsonObject();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return this.getSubMember(path).isPresent();
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsByte());
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsInt());
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsLong());
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsShort());
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsFloat());
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsDouble());
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsString().charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.update(path, String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsBoolean());
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.update(path, value);
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getSubMember(path).orElseThrow().getAsString());
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.update(path, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!(this.hasMember(path)))
            return Optional.empty();

        JsonArray elem = this.getSubMember(path).orElseThrow().getAsJsonArray();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            return Optional.of(this.getPrimitiveList(elem, classOfT));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<T> res = new ArrayList<>(elem.size());
            for(int i = 0; i < elem.size(); i++){
                JsonSerializedObject obj = new JsonSerializedObject(elem.get(i).getAsJsonObject(), classOfT);
                SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
                res.add((T) val);
            }

            return Optional.of(res);

        }else if(classOfT.isEnum()){
            return Optional.of(this.getEnumList(this.getPrimitiveList(elem, String.class), classOfT));

        }else{
            throw new ConfigurationException("Could not read list. Invalid element type " + classOfT);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getPrimitiveList(@NotNull JsonArray elem, @NotNull Class<T> classOfT){
        List<T> res = new ArrayList<>(elem.size());
        for(int i = 0; i < elem.size(); i++){
            T val = (T) this.getPrimitive(classOfT, elem.get(i).getAsJsonPrimitive());
            res.add(val);
        }

        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setList(@NotNull String path, List<T> list) {
        if(list == null || list.isEmpty() || list.get(0) == null)
            throw new ConfigurationException("Cannot write an empty list or a list with null elements");

        Class<T> classOfT = (Class<T>) list.get(0).getClass();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            this.setPrimitiveList(path, list);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            JsonArray array = new JsonArray();
            for(T val : list){
                JsonSerializedObject obj = new JsonSerializedObject(classOfT);
                ((SerializableObject) val).write(obj);
                obj.flush();
                JsonObject data = this.getDataFieldFromSerializedObject(obj);

                array.add(data);
            }

            this.config.add(path, array);

        }else if(classOfT.isEnum()){
            List<String> names = new ArrayList<>();
            for(var e : list){
                names.add(e.toString());
            }

            this.setPrimitiveList(path, names);

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!this.hasMember(path))
            return Optional.empty();

        JsonElement elem = this.getSubMember(path).orElseThrow();
        if(elem.isJsonNull()){
            return Optional.empty();
        }

        if(elem.isJsonPrimitive()){
            T val = (T) this.getPrimitive(classOfT, elem.getAsJsonPrimitive());
            return Optional.of(val);

        }else if(elem.isJsonObject()){
            JsonSerializedObject obj = new JsonSerializedObject(elem.getAsJsonObject(), classOfT);
            SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
            return Optional.of((T) val);

        }else{
            ConfigurationException e = new ConfigurationException("Not a primitive or deserializable: " + elem);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T> void set(@NotNull String path, T value) {
        Optional<JsonElement> opt = this.getSubMember(path);
        if(opt.isEmpty())
            return; //invalid path

        JsonElement elem = opt.get();
        if(elem.isJsonNull()){
            //new path entry
            if(value == null)
                this.config.add(path, elem);
            else{
                if(ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class){
                    this.updateJsonPrimitive(new JsonPrimitive(""), value);

                }else if(value instanceof SerializableObject ser){
                    this.updateSerializable(path, ser);
                }
            }

        }else if(elem.isJsonPrimitive()){
            this.updateJsonPrimitive(elem.getAsJsonPrimitive(), Objects.requireNonNullElse(value, "null"));

        }else if(elem.isJsonObject()){
            if(value == null){
                throw new ConfigurationException("Cannot set complex type to null");

            }else if(value instanceof SerializableObject ser){
                this.updateSerializable(path, ser);

            }else{
                throw new ConfigurationException("Not a primitive or serializable: " + value);
            }

        }else{
            throw new ConfigurationException("Not a primitive or serializable: " + value);
        }

    }

    /**
     * Get the {@link JsonElement} that is referenced by the given path.
     *
     * @param path A path to that object, that may contain child references.
     * @return The underlying {@link JsonElement} at the given path.
     */
    protected Optional<JsonElement> getSubMember(String path){
        if(!path.contains(".")){
            if(this.config.has(path)){
                return Optional.of(this.config.get(path));
            }else{
                //new path
                return Optional.of(JsonNull.INSTANCE);
            }
        }


        JsonElement currentObject = this.config;
        for(String sub : path.split("\\.")){
            if(currentObject.isJsonNull() || currentObject.isJsonPrimitive())
                return Optional.of(currentObject);

            if(!currentObject.getAsJsonObject().has(sub)){
                if(path.endsWith(sub)){
                    //a new path to add, so return a new json element
                    return Optional.of(JsonNull.INSTANCE);
                }else{
                    return Optional.empty();
                }
            }


            currentObject = currentObject.getAsJsonObject().get(sub);
        }

        return Optional.of(currentObject);
    }

    protected void updateJsonPrimitive(@NotNull JsonPrimitive element, @NotNull Object value){
        try {
            Field valueField = element.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(element, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void update(@NotNull String path, @NotNull Object value){
        Optional<JsonElement> opt = this.getSubMember(path);
        if(opt.isEmpty())
            return; //invalid path

        JsonElement elem = opt.get();
        JsonPrimitive prim;
        boolean add = false;
        if(elem.isJsonNull()){
            //new elem
            prim = new JsonPrimitive("");
            add = true;
        }else if(elem.isJsonPrimitive()){
            prim = elem.getAsJsonPrimitive();
        }else{
            throw new ConfigurationException("Not a primitive JSON: " + elem);
        }

        this.updateJsonPrimitive(prim, value);
        if(add)
            this.config.add(path, prim);
    }

    private <T> Object getPrimitive(@NotNull Class<T> classOfT, @NotNull JsonPrimitive elem){
        if(classOfT == String.class){
            return elem.getAsString();

        }else if(classOfT == byte.class || classOfT == Byte.class){
            return elem.getAsInt();

        }else if(classOfT == short.class || classOfT == Short.class){
            return elem.getAsInt();

        }else if(classOfT == int.class || classOfT == Integer.class){
            return elem.getAsInt();

        }else if(classOfT == long.class || classOfT == Long.class){
            return elem.getAsInt();

        }else if(classOfT == float.class || classOfT == Float.class){
            return elem.getAsInt();

        }else if(classOfT == double.class || classOfT == Double.class){
            return elem.getAsInt();

        }else if(classOfT == boolean.class || classOfT == Boolean.class){
            return elem.getAsInt();

        }else if(classOfT == char.class || classOfT == Character.class){
            return elem.getAsInt();

        }else{
            throw new UnsupportedOperationException("Primitive type required but did not get a primitive");
        }
    }

    protected <T> void setPrimitiveList(@NotNull String path, @NotNull List<T> list){
        JsonArray array = new JsonArray();
        for(T val : list){
            JsonPrimitive prim;
            if(val instanceof Character c){
                prim = new JsonPrimitive(c);

            }else if(val instanceof Boolean b){
                prim = new JsonPrimitive(b);

            }else if(val instanceof String s){
                prim = new JsonPrimitive(s);

            }else if(val instanceof Number n){
                prim = new JsonPrimitive(n);

            }else{
                throw new ConfigurationException("Not a primitive: " + val);
            }

            array.add(prim);
        }

        this.config.add(path, array);
    }

    protected void updateSerializable(@NotNull String path, @NotNull SerializableObject value){
        //new object
        JsonSerializedObject obj = new JsonSerializedObject(value.getClass());
        value.write(obj);
        obj.flush();

        JsonObject data = this.getDataFieldFromSerializedObject(obj);
        this.config.add(path, data);
    }

}
