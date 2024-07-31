package net.configuration.config.impl;

import net.configuration.config.ConfigurationException;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.TextSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TextConfiguration extends ByteConfiguration {

    protected TextConfiguration(File file) throws IOException {
        super(file, false);

        this.load();
    }

    @Override
    public boolean save() {
        try(FileOutputStream fout = new FileOutputStream(this.file)){
            fout.write(this.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean reload() {
        try {
            this.load();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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
            String[] data = elem.split("\\[br]");
            for(String e : data){
                TextSerializedObject obj = new TextSerializedObject(e.substring(1, e.length() - 1), classOfT);
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
                TextSerializedObject obj = new TextSerializedObject(classOfT);
                ((SerializableObject) e).write(obj);
                obj.flush();

                str.append("[br]").append(obj);
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
            TextSerializedObject obj = new TextSerializedObject(elem.substring(1, elem.length() - 1), classOfT);
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
            TextSerializedObject obj = new TextSerializedObject(classOfT);
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

    @Override
    public String toString() {
        if(this.config == null)
            return "";

        StringBuilder str = new StringBuilder("{");
        int max = this.config.size();
        int i = 0;
        for(var e : this.config.entrySet()){
            str.append(e.getKey()).append(":").append(e.getValue()).append((i < max - 1) ? "<br>" : "");
            i++;
        }
        str.append("}");

        return str.toString();
    }

    private void load() throws IOException {
        String data = Files.readString(this.file.toPath(), StandardCharsets.UTF_8);
        if(!data.isEmpty())
            data = data.substring(1, data.length() - 1);

        this.config = TextSerializedObject.deserializeString(data);
    }
}
