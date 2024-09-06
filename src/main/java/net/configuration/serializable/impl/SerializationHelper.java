package net.configuration.serializable.impl;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SerializationHelper {

    private SerializationHelper(){} //hide implicit constructor

    /**
     * Convert the object instance of an enum value into a correct enum value.
     *
     * @param enumSuspect The object instance of an enum.
     * @return The enum instance of that object.
     * @throws SerializationException If the given object is no enum.
     */
    @SuppressWarnings("unchecked")
    //convert the enum object to a 'real' enum instance
    public static <T extends Enum<T>> T toEnum(@NotNull Object enumSuspect){
        try {
            Class<?> c = enumSuspect.getClass();
            String name = (String) c.getMethod("name").invoke(enumSuspect);
            return (T) c.getMethod("valueOf", String.class).invoke(null, name);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Write the given list into the given {@link SerializedObject}.
     *
     * @param obj The object to write to.
     * @param listType The type of the list elements.
     * @param list The list to write.
     * @throws IllegalArgumentException If the list type could not be written into the object.
     */
    @SuppressWarnings({"unchecked"})
    //Set a valid list into this object (helper method)
    public static void setList(@NotNull String name, @NotNull SerializedObject obj, Class<?> listType, List<?> list){
        if(listType == String.class){
            obj.setStringList(name, (List<String>) list);

        }else if(listType == Integer.class){
            obj.setIntList(name, (List<Integer>) list);

        }else if(listType == Long.class){
            obj.setLongList(name, (List<Long>) list);

        }else if(listType == Byte.class){
            obj.setByteList(name, (List<Byte>) list);

        }else if(listType == Double.class){
            obj.setDoubleList(name, (List<Double>) list);

        } else if(SerializableObject.class.isAssignableFrom(listType)){
            List<SerializableObject> l = (List<SerializableObject>) list;
            obj.setList(name, l);

        }else{
            throw new IllegalArgumentException("Illegal list type " + listType);
        }
    }

    /**
     * Read the next list from the given object.
     *
     * @param obj The object to read from.
     * @param listType The type of the list elements.
     * @return The list read from the given object.
     *
     * @throws IllegalArgumentException if the given list type cannot be serialized/deserialized.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Collection<T>> getList(@NotNull String name, @NotNull SerializedObject obj, @NotNull Class<?> listType){
        if(listType == String.class){
            Optional<Collection<String>> opt = obj.getStringList(name);
            Collection<String> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        }else if(listType == Integer.class){
            Optional<Collection<Integer>> opt = obj.getIntList(name);
            Collection<Integer> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        }else if(listType == Double.class){
            Optional<Collection<Double>> opt = obj.getDoubleList(name);
            Collection<Double> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        }else if(listType == Long.class){
            Optional<Collection<Long>> opt = obj.getLongList(name);
            Collection<Long> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        }else if(listType == Byte.class){
            Optional<Collection<Byte>> opt = obj.getByteList(name);
            Collection<Byte> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        } else if(SerializableObject.class.isAssignableFrom(listType)){
            Optional<Collection<SerializableObject>> opt = obj.getList(name, (Class<? extends SerializableObject>) listType);
            Collection<?> val = opt.orElse(new ArrayList<>());
            return Optional.of(castList(val));

        }else{
            throw new IllegalArgumentException("Illegal list type " + listType);
        }
    }

    /**
     * Convert the given collection into a list of given element types.
     *
     * @param original The collection to deep copy.
     * @return A list containing all the elements of the given collection as elements of type T.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> castList(@NotNull Collection<?> original){
        List<T> result = new ArrayList<>();
        for(var e : original){
            if(e == null)
                result.add(null);
            else
                result.add((T) e);
        }

        return result;
    }


    /**
     * Write the given primitive value in the given object.
     *
     * @param serializedObject The object to write to.
     * @param obj The primitive value to be written.
     * @param classOfT The type of that primitive.
     * @throws IllegalArgumentException if the given object is no primitive.
     */
    //Set a valid primitive into this object (helper method)
    public static void setPrimitive(@NotNull String name, @NotNull SerializedObject serializedObject, Object obj, Class<?> classOfT){
        if(classOfT == Boolean.class){
            serializedObject.setBoolean(name, (boolean) obj);

        }else if(classOfT == Byte.class){
            serializedObject.setByte(name, (byte) obj);

        }else if(classOfT == Short.class){
            serializedObject.setShort(name, (short) obj);

        }else if(classOfT == Integer.class){
            serializedObject.setInt(name, (int) obj);

        }else if(classOfT == Long.class){
            serializedObject.setLong(name, (long) obj);

        }else if(classOfT == Float.class){
            serializedObject.setFloat(name, (float) obj);

        }else if(classOfT == Double.class){
            serializedObject.setDouble(name, (double) obj);

        }else if(classOfT == Character.class){
            serializedObject.setChar(name, (char) obj);

        }else if(classOfT == String.class){
            serializedObject.setString(name, (String) obj);

        }else{
            throw new IllegalArgumentException("Cannot serialize object " + obj);
        }
    }


    /**
     * Read the next primitive from the given object.
     *
     * @param obj The object to read from.
     * @param classOfT The type of the read primitive.
     * @return The read primitive object.
     * @throws IllegalArgumentException if the given class is no primitive or wrapper class.
     */
    //Get a valid primitive from this object (helper method)
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getPrimitive(@NotNull String name, @NotNull SerializedObject obj, Class<?> classOfT){
        if(classOfT.isPrimitive())
            classOfT = ClassUtils.primitiveToWrapper(classOfT);

        if(classOfT == Boolean.class){
            Optional<Boolean> opt = obj.getBoolean(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Byte.class || classOfT == Long.class || classOfT == Integer.class || classOfT == Short.class){
            return getPrimitiveInteger(name, obj, classOfT);

        }else if(classOfT == Float.class){
            Optional<Float> opt = obj.getFloat(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Double.class){
            Optional<Double> opt = obj.getDouble(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Character.class){
            Optional<Character> opt = obj.getChar(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == String.class){
            Optional<String> opt = obj.getString(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else{
            throw new IllegalArgumentException("Cannot deserialize primitive object for " + classOfT.getName());
        }

        return Optional.empty();
    }

    /**
     * Get the primitive integer type mapped to the given name inside the provided serialized object.
     * Primitive integer types for this method consist of byte, short, int, long.
     *
     * @param name The name the integer is mapped to.
     * @param obj The serialized object to get the integer type from.
     * @param classOfT The type of the integer.
     * @return An optional containing the integer mapped to the given name or an empty optional, if there is no integer
     * type mapped to that name.
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> getPrimitiveInteger(@NotNull String name, @NotNull SerializedObject obj, Class<?> classOfT){
        if(classOfT == Byte.class){
            Optional<Byte> opt = obj.getByte(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Short.class){
            Optional<Short> opt = obj.getShort(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Integer.class){
            Optional<Integer> opt = obj.getInt(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }else if(classOfT == Long.class){
            Optional<Long> opt = obj.getLong(name);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }

        }

        return Optional.empty();
    }

    /**
     * Retrieve the primitive value represented by the given string.
     *
     * @param value The string representation of the primitive.
     * @param clazz The type of the primitive.
     * @return The actual primitive object represented by the string.
     */
    public static Object extractPrimitive(@NotNull String value, @NotNull Class<?> clazz){
        if(clazz.isPrimitive())
            clazz = ClassUtils.primitiveToWrapper(clazz);

        if(clazz == Boolean.class){
            return Boolean.parseBoolean(value);

        }else if(clazz == Byte.class){
            return Byte.parseByte(value);

        }else if(clazz == Short.class){
            return Short.parseShort(value);

        }else if(clazz == Integer.class){
            return Integer.parseInt(value);

        }else if(clazz == Long.class){
            return Long.parseLong(value);

        }else if(clazz == Float.class){
            return Float.parseFloat(value);

        }else if(clazz == Double.class){
            return Double.parseDouble(value);

        }else if(clazz == Character.class){
            return value.charAt(0);

        }else if(clazz == String.class){
            return value;
        }

        return null;
    }

}
