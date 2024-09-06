package net.configuration.serializable.impl.types;


import net.configuration.config.ConfigurationException;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.SerializationHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class TextSerializedObject extends ByteSerializedObject{

    private static final char NESTED_BEGIN = '{';
    private static final char NESTED_END = '}';

    @SuppressWarnings("unused")
    public TextSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
    }

    public TextSerializedObject(@NotNull String buffer, @NotNull Class<?> forClass){
        //create byte Array from str
        super(convertMapToBuffer(deserializeString(buffer)), forClass);
    }

    public TextSerializedObject(@NotNull String buffer){
        //create byte Array from str
        super(convertMapToBuffer(deserializeString(buffer)));
    }

    @SuppressWarnings("unused")
    public TextSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
    }

    @SuppressWarnings("unused")
    protected TextSerializedObject(){
        super();
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        TextSerializedObject nested = new TextSerializedObject(value.getClass());
        value.write(nested);
        nested.flush();

        this.setString(name, nested.toString());
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        Optional<String> opt = this.getString(name);
        if(opt.isEmpty())
            return Optional.empty();

        String data = opt.get();
        TextSerializedObject nested = new TextSerializedObject(data.substring(1, data.length() - 1), classOfT);
        Creator<T> creator = Creator.getCreator(classOfT);
        T val = creator.read(nested);

        return Optional.of(val);
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof TextSerializedObject)){
            throw new SerializationException("Cannot write a non-text object into a text format");
        }

        String s = value.toString();
        this.setString(name, s.substring(1, s.length() - 1));

    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        Optional<String> opt = this.getString(name);
        if(opt.isEmpty())
            return Optional.empty();


        return Optional.of(new TextSerializedObject(opt.get()));
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        Optional<String> opt = this.getString(name);
        if(opt.isEmpty())
            return Optional.empty();

        String raw = opt.get();
        List<SerializableObject> list = new ArrayList<>();
        for(String elem : raw.substring(1, raw.length() - 1).split("\\[br]")){
            TextSerializedObject nested = new TextSerializedObject(elem.substring(1, elem.length() - 1), clazz);
            SerializableObject obj = Creator.getCreator(clazz).read(nested);
            list.add(obj);
        }


        return Optional.of(list);
    }

    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        StringBuilder str = new StringBuilder();
        for(SerializableObject elem : value){
            if(elem == null)
                continue;

            TextSerializedObject nested = new TextSerializedObject(elem.getClass());
            elem.write(nested);
            nested.flush();

            str.append("[br]").append(nested);
        }
        str = new StringBuilder("[" + str.substring(4) + "]");

        this.setString(name, str.toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        String elem = this.data.get(name);
        if(elem.equals(NullSerializable.CODON))
            return Optional.empty();

        if(elem.startsWith(String.valueOf(NESTED_BEGIN)) && elem.endsWith(String.valueOf(NESTED_END))){ //nested byte array
            TextSerializedObject nested = new TextSerializedObject(elem, classOfT);
            Optional<?> o = nested.getSerializable(name, (Class<? extends SerializableObject>) classOfT);
            if(o.isPresent())
                return Optional.of(o.get());

        }else {
            return Optional.ofNullable(SerializationHelper.extractPrimitive(elem, classOfT));
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("{");
        int max = this.data.size();
        int i = 0;
        for(var e : this.data.entrySet()){
            str.append(e.getKey()).append(":").append(e.getValue()).append((i < max - 1) ? "<br>" : "");
            i++;
        }
        str.append("}");

        return str.toString();
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {
        try (OutputStreamWriter writer = new OutputStreamWriter(stream)){
            writer.write(this.toString());
            writer.flush();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }


    /**
     * Convert the raw string representation of a text serialized object into the name - value pairs stored inside the
     * string.
     *
     * @param data The raw string representation of the text serialized object.
     * @return The name - value pairs stored in the string.
     */
    @NotNull
    public static Map<String, String> deserializeString(@NotNull String data) {
        if(data.isEmpty())
            return new HashMap<>();

        List<Nested> nestedStrings = new LinkedList<>();
        int left = -1;
        Deque<Character> stack = new ConcurrentLinkedDeque<>();
        for(int i = 0; i < data.length(); i++){
            char c = data.charAt(i);
            if(c == NESTED_BEGIN){
                if(stack.isEmpty())
                    left = i+1;

                stack.push(c);


            }else if(c == NESTED_END){
                if(stack.isEmpty())
                    throw new ConfigurationException("Error while deserializing: Stack already empty");

                stack.pop();
                if(stack.isEmpty()){
                    Nested rep = new Nested(left, i, NESTED_BEGIN + data.substring(left, i) + NESTED_END);
                    nestedStrings.add(rep);
                }
            }
        }

        if(!stack.isEmpty())
            throw new ConfigurationException("Error while deserializing: Stack not empty");

        //convert to Map
        Map<String, String> map = new HashMap<>();
        int idx = 0;
        for(Nested n : nestedStrings){
            data = data.replace(n.value, NESTED_BEGIN + String.valueOf(idx++) + NESTED_END);
        }


        int i = 0;
        for(String e : data.split("<br>")){
            String[] d = e.split(":");
            String key = d[0];
            String value = d[1];

            if(value.startsWith(String.valueOf(NESTED_BEGIN)) && value.endsWith(String.valueOf(NESTED_END)))
                value = nestedStrings.get(i++).value;

            map.put(key, value);
        }

        return map;

    }

    /**
     * Convert the current name - value map into a byte[] representation.
     *
     * @param map The map to convert.
     * @return The byte[] representation of the map.
     */
    private static ByteBuffer convertMapToBuffer(@NotNull Map<String, String> map){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        for(var e : map.entrySet()){
            byte[] entry = (e.getKey() + "{;}" + e.getValue()).getBytes(StandardCharsets.UTF_8);
            buffer.putInt(entry.length);
            buffer.put(entry);
        }

        //Trim the byte array to the current size to avoid unused memory
        int pos = buffer.position();
        if(pos != 0){
            byte[] flushedData = new byte[pos];
            for(int i = 0; i < pos; i++){
                flushedData[i] = buffer.get(i);
            }
            buffer = ByteBuffer.wrap(flushedData);
        }

        return buffer;
    }

    /**
     * Helper record to save nested strings of objects inside the raw string. Saves the beginning and end index of nested strings
     * in the original string and its string value.
     */
    private record Nested (int left, int right, @NotNull String value){ }
}
