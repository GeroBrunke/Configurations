package net.configuration.config;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface Configuration {

    /**
     * Save the changes made in the configuration.
     *
     * @return True if the changes could be saved, false if any error occurred.
     */
    boolean save();

    /**
     * Reload all the configuration values. Note that reloading the config will discard all changes made after the last
     * save.
     *
     * @return True if the previous state of the configuration was loaded again properly, false otherwise.
     */
    boolean reload();

    /**
     * Get the name of the resource this config is for.
     *
     * @return The name of this config's resource.
     */
    @NotNull String getName();

    /**
     * Check if the current file configuration contains a value for the given path.
     *
     * @param path The path to check for existence.
     * @return True iff the config contains a value under this path.
     */
    boolean hasMember(@NotNull String path);

    /**
     * Read the value at the given path and parse it into a byte.
     *
     * @param path The value's path.
     * @return The byte value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Byte> getByte(@NotNull String path);

    /**
     * Set the byte-value at the given path to the new byte value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setByte(@NotNull String path, byte value);

    /**
     * Read the value at the given path and parse it into an integer.
     *
     * @param path The value's path.
     * @return The integer value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Integer> getInt(@NotNull String path);

    /**
     * Set the int-value at the given path to the new integer value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setInt(@NotNull String path, int value);

    /**
     * Read the value at the given path and parse it into a long.
     *
     * @param path The value's path.
     * @return The long value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Long> getLong(@NotNull String path);

    /**
     * Set the long-value at the given path to the new long value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setLong(@NotNull String path, long value);

    /**
     * Read the value at the given path and parse it into a short.
     *
     * @param path The value's path.
     * @return The short value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Short> getShort(@NotNull String path);

    /**
     * Set the short-value at the given path to the new short value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setShort(@NotNull String path, short value);

    /**
     * Read the value at the given path and parse it into a float.
     *
     * @param path The value's path.
     * @return The float value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Float> getFloat(@NotNull String path);

    /**
     * Set the float-value at the given path to the new float value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setFloat(@NotNull String path, float value);

    /**
     * Read the value at the given path and parse it into a double.
     *
     * @param path The value's path.
     * @return The double value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Double> getDouble(@NotNull String path);

    /**
     * Set the double-value at the given path to the new double value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setDouble(@NotNull String path, double value);

    /**
     * Read the value at the given path and parse it into a character.
     *
     * @param path The value's path.
     * @return The char value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Character> getChar(@NotNull String path);

    /**
     * Set the char-value at the given path to the new character value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setChar(@NotNull String path, char value);

    /**
     * Read the value at the given path and parse it into a boolean instance.
     *
     * @param path The value's path.
     * @return The boolean value at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<Boolean> getBoolean(@NotNull String path);

    /**
     * Set the boolean-value at the given path to the new boolean value.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setBoolean(@NotNull String path, boolean value);

    /**
     * Read the value at the given path and parse it into a string.
     *
     * @param path The value's path.
     * @return The string at the given path. If there is no value at the given path, this method will return an empty optional.
     */
    @NotNull Optional<String> getString(@NotNull String path);

    /**
     * Set the String-value at the given path to the new string.
     *
     * @param path The value's path. Does not necessarily exist before.
     * @param value The new value.
     */
    void setString(@NotNull String path, String value);

    /**
     * Get the list from the given path. NOte that not every type parameter for T is allowed. T has to be serializable.
     *
     * @param path The value's path.
     * @param classOfT The type of elements inside the list.
     * @return The list retrieved from the given path or an empty optional if no list was found.
     */
    @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT);

    /**
     * Set the given list of any object into the given path.
     *
     * @param path The path to set the array to. Does not necessarily exist before.
     * @param list The list to set.
     */
    <T> void setList(@NotNull String path, List<T> list);


    /**
     * Get the array in the given path converted into an array of given type. Notice that this method is not supported
     * by every config type.
     *
     * @param path The array's path.
     * @param classOfT The type of the objects inside the array.
     * @return A new array of given type read from the path. If there is no array at the given path, this method will return
     * an empty optional.
     */
    @NotNull<T> Optional<T[]> getArray(@NotNull String path, @NotNull Class<T> classOfT);

    /**
     * Set the given array of any object into the given path. Notice that this method is not supported by every config type.
     *
     * @param path The path to set the array to. Does not necessarily exist before.
     * @param array The array to set.
     */
    <T> void setArray(@NotNull String path, T[] array);

    /**
     * Get the object at the given path. Notice that this method is not supported in every config type.
     *
     * @param path The path to that object.
     * @return The object at that path or an empty optional, if there is no object (or a null value) at the given path.
     */
    @NotNull<T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT);

    /**
     * Set the given object at that path. Notice that this method is not supported in every config type.
     *
     * @param path The path to that object. Does not necessarily exist before.
     * @param value The object to set.
     */
    <T> void set(@NotNull String path, T value);

}
