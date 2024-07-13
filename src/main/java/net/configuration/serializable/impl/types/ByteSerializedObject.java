package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class ByteSerializedObject extends AbstractSerializedObject {

    public static final int BUFFER_SIZE = (int) Math.pow(2, 20); //max read 1MB

    public ByteSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
    }

    public ByteSerializedObject(@NotNull ByteBuffer buffer, @NotNull Class<?> clazz){

    }

    public ByteSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
    }

    @SuppressWarnings("unused")
    protected ByteSerializedObject(){
        super();
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Byte> getByte() {
        return Optional.empty();
    }

    @Override
    public void setByte(@NotNull String name, byte value) {

    }

    @Override
    public void setByte(byte value) {

    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Short> getShort() {
        return Optional.empty();
    }

    @Override
    public void setShort(@NotNull String name, short value) {

    }

    @Override
    public void setShort(short value) {

    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getInt() {
        return Optional.empty();
    }

    @Override
    public void setInt(@NotNull String name, int value) {

    }

    @Override
    public void setInt(int value) {

    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Long> getLong() {
        return Optional.empty();
    }

    @Override
    public void setLong(@NotNull String name, long value) {

    }

    @Override
    public void setLong(long value) {

    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Float> getFloat() {
        return Optional.empty();
    }

    @Override
    public void setFloat(@NotNull String name, float value) {

    }

    @Override
    public void setFloat(float value) {

    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getDouble() {
        return Optional.empty();
    }

    @Override
    public void setDouble(@NotNull String name, double value) {

    }

    @Override
    public void setDouble(double value) {

    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Character> getChar() {
        return Optional.empty();
    }

    @Override
    public void setChar(@NotNull String name, char value) {

    }

    @Override
    public void setChar(char value) {

    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getString() {
        return Optional.empty();
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {

    }

    @Override
    public void setString(@NotNull String value) {

    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean() {
        return Optional.empty();
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {

    }

    @Override
    public void setBoolean(boolean value) {

    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull String name, @NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T> void setArray(@NotNull String name, T @NotNull [] array) {

    }

    @Override
    public <T> void setArray(T @NotNull [] array) {

    }

    @Override
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        return Optional.empty();
    }


    @Override
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull Class<? extends Enum<?>> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {

    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull T value) {

    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(Class<T> classOfT) {
        return Optional.empty();
    }


    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {

    }

    @Override
    public void setSerializable(@NotNull SerializableObject value) {

    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<SerializedObject> get() {
        return Optional.empty();
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {

    }

    @Override
    public void set(@NotNull SerializedObject value) {

    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<SerializableObject> getNull() {
        return Optional.empty();
    }

    @Override
    public void setNull(@NotNull String name) {

    }

    @Override
    public void setNull() {

    }

    @Override
    public boolean isNextNull(@NotNull Class<?> type) {
        return false;
    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        return false;
    }

    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Integer>> getIntList() {
        return Optional.empty();
    }

    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {

    }

    @Override
    public void setIntList(@NotNull Collection<Integer> value) {

    }

    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Long>> getLongList() {
        return Optional.empty();
    }

    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {

    }

    @Override
    public void setLongList(@NotNull Collection<Long> value) {

    }

    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Double>> getDoubleList() {
        return Optional.empty();
    }

    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {

    }

    @Override
    public void setDoubleList(@NotNull Collection<Double> value) {

    }

    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Byte>> getByteList() {
        return Optional.empty();
    }

    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {

    }

    @Override
    public void setByteList(@NotNull Collection<Byte> value) {

    }

    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<String>> getStringList() {
        return Optional.empty();
    }

    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {

    }

    @Override
    public void setStringList(@NotNull Collection<String> value) {

    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(Class<? extends SerializableObject> clazz) {
        return Optional.empty();
    }

    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {

    }

    @Override
    public void setList(@NotNull Collection<? extends SerializableObject> value) {

    }

    @Override
    public <K, V> Optional<Map<K, V>> getMap(@NotNull String name, @NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        return Optional.empty();
    }

    @Override
    public <K, V> Optional<Map<K, V>> getMap(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        return Optional.empty();
    }


    @Override
    public <K, V> void setMap(@NotNull String name, @NotNull Map<K, V> value) {

    }

    @Override
    public <K, V> void setMap(@NotNull Map<K, V> value) {

    }

    @Override
    public void flush() {

    }

    @Override
    public byte @NotNull [] toByteArray() {
        return new byte[0];
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {

    }

    @Override
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        return Optional.empty();
    }
}
