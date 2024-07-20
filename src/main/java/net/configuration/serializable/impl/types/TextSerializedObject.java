package net.configuration.serializable.impl.types;


import net.configuration.serializable.api.SerializationException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class TextSerializedObject extends ByteSerializedObject{

    @SuppressWarnings("unused")
    public TextSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
    }

    public TextSerializedObject(@NotNull String buffer, @NotNull Class<?> forClass){
        //create byte Array from str
        super(a(buffer), forClass);
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
    public String toString() {
        return this.data.toString();
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

    @NotNull
    private static ByteBuffer a(String raw){
        Map<String, String> map = deserializeStringToMap(raw);
        return convertMapToBuffer(map);
    }

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

    private static Map<String, String> deserializeStringToMap(@NotNull String data){
        List<String> nestedStrings = new LinkedList<>();
        int l = 0;
        int id = 0;
        int nestedIdx = data.indexOf("[", l);
        while(l < data.length() && nestedIdx != -1){
            String nested = readNested(data.substring(nestedIdx));
            l += nested.length();
            nestedIdx = data.indexOf("]", l);
            nestedStrings.add(nested);
            data = data.replace(nested, String.valueOf(id++));
            nestedIdx -= nested.length();
        }
        data = data.substring(1, data.length() - 1);

        //Split at ', ' and create a map from the data
        Map<String, String> map = new HashMap<>();
        String prevKey = "";
        int idx = 0;
        for(String entry : data.split(", ")){
            if(entry.contains("=")){
                String[] e = entry.split("=");
                String key = e[0];
                String value = e[1];
                if(value.equals("[" + idx + "]")){
                    value = "[" + nestedStrings.get(idx++) + "]";
                }


                map.put(key, value);
                prevKey = key;

            }else{
                //currently reading a serialized list, so add them to the previous key
                map.put(prevKey, map.get(prevKey) + ", " + entry);
            }
        }

        return map;

    }

    private static String readNested(@NotNull String data){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < data.length(); i++){
            char c = data.charAt(i);
            if(c == '[')
                return readNested(data.substring(i+1));
            else if(c == ']')
                return s.toString();
            else s.append(c);
        }

        return s.toString();
    }
}
