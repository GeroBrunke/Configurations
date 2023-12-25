package net.configuration.serializable.api;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;

public interface SerializedObject {

    /**
     * Get the class of the object stored in this {@link SerializedObject} instance.
     *
     * @return The type of object stored in this object.
     */
    Class<?> getForClass();

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

    //############################## complex values ################################

    /**
     * Read the enum mapped to the given name from this object.
     *
     * @param name The name of the field this enum is mapped to.
     * @return The enum value mapped to this field name or an empty optional, if no enum was found.
     */
    <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<T> classOfT);

    /**
     * Read the enum mapped to the next given field name from this object.
     *
     * @return The enum value mapped to the next field name or an empty optional, if no enum was found.
     */
    <T extends Enum<T>> Optional<T> getEnum(@NotNull Class<T> classOfT);

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
     * @param name The name of the field this {@link SerializableObject} is mapped to.
     * @return The {@link SerializableObject} mapped to this field name or an empty optional, if no such object was found.
     */
    Optional<SerializableObject> getSerializable(@NotNull String name);

    /**
     * Read the {@link SerializableObject} mapped to the next given field name from this object.
     *
     * @return The {@link SerializableObject} value mapped to the next field name or an empty optional, if no such object
     * was found.
     */
    Optional<SerializableObject> getSerializable();

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
    default Optional<Object> getObject(@NotNull String name){
        //TODO: implement me
        return Optional.empty();
    }

    /**
     * Read the object mapped to the next given field name from this object.
     *
     * @return The object mapped to the next field name or an empty optional, if no such object was found.
     */
    default Optional<Object> getObject(){
        //TODO: implement me
        return Optional.empty();
    }

    /**
     * Write the given object mapped to the given name into this object.
     *
     * @param name The field name this object is mapped to.
     * @param value The object to write.
     */
    default void setObject(@NotNull String name, @NotNull Object value){
        //TODO: implement me
    }

    /**
     * Write the given object into this object. Since there is no field name given, this method creates a
     * dummy name like "object-{id}" where id is the next free id for object names in this object.
     *
     * @param value The value to write.
     */
    default void setObject(@NotNull Object value){
        //TODO: implement me
    }

    /**
     * Read the null object mapped to the given name from this object.
     *
     * @param name The name of the field this null object is mapped to.
     * @return The null object mapped to this field name or an empty optional, if the object is NOT NULL.
     */
    Optional<SerializedObject> getNull(@NotNull String name);

    /**
     * Read the null object mapped to the next given field name from this object.
     *
     * @return The null object mapped to the next field name or an empty optional, if the object is NOT NULL.
     */
    Optional<SerializedObject> getNull();

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
     * @param name The name of the field this collection is mapped to.
     * @return The collection mapped to this field name or an empty optional, if no collection was found.
     */
    Optional<Collection<SerializableObject>> getList(@NotNull String name);

    /**
     * Read the collection of {@link SerializableObject} mapped to the next given field name from this object.
     *
     * @return The collection mapped to the next field name or an empty optional, if no collection was found.
     */
    Optional<Collection<SerializableObject>> getList();

    /**
     * Write the given collection of {@link SerializableObject} mapped to the given name into this object.
     *
     * @param name The field name this collection is mapped to.
     * @param value The collection to write.
     */
    void setList(@NotNull String name, @NotNull Collection<SerializableObject> value);

    /**
     * Write the given collection of {@link SerializableObject} value into this object. Since there is no field name
     * given, this method creates a dummy name like "list-{id}" where id is the next free id for collection names
     * in this object.
     *
     * @param value The value to write.
     */
    void setList(@NotNull Collection<SerializableObject> value);

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
     * @param array The raw array data to read.
     * @return An instance of {@link SerializedObject} containing all the data from the array.
     */
    @NotNull static SerializedObject createFromByteArray(byte @NotNull[] array){
        //TODO: Implement me
        return null;
    }

    /**
     * Create an instance of {@link SerializedObject} based on data read from the given stream.
     *
     * @param stream The stream to read the data from.
     * @return An instance of {@link SerializedObject} containing all the read data from the stream.
     */
    @NotNull static SerializedObject readFromStream(@NotNull InputStream stream){
        //TODO: implement me
        return null;
    }
}
