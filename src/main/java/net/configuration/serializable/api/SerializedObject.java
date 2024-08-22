package net.configuration.serializable.api;

import com.google.gson.JsonParser;
import net.configuration.main.Main;
import net.configuration.serializable.impl.MapSerializable;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jdom2.input.DOMBuilder;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public interface SerializedObject extends ObjectSerializer{


    /**
     * Get the class of the object stored in this {@link SerializedObject} instance.
     *
     * @return The type of object stored in this object.
     */
    @NotNull Optional<Class<?>> getForClass();

    <T extends SerializableObject> void setForClass(@NotNull Class<T> clazz);

    //############################## primitive values ################################

    /**
     * Read the byte mapped to the given name from this object.
     *
     * @param name The name of the field this byte is mapped to.
     * @return The byte value mapped to this field name or an empty optional, if no byte was found.
     */
    Optional<Byte> getByte(@NotNull String name);

    /**
     * Read the byte mapped to the next given field name from this object.
     *
     * @return The byte value mapped to the next field name or an empty optional, if no byte was found.
     */
    Optional<Byte> getByte();

    /**
     * Write the given byte mapped to the given name into this object.
     *
     * @param name The field name this byte is mapped to.
     * @param value The byte value to write.
     */
    void setByte(@NotNull String name, byte value);

    /**
     * Write the given byte value into this object. Since there is no field name given, this method creates a
     * dummy name like "byte-{id}" where id is the next free id for byte names in this object.
     *
     * @param value The value to write.
     */
    void setByte(byte value);

    /**
     * Read the short mapped to the given name from this object.
     *
     * @param name The name of the field this short is mapped to.
     * @return The short value mapped to this field name or an empty optional, if no short was found.
     */
    Optional<Short> getShort(@NotNull String name);

    /**
     * Read the short mapped to the next given field name from this object.
     *
     * @return The short value mapped to the next field name or an empty optional, if no short was found.
     */
    Optional<Short> getShort();

    /**
     * Write the given short mapped to the given name into this object.
     *
     * @param name The field name this short is mapped to.
     * @param value The short value to write.
     */
    void setShort(@NotNull String name, short value);

    /**
     * Write the given short value into this object. Since there is no field name given, this method creates a
     * dummy name like "short-{id}" where id is the next free id for short names in this object.
     *
     * @param value The value to write.
     */
    void setShort(short value);

    /**
     * Read the int mapped to the given name from this object.
     *
     * @param name The name of the field this int is mapped to.
     * @return The int value mapped to this field name or an empty optional, if no int was found.
     */
    Optional<Integer> getInt(@NotNull String name);

    /**
     * Read the int mapped to the next given field name from this object.
     *
     * @return The int value mapped to the next field name or an empty optional, if no int was found.
     */
    Optional<Integer> getInt();

    /**
     * Write the given int mapped to the given name into this object.
     *
     * @param name The field name this int is mapped to.
     * @param value The int value to write.
     */
    void setInt(@NotNull String name, int value);

    /**
     * Write the given int value into this object. Since there is no field name given, this method creates a
     * dummy name like "int-{id}" where id is the next free id for int names in this object.
     *
     * @param value The value to write.
     */
    void setInt(int value);

    /**
     * Read the long mapped to the given name from this object.
     *
     * @param name The name of the field this long is mapped to.
     * @return The long value mapped to this field name or an empty optional, if no long was found.
     */
    Optional<Long> getLong(@NotNull String name);

    /**
     * Read the long mapped to the next given field name from this object.
     *
     * @return The long value mapped to the next field name or an empty optional, if no long was found.
     */
    Optional<Long> getLong();

    /**
     * Write the given long mapped to the given name into this object.
     *
     * @param name The field name this long is mapped to.
     * @param value The long value to write.
     */
    void setLong(@NotNull String name, long value);

    /**
     * Write the given long value into this object. Since there is no field name given, this method creates a
     * dummy name like "long-{id}" where id is the next free id for long names in this object.
     *
     * @param value The value to write.
     */
    void setLong(long value);

    /**
     * Read the float mapped to the given name from this object.
     *
     * @param name The name of the field this float is mapped to.
     * @return The float value mapped to this field name or an empty optional, if no float was found.
     */
    Optional<Float> getFloat(@NotNull String name);

    /**
     * Read the float mapped to the next given field name from this object.
     *
     * @return The float value mapped to the next field name or an empty optional, if no float was found.
     */
    Optional<Float> getFloat();

    /**
     * Write the given float mapped to the given name into this object.
     *
     * @param name The field name this float is mapped to.
     * @param value The float value to write.
     */
    void setFloat(@NotNull String name, float value);

    /**
     * Write the given float value into this object. Since there is no field name given, this method creates a
     * dummy name like "float-{id}" where id is the next free id for float names in this object.
     *
     * @param value The value to write.
     */
    void setFloat(float value);

    /**
     * Read the double mapped to the given name from this object.
     *
     * @param name The name of the field this double is mapped to.
     * @return The double value mapped to this field name or an empty optional, if no double was found.
     */
    Optional<Double> getDouble(@NotNull String name);

    /**
     * Read the double mapped to the next given field name from this object.
     *
     * @return The double value mapped to the next field name or an empty optional, if no double was found.
     */
    Optional<Double> getDouble();

    /**
     * Write the given double mapped to the given name into this object.
     *
     * @param name The field name this double is mapped to.
     * @param value The double value to write.
     */
    void setDouble(@NotNull String name, double value);

    /**
     * Write the given double value into this object. Since there is no field name given, this method creates a
     * dummy name like "double-{id}" where id is the next free id for double names in this object.
     *
     * @param value The value to write.
     */
    void setDouble(double value);

    /**
     * Read the char mapped to the given name from this object.
     *
     * @param name The name of the field this char is mapped to.
     * @return The char value mapped to this field name or an empty optional, if no char was found.
     */
    Optional<Character> getChar(@NotNull String name);

    /**
     * Read the char mapped to the next given field name from this object.
     *
     * @return The char value mapped to the next field name or an empty optional, if no char was found.
     */
    Optional<Character> getChar();

    /**
     * Write the given char mapped to the given name into this object.
     *
     * @param name The field name this char is mapped to.
     * @param value The char value to write.
     */
    void setChar(@NotNull String name, char value);

    /**
     * Write the given char value into this object. Since there is no field name given, this method creates a
     * dummy name like "char-{id}" where id is the next free id for char names in this object.
     *
     * @param value The value to write.
     */
    void setChar(char value);

    /**
     * Read the string mapped to the given name from this object.
     *
     * @param name The name of the field this string is mapped to.
     * @return The string value mapped to this field name or an empty optional, if no string was found.
     */
    Optional<String> getString(@NotNull String name);

    /**
     * Read the string mapped to the next given field name from this object.
     *
     * @return The string value mapped to the next field name or an empty optional, if no string was found.
     */
    Optional<String> getString();

    /**
     * Write the given string mapped to the given name into this object.
     *
     * @param name The field name this string is mapped to.
     * @param value The string value to write.
     */
    void setString(@NotNull String name, @NotNull String value);

    /**
     * Write the given string value into this object. Since there is no field name given, this method creates a
     * dummy name like "string-{id}" where id is the next free id for string names in this object.
     *
     * @param value The value to write.
     */
    void setString(@NotNull String value);

    /**
     * Read the boolean mapped to the given name from this object.
     *
     * @param name The name of the field this boolean is mapped to.
     * @return The boolean value mapped to this field name or an empty optional, if no boolean was found.
     */
    Optional<Boolean> getBoolean(@NotNull String name);

    /**
     * Read the boolean mapped to the next given field name from this object.
     *
     * @return The boolean value mapped to the next field name or an empty optional, if no boolean was found.
     */
    Optional<Boolean> getBoolean();

    /**
     * Write the given boolean mapped to the given name into this object.
     *
     * @param name The field name this boolean is mapped to.
     * @param value The boolean value to write.
     */
    void setBoolean(@NotNull String name, boolean value);

    /**
     * Write the given boolean value into this object. Since there is no field name given, this method creates a
     * dummy name like "boolean-{id}" where id is the next free id for boolean names in this object.
     *
     * @param value The value to write.
     */
    void setBoolean(boolean value);

    /**
     * Get the array mapped to the given name in this object.
     *
     * @param name The field name the array is mapped to.
     * @param classOfT The type of the elements in the array.
     * @return The array value mapped to the given name or an empty optional, if no array was found.
     */
    <T> Optional<T[]> getArray(@NotNull String name, @NotNull Class<T> classOfT);

    /**
     * Read the array mapped to the next given field name from this object.
     *
     * @return The array mapped to the next field name or an empty optional, if no array was found.
     */
    <T> Optional<T[]> getArray(@NotNull Class<T> classOfT);

    /**
     * Write the given array into this object mapped to the given name if the given generic type is serializable.
     *
     * @param name The name of the field this array is mapped to.
     * @param array The array to write.
     */
    <T> void setArray(@NotNull String name, T @NotNull[] array);

    /**
     * Write the given array into this object. Since there is no field name given, this method creates a
     * dummy name like "array-{id}" where id is the next free id for boolean names in this object.
     *
     * @param array The array to write.
     */
    <T> void setArray(T @NotNull[] array);

    //############################## complex values ################################

    /**
     * Read the enum mapped to the given name from this object.
     *
     * @param name The name of the field this enum is mapped to.
     * @return The enum value mapped to this field name or an empty optional, if no enum was found.
     */
    <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT);

    /**
     * Read the enum mapped to the next given field name from this object.
     *
     * @return The enum value mapped to the next field name or an empty optional, if no enum was found.
     */
    <T extends Enum<T>> Optional<T> getEnum(@NotNull Class<? extends Enum<?>> classOfT);

    /**
     * Write the given enum mapped to the given name into this object.
     *
     * @param name The field name this enum is mapped to.
     * @param value The enum value to write.
     */
    <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value);

    /**
     * Write the given enum value into this object. Since there is no field name given, this method creates a
     * dummy name like "enum-{id}" where id is the next free id for enum names in this object.
     *
     * @param value The value to write.
     */
    <T extends Enum<T>> void setEnum(@NotNull T value);

    /**
     * Read the {@link SerializableObject} mapped to the given name from this object.
     *
     * @param name     The name of the field this {@link SerializableObject} is mapped to.
     * @param classOfT The class type of the read object.
     * @return The {@link SerializableObject} mapped to this field name or an empty optional, if no such object was found.
     */
    <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT);

    /**
     * Read the {@link SerializableObject} mapped to the next given field name from this object.
     *
     * @return The {@link SerializableObject} value mapped to the next field name or an empty optional, if no such object
     * was found.
     */
    <T extends SerializableObject> Optional<T> getSerializable(Class<T> classOfT);

    /**
     * Write the given {@link SerializableObject} mapped to the given name into this object.
     *
     * @param name The field name this object is mapped to.
     * @param value The object to write.
     */
    void setSerializable(@NotNull String name, @NotNull SerializableObject value);

    /**
     * Write the given {@link SerializableObject} into this object. Since there is no field name given, this method creates a
     * dummy name like "object-{id}" where id is the next free id for {@link SerializableObject} names in this object.
     *
     * @param value The value to write.
     */
    void setSerializable(@NotNull SerializableObject value);

    /**
     * Read the {@link SerializedObject} mapped to the given name from this object.
     *
     * @param name The name of the field this object is mapped to.
     * @return The {@link SerializedObject} mapped to this field name or an empty optional, if no such object was found.
     */
    Optional<SerializedObject> get(@NotNull String name);

    /**
     * Read the {@link SerializedObject} mapped to the next given field name from this object.
     *
     * @return The {@link SerializedObject} mapped to the next field name or an empty optional, if no such object was found.
     */
    Optional<SerializedObject> get();

    /**
     * Write the given {@link SerializedObject} mapped to the given name into this object.
     *
     * @param name The field name this object is mapped to.
     * @param value The object to write.
     */
    void set(@NotNull String name, @NotNull SerializedObject value);

    /**
     * Write the given {@link SerializedObject} into this object. Since there is no field name given, this method creates a
     * dummy name like "serialized-{id}" where id is the next free id for {@link SerializedObject} names in this object.
     *
     * @param value The value to write.
     */
    void set(@NotNull SerializedObject value);

    /**
     * Read the object mapped to the given name from this object.
     *
     * @param name The name of the field this object is mapped to.
     * @return The object mapped to this field name or an empty optional, if no such object was found.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default <T> Optional<T> getObject(@NotNull String name, @NotNull Class<T> classOfT){
        if(this.isNextNull(name, classOfT))
            return Optional.empty();

        if(classOfT.isEnum()){
            Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) classOfT;
            Optional<Enum> opt = this.getEnum(name, e);
            if(opt.isPresent()){
                T val = (T) SerializationHelper.toEnum(opt.get());
                return Optional.of(val);
            }

        }else if(classOfT.isArray()){
            Optional<?> opt = this.getArray(name, classOfT.componentType());
            if(opt.isPresent()){
                T array = (T) opt.get();
                return Optional.of(array);
            }

        }else if(SerializedObject.class.isAssignableFrom(classOfT)){
            Optional<SerializedObject> opt = this.get();
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            Optional<? extends SerializableObject> opt = this.getSerializable(name, (Class<? extends SerializableObject>) classOfT);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else {
            return getCollectionFromObject(name, classOfT);
        }

        return Optional.empty();
    }

    /**
     * Only used internally. Use {@link SerializedObject#getObject(String, Class)} instead.
     */
    @SuppressWarnings("unchecked")
    default <T> Optional<T> getCollectionFromObject(@NotNull String name, @NotNull Class<T> classOfT){
        if(Map.class.isAssignableFrom(classOfT)){
            Optional<MapSerializable> opt = this.getSerializable(name, MapSerializable.class);
            if(opt.isPresent()){
                Map<?,?> val = opt.get().getMap(Object.class, Object.class);
                return Optional.of((T) val);
            }

        }else if(List.class.isAssignableFrom(classOfT)){
            try{
                Optional<String> typeOpt = this.getString("listElementType");
                if(typeOpt.isPresent()){
                    Class<?> type = Class.forName(typeOpt.get());
                    return (Optional<T>) SerializationHelper.getList(name, this, type);
                }
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }

            return Optional.empty(); //cannot read a list that was not set via the setObject() Method.

        }else if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            Optional<Object> opt = SerializationHelper.getPrimitive(name, this, classOfT);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Object.class){
            Optional<Object> opt = this.getRawObject(name, classOfT);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else{
            throw new IllegalArgumentException("Cannot deserialize object for class " + classOfT.getName());
        }

        return Optional.empty();
    }


    /**
     * Write the given object mapped to the given name into this object.
     *
     * @param name The field name this object is mapped to.
     * @param value The object to write.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <T> void setObject(@NotNull String name, T value) {
        if(value == null){
            this.setNull(name);
            return;
        }

        Class<T> classOfT = (Class<T>) value.getClass();
        if(classOfT.isEnum()){
            this.setEnum(name, (Enum) SerializationHelper.toEnum(value));

        }else if(classOfT.isArray()){
            int size = Array.getLength(value);
            Object[] array = new Object[size];
            for(int i = 0; i < size; i++){
                array[i] = Array.get(value, i);
            }

            this.setArray(name, array);

        }else if(value instanceof SerializedObject sobj){
            this.set(name, sobj);

        }else if(value instanceof SerializableObject ser){
            this.setSerializable(name, ser);

        }else if(value instanceof Map<?,?> map){
            if(map.isEmpty()){ //empty list are marked as null
                this.setNull(name);
                return;
            }

            SerializableType type = SerializableType.fromImplementationClass(this.getClass());
            if(type == null)
                throw new SerializationException("Could not deduce serializable type from class " + this.getClass());

            MapSerializable mapSer = new MapSerializable(type, map);
            this.setSerializable(name, mapSer);

        }else if(value instanceof List<?> list){
            if(list.isEmpty() || list.get(0) == null){ //empty list are marked as null
                this.setNull(name);
                return;
            }

            Class<?> listType = list.get(0).getClass();
            this.setString("listElementType", listType.getName());
            SerializationHelper.setList(name, this, listType, list);

        }else if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            SerializationHelper.setPrimitive(name, this, value, classOfT);

        }else{
            throw new IllegalArgumentException("Cannot serialize object " + value);
        }
    }

    /**
     * Read the null object mapped to the given name from this object.
     *
     * @param name The name of the field this null object is mapped to.
     * @return The null object mapped to this field name or an empty optional, if the object is NOT NULL.
     */
    Optional<SerializableObject> getNull(@NotNull String name);

    /**
     * Read the null object mapped to the next given field name from this object.
     *
     * @return The null object mapped to the next field name or an empty optional, if the object is NOT NULL.
     */
    Optional<SerializableObject> getNull();

    /**
     * Write the given null object mapped to the given name into this object.
     *
     * @param name The field name this object is mapped to.
     */
    void setNull(@NotNull String name);

    /**
     * Write the given null object into this object. Since there is no field name given, this method creates a
     * dummy name like "null-{id}" where id is the next free id for null object names in this object.
     *
     */
    void setNull();


    /**
     * Check if the value mapped to the given name stores a null serialized value.
     * @return If the next value is a null value.
     */
    boolean isNextNull(@NotNull String name, @NotNull Class<?> type);


    //############################## List values ################################

    /**
     * Read the int collection mapped to the given name from this object.
     *
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Integer>> getIntList(@NotNull String name);

    /**
     * Read the int collection mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Integer>> getIntList();

    /**
     * Write the given int collection mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setIntList(@NotNull String name, @NotNull Collection<Integer> value);

    /**
     * Write the given int collection value into this object. Since there is no field name given, this method creates a
     * dummy name like "intList-{id}" where id is the next free id for int collection names in this object.
     *
     * @param value The value to write.
     */
    void setIntList(@NotNull Collection<Integer> value);

    /**
     * Read the long collection mapped to the given name from this object.
     *
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Long>> getLongList(@NotNull String name);

    /**
     * Read the long collection mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Long>> getLongList();

    /**
     * Write the given long collection mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setLongList(@NotNull String name, @NotNull Collection<Long> value);

    /**
     * Write the given long collection value into this object. Since there is no field name given, this method creates a
     * dummy name like "longList-{id}" where id is the next free id for long collection names in this object.
     *
     * @param value The value to write.
     */
    void setLongList(@NotNull Collection<Long> value);

    /**
     * Read the double collection mapped to the given name from this object.
     *
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Double>> getDoubleList(@NotNull String name);

    /**
     * Read the double collection mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Double>> getDoubleList();

    /**
     * Write the given double collection mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setDoubleList(@NotNull String name, @NotNull Collection<Double> value);

    /**
     * Write the given int collection value into this object. Since there is no field name given, this method creates a
     * dummy name like "doubleList-{id}" where id is the next free id for double collection names in this object.
     *
     * @param value The value to write.
     */
    void setDoubleList(@NotNull Collection<Double> value);

    /**
     * Read the byte collection mapped to the given name from this object.
     *
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Byte>> getByteList(@NotNull String name);

    /**
     * Read the byte collection mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<Byte>> getByteList();

    /**
     * Write the given byte collection mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setByteList(@NotNull String name, @NotNull Collection<Byte> value);

    /**
     * Write the given byte collection value into this object. Since there is no field name given, this method creates a
     * dummy name like "byteList-{id}" where id is the next free id for byte collection names in this object.
     *
     * @param value The value to write.
     */
    void setByteList(@NotNull Collection<Byte> value);

    /**
     * Read the string collection mapped to the given name from this object.
     *
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<String>> getStringList(@NotNull String name);

    /**
     * Read the string collection mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<String>> getStringList();

    /**
     * Write the given string collection mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setStringList(@NotNull String name, @NotNull Collection<String> value);

    /**
     * Write the given string collection value into this object. Since there is no field name given, this method creates a
     * dummy name like "stringList-{id}" where id is the next free id for string collection names in this object.
     *
     * @param value The value to write.
     */
    void setStringList(@NotNull Collection<String> value);

    /**
     * Read the collection of {@link SerializableObject} mapped to the given name from this object.
     *
     * @param name  The name of the field this collection is mapped to.
     * @param clazz The type of the list entries.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz);

    /**
     * Read the collection of {@link SerializableObject} mapped to the next given field name from this object.
     *
     * @param clazz The type of the list entries.
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<SerializableObject>> getList(Class<? extends SerializableObject> clazz);

    /**
     * Write the given collection of {@link SerializableObject} mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value);

    /**
     * Write the given collection of {@link SerializableObject} value into this object. Since there is no field name
     * given, this method creates a dummy name like "list-{id}" where id is the next free id for collection names
     * in this object.
     *
     * @param value The value to write.
     */
    void setList(@NotNull Collection<? extends SerializableObject> value);

    /**
     * Read the map of serializable objects mapped to the given name from this object.
     *
     * @param name The name of the field this map is mapped to.
     * @param keyClass The type of the key elements in the map (has to be serializable).
     * @param valueClass The type of the values in the map (has to be serializable).
     * @return The map mapped to this field name or an empty optional, if no map was found.
     */
    <K,V> Optional<Map<K, V>> getMap(@NotNull String name, @NotNull Class<K> keyClass, @NotNull Class<V> valueClass);

    /**
     * Read the map of serializable objects mapped to the next given field name from this object.
     *
     * @param keyClass The type of the key elements in the map (has to be serializable).
     * @param valueClass The type of the values in the map (has to be serializable).
     * @return The map mapped to the next field name or an empty optional, if no map was found.
     */
    <K, V> Optional<Map<K, V>> getMap(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass);

    /**
     * Write the given map of serializable objects mapped to the given name into this object.
     *
     * @param name The field name this map is mapped to.
     * @param value The map to write.
     */
    <K,V> void setMap(@NotNull String name, @NotNull Map<K, V> value);

    /**
     * Write the given map of serializable objects value into this object. Since there is no field name
     * given, this method creates a dummy name like "map-{id}" where id is the next free id for map names
     * in this object.
     *
     * @param value The value to write.
     */
    <K,V> void setMap(@NotNull Map<K, V> value);

    //############################## void/static methods ################################

    /**
     * Flush all the data inside the object. After the object is flushed it can be read again. Note:<br>
     * You can only write to an object until it is flushed and only read after it is flushed.
     */
    void flush();

    /**
     * Wrap the current data inside this object into a byte array.
     *
     * @return A byte[] containing all the data of this object.
     */
    byte @NotNull[] toByteArray();

    /**
     * Write the current data into the given stream. Note that this object does not have to be flushed
     * before it can be written.
     *
     * @param stream The stream to write this object to.
     */
    void writeToStream(@NotNull OutputStream stream);

    /**
     * Create an instance of {@link SerializedObject} based on the data provided in the given array.
     *
     * @param type The type of the serialized object.
     * @param forClass The type of the object that is serialized in the given byte array.
     * @param array The raw array data to read.
     * @return An instance of {@link SerializedObject} containing all the data from the array.
     */
    @NotNull static SerializedObject createFromByteArray(@NotNull SerializableType type, @NotNull Class<?> forClass, byte @NotNull[] array){
        try{
            String strData = new String(array, StandardCharsets.UTF_8);
            SerializedObject obj = null;
            switch(type){
                case BYTE -> obj = new ByteSerializedObject(ByteBuffer.wrap(array), forClass);
                case JSON -> obj = new JsonSerializedObject(JsonParser.parseString(strData).getAsJsonObject(), forClass);
                case YAML -> obj = new YamlSerializedObject(YamlConfiguration.loadConfigurationFromString(strData), forClass);
                case TEXT -> obj = new TextSerializedObject(strData.substring(1, strData.length() - 1), forClass);
                case SQL -> obj = new SQLSerializedObject(Main.getDefaultConnection(), strData, forClass);

                case PROPERTIES -> {
                    Properties prop = new Properties();
                    prop.load(new StringReader(strData));
                    obj = new PropertiesSerializedObject("", prop, forClass);
                }

                case XML -> {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                    org.w3c.dom.Document w3cDocument = documentBuilder.parse(new InputSource(new StringReader(strData)));

                    obj = new XmlSerializedObject(new DOMBuilder().build(w3cDocument), forClass);
                }
            }

            if(obj == null)
                throw new SerializationException("Could not read the given data");

            return obj;
        }catch(Exception e){
            throw new SerializationException(e);
        }
    }

    /**
     * Create an instance of {@link SerializedObject} based on data read from the given stream.
     *
     * @param type The type of the serialized object.
     * @param forClass The type of the object that is serialized in the given stream.
     * @param stream The stream to read the data from.
     * @return An instance of {@link SerializedObject} containing all the read data from the stream.
     */
    @NotNull static SerializedObject readFromStream(@NotNull SerializableType type, @NotNull Class<?> forClass, @NotNull InputStream stream){
        try {
            byte[] buffer = new byte[ByteSerializedObject.BUFFER_SIZE];
            int read = IOUtils.read(stream, buffer);
            if(read <= 0)
                throw new SerializationException("Read empty stream");

            byte[] data = new byte[read];
            System.arraycopy(buffer, 0, data, 0, read);
            return createFromByteArray(type, forClass, data);

        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
