package net.configuration.serializable.impl;

import com.google.gson.JsonParser;
import net.configuration.main.Main;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.types.*;
import org.apache.commons.lang3.ClassUtils;
import org.jdom2.input.DOMBuilder;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Optional;

public class TupleSerializable implements SerializableObject {

    private static final String INVALID = "INVALID";

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<TupleSerializable> CREATOR = src -> {
        TupleSerializable ser = new TupleSerializable();
        return ser.read(src);
    };

    private String keyVal;
    private String value;

    private TupleSerializable(){} //Hide implicit

    public TupleSerializable(@NotNull SerializableType type, @NotNull Object key, @NotNull Object value){
        this.initKey(type, key);
        this.initValue(type, value);
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString("keyVal", this.keyVal);
        dest.setString("value", this.value);
    }

    @Override
    public @NotNull TupleSerializable read(@NotNull SerializedObject src) {
        this.keyVal = src.getString("keyVal").orElse(INVALID);
        this.value = src.getString("value").orElse(INVALID);
        return this;
    }

    @NotNull
    public Optional<Object> getKey(@NotNull SerializableType type, @NotNull Class<?> clazz){
        return this.deserializeString(type, clazz, this.keyVal);
    }

    @NotNull
    public Optional<Object> getValue(@NotNull SerializableType type, @NotNull Class<?> clazz){
        return this.deserializeString(type, clazz, this.value);
    }

    private void initKey(@NotNull SerializableType type, @NotNull Object key){
        if(ClassUtils.isPrimitiveOrWrapper(key.getClass()) || key.getClass() == String.class){
            //primitive key
            this.keyVal = String.valueOf(key);

        }else if(key instanceof SerializableObject keySer){
            //complex key
            SerializedObject ser = type.createEmpty(key.getClass());
            keySer.write(ser);
            ser.flush();
            this.keyVal = ser.toString();

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
            ser.flush();
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

            case BYTE -> {
                return new ByteSerializedObject(ByteBuffer.wrap(ByteSerializedObject.createArrayFromString(data)), clazz);
            }

            case TEXT -> {
                return new TextSerializedObject(data.substring(1, data.length() - 1), clazz);
            }

            case XML -> {
                try{
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                    org.w3c.dom.Document w3cDocument = documentBuilder.parse(new InputSource(new StringReader(data)));

                    return new XmlSerializedObject(new DOMBuilder().build(w3cDocument), clazz);
                }catch(Exception e){
                    throw new SerializationException(e);
                }
            }

            case SQL -> {
                return new SQLSerializedObject(Main.getDefaultConnection(), data, clazz);
            }

            default -> throw new UnsupportedOperationException("Implement me");
        }
    }
}
