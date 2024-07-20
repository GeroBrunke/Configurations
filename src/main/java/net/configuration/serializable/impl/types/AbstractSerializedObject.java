package net.configuration.serializable.impl.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.TupleSerializable;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractSerializedObject implements SerializedObject {

    private int counter = 0;

    @NotNull protected Class<?> clazz;
    protected Logger logger;
    protected boolean printWarnings = false;

    protected String ymlPrefix;
    protected String xmlPrefix;

    //store all field names for a given type in the class
    @NotNull protected final BiMap<Class<?>, List<String>> classFields = HashBiMap.create();

    //store a pointer to the next field name in classFields for a given type
    @NotNull protected final Map<Class<?>, Integer> fieldPointer = new ConcurrentHashMap<>();

    protected AbstractSerializedObject(@NotNull Class<?> clazz){
        this.clazz = clazz;
        this.loadClassFields();
    }

    protected AbstractSerializedObject(){
        this.clazz = Class.class;
    }

    protected AbstractSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings){
        this(clazz);
        logger = warnLog;
        this.printWarnings = printWarnings;
    }

    @Override
    public @NotNull Optional<Class<?>> getForClass() {
        if(clazz == Class.class)
            return Optional.empty();

        return Optional.of(clazz);
    }

    @Override
    public <T extends SerializableObject> void setForClass(@NotNull Class<T> clazz) {
        this.clazz = clazz;
        this.xmlPrefix = clazz.getSimpleName() + "_";
        this.ymlPrefix = clazz.getSimpleName() + ".";
        this.loadClassFields();
    }

    @Override
    public void flush() {
        //reset field pointers
        this.fieldPointer.replaceAll((k, v) -> 0);
        this.counter = 0;
    }


    //########################## Abstract methods equal in every subclass ###############################

    @Override
    public Optional<Byte> getByte() {
        String name = this.getFieldName(Byte.class);
        return this.getByte(name);
    }

    @Override
    public void setByte(byte value) {
        String name = this.getFieldName(Byte.class);
        this.setByte(name, value);
    }

    @Override
    public Optional<Short> getShort() {
        String name = this.getFieldName(Short.class);
        return this.getShort(name);
    }

    @Override
    public void setShort(short value) {
        String name = this.getFieldName(Short.class);
        this.setShort(name, value);
    }

    @Override
    public Optional<Integer> getInt() {
        String name = this.getFieldName(Integer.class);
        return this.getInt(name);
    }

    @Override
    public void setInt(int value) {
        String name = this.getFieldName(Integer.class);
        this.setInt(name, value);
    }

    @Override
    public Optional<Long> getLong() {
        String name = this.getFieldName(Long.class);
        return this.getLong(name);
    }

    @Override
    public void setLong(long value) {
        String name = this.getFieldName(Long.class);
        this.setLong(name, value);
    }

    @Override
    public Optional<Float> getFloat() {
        String name = this.getFieldName(Float.class);
        return this.getFloat(name);
    }

    @Override
    public void setFloat(float value) {
        String name = this.getFieldName(Float.class);
        this.setFloat(name, value);
    }

    @Override
    public Optional<Double> getDouble() {
        String name = this.getFieldName(Double.class);
        return this.getDouble(name);
    }

    @Override
    public void setDouble(double value) {
        String name = this.getFieldName(Double.class);
        this.setDouble(name, value);
    }

    @Override
    public Optional<Character> getChar() {
        String name = this.getFieldName(Character.class);
        return this.getChar(name);
    }

    @Override
    public void setChar(char value) {
        String name = this.getFieldName(Character.class);
        this.setChar(name, value);
    }

    @Override
    public Optional<String> getString() {
        String name = this.getFieldName(String.class);
        return this.getString(name);
    }

    @Override
    public void setString(@NotNull String value) {
        String name = this.getFieldName(String.class);
        this.setString(name, value);
    }

    @Override
    public Optional<Boolean> getBoolean() {
        String name = this.getFieldName(Boolean.class);
        return this.getBoolean(name);
    }

    @Override
    public void setBoolean(boolean value) {
        String name = this.getFieldName(Boolean.class);
        this.setBoolean(name, value);
    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull Class<T> classOfT) {
        String name = this.getFieldName(classOfT);
        return this.getArray(name, classOfT);
    }

    @Override
    public <T> void setArray(T @NotNull [] array) {
        String name = this.getFieldName(array.getClass());
        this.setArray(name, array);
    }

    @Override
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull Class<? extends Enum<?>> classOfT) {
        String name = this.getFieldName(classOfT);
        return this.getEnum(name, classOfT);
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull T value) {
        String name = this.getFieldName(value.getClass());
        this.setEnum(name, value);
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(Class<T> classOfT) {
        String name = this.getFieldName(classOfT);
        return this.getSerializable(name, classOfT);
    }

    @Override
    public void setSerializable(@NotNull SerializableObject value) {
        String name = this.getFieldName(value.getClass());
        this.setSerializable(name, value);
    }

    @Override
    public Optional<SerializableObject> getNull() {
        String name = this.getFieldName(Class.class);
        return this.getNull(name);
    }

    @Override
    public void setNull() {
        this.setNull(this.getFieldName(Class.class));
    }

    @Override
    public boolean isNextNull(@NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        String name = this.getFieldName(type);
        return this.isNextNull(name, type);
    }

    @Override
    public Optional<SerializedObject> get() {
        String name = this.getFieldName(SerializedObject.class);
        return this.get(name);
    }

    @Override
    public void set(@NotNull SerializedObject value) {
        String name = this.getFieldName(SerializedObject.class);
        this.set(name, value);
    }

    @Override
    public Optional<Collection<Integer>> getIntList() {
        String name = this.getFieldName(Collection.class);
        return this.getIntList(name);
    }

    @Override
    public void setIntList(@NotNull Collection<Integer> value) {
        String name = this.getFieldName(Collection.class);
        this.setIntList(name, value);
    }

    @Override
    public Optional<Collection<Long>> getLongList() {
        String name = this.getFieldName(Collection.class);
        return this.getLongList(name);
    }

    @Override
    public void setLongList(@NotNull Collection<Long> value) {
        String name = this.getFieldName(Collection.class);
        this.setLongList(name, value);
    }

    @Override
    public Optional<Collection<Double>> getDoubleList() {
        String name = this.getFieldName(Collection.class);
        return this.getDoubleList(name);
    }

    @Override
    public void setDoubleList(@NotNull Collection<Double> value) {
        String name = this.getFieldName(Collection.class);
        this.setDoubleList(name, value);
    }

    @Override
    public Optional<Collection<Byte>> getByteList() {
        String name = this.getFieldName(Collection.class);
        return this.getByteList(name);
    }

    @Override
    public void setByteList(@NotNull Collection<Byte> value) {
        String name = this.getFieldName(Collection.class);
        this.setByteList(name, value);
    }

    @Override
    public Optional<Collection<String>> getStringList() {
        String name = this.getFieldName(Collection.class);
        return this.getStringList(name);
    }

    @Override
    public void setStringList(@NotNull Collection<String> value) {
        String name = this.getFieldName(Collection.class);
        this.setStringList(name, value);
    }

    @Override
    public <T> Optional<T[]> getArray(@NotNull String name, @NotNull Class<T> classOfT) {
        return this.getArrayHelper(name, classOfT);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> void setArray(@NotNull String name, T @NotNull [] array) {
        if(array[0] == null)
            throw new SerializationException("Null value inside array");

        Class<T> elementType = (Class<T>) array[0].getClass(); //never primitive always wrapper
        this.setArray(name, array, elementType);
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(Class<? extends SerializableObject> clazz) {
        String name = this.getFieldName(Collection.class);
        return this.getList(name, clazz);
    }

    @Override
    public void setList(@NotNull Collection<? extends SerializableObject> value) {
        String name = this.getFieldName(Collection.class);
        this.setList(name, value);
    }

    @Override
    public <K, V> Optional<Map<K, V>> getMap(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        String name = this.getFieldName(Collection.class);
        return this.getMap(name, keyClass, valueClass);
    }

    @Override
    public <K, V> void setMap(@NotNull Map<K, V> value) {
        String name = this.getFieldName(Collection.class);
        this.setMap(name, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Map<K, V>> getMap(@NotNull String name, @NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        var listOpt = this.getList(name, TupleSerializable.class);
        if(listOpt.isEmpty())
            return Optional.empty();

        Map<K, V> map = new HashMap<>();
        for(var e : listOpt.get()){
            if(e instanceof TupleSerializable tuple){
                SerializableType type = Objects.requireNonNull(SerializableType.fromImplementationClass(this.getClass()));
                Object key = tuple.getKey(type, keyClass).orElseThrow();
                Object value = tuple.getValue(type, valueClass).orElseThrow();
                map.put((K) key, (V) value);
            }else{
                return Optional.empty();
            }
        }

        return Optional.of(map);
    }

    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        List<Integer> list = new ArrayList<>();
        String listStr = this.getString(name).orElse("");
        for(String elem : listStr.split(", ")){
            int e = Integer.parseInt(elem);
            list.add(e);
        }

        return Optional.of(list);
    }

    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {
        StringBuilder listStr = new StringBuilder();
        for(int v : value){
            listStr.append(", ").append(v);
        }
        listStr = new StringBuilder(listStr.substring(2));
        this.setString(name, listStr.toString());
    }

    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        List<Long> list = new ArrayList<>();
        String listStr = this.getString(name).orElse("");
        for(String elem : listStr.split(", ")){
            long e = Long.parseLong(elem);
            list.add(e);
        }

        return Optional.of(list);
    }

    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {
        StringBuilder listStr = new StringBuilder();
        for(long v : value){
            listStr.append(", ").append(v);
        }
        listStr = new StringBuilder(listStr.substring(2));
        this.setString(name, listStr.toString());
    }

    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        List<Double> list = new ArrayList<>();
        String listStr = this.getString(name).orElse("");
        for(String elem : listStr.split(", ")){
            double e = Double.parseDouble(elem);
            list.add(e);
        }

        return Optional.of(list);
    }

    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {
        StringBuilder listStr = new StringBuilder();
        for(double v : value){
            listStr.append(", ").append(v);
        }
        listStr = new StringBuilder(listStr.substring(2));
        this.setString(name, listStr.toString());
    }

    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        List<Byte> list = new ArrayList<>();
        String listStr = this.getString(name).orElse("");
        for(String elem : listStr.split(", ")){
            byte e = Byte.parseByte(elem);
            list.add(e);
        }

        return Optional.of(list);
    }

    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {
        StringBuilder listStr = new StringBuilder();
        for(byte v : value){
            listStr.append(", ").append(v);
        }
        listStr = new StringBuilder(listStr.substring(2));
        this.setString(name, listStr.toString());
    }

    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        String listStr = this.getString(name).orElse("");
        List<String> list = new ArrayList<>(Arrays.asList(listStr.split(", ")));

        return Optional.of(list);
    }

    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {
        StringBuilder listStr = new StringBuilder();
        for(String v : value){
            listStr.append(", ").append(v);
        }
        listStr = new StringBuilder(listStr.substring(2));
        this.setString(name, listStr.toString());
    }

    @Override
    public <K, V> void setMap(@NotNull String name, @NotNull Map<K, V> value) {
        List<TupleSerializable> list = new ArrayList<>();
        SerializableType type = Objects.requireNonNull(SerializableType.fromImplementationClass(this.getClass()));
        for(var entrySet : value.entrySet()){
            var ser = new TupleSerializable(type, entrySet.getKey(), entrySet.getValue());
            list.add(ser);
        }

        this.setList(name, list);
    }



    /**
     * Load all fields that should be serialized from the given class. By default, all fields that are not static,
     * transient or annotated with {@link IgnoreSerialization} are considered serializable and therefore loaded by this
     * method.
     */
    protected final void loadClassFields(){
        for(Field field : this.clazz.getDeclaredFields()){
            String name = field.getName();
            Class<?> type = field.getType();

            //ignore static, transient or annotated fields
            IgnoreSerialization ignore = field.getAnnotation(IgnoreSerialization.class);
            int modifiers = field.getModifiers();
            if(ignore != null || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)){
                //print warn for ignore message if enabled
                if(ignore != null && this.logger != null && this.printWarnings){
                    logger.log(Level.WARNING, () ->
                            "Ignore serialization of field " + name + ". Reason: " + ignore.value());
                }

                continue;
            }

            //convert primitive to Wrapper objects
            if(type.isPrimitive()){
                type = ClassUtils.primitiveToWrapper(type);
            }

            //check if the field type is valid: PrimitiveWrapper, String, Enum, Collection or a serializable object
            if(!(ClassUtils.isPrimitiveWrapper(type) || type == String.class || type.isEnum() ||
                    Collection.class.isAssignableFrom(type) || SerializableObject.class.isAssignableFrom(type) ||
                    SerializedObject.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || type == Object.class)){

                throw new SerializationException("Cannot serialize field {" + name + ", type: " + type.getName() + "} " +
                        "in class " + this.clazz.getName());
            }


            //add an empty list, if the current type is not already present in the map
            this.classFields.computeIfAbsent(type, t ->{
                this.fieldPointer.put(t, 0);
                return new ArrayList<>(0);
            });

            this.classFields.get(type).add(name);

        }
    }

    /**
     * Get the field of given name from the current class or an empty optional, if there is no such field.
     *
     * @param name The name of the field.
     * @return An optional containing the declared field with the goiven name from the current class.
     */
    protected Optional<Field> getField(@NotNull String name){
        try{
            return Optional.of(this.clazz.getDeclaredField(name));
        }catch(NoSuchFieldException e){
            return Optional.empty();
        }
    }

    /**
     * Get the name of the field of given type in the current class the current pointer points to.
     * After the name is fetched the pointer is increased by 1, if no field is found for this type or
     * the pointer exceeds the amount of fields of given type a {@link SerializationException} is thrown.
     *
     * @param forType The type of the field.
     * @return The name of the field of given type the current pointer points to.
     */
    @NotNull
    protected String getFieldName(@NotNull Class<?> forType){
        //convert primitive to wrapper
        if(forType.isPrimitive()){
            forType = ClassUtils.primitiveToWrapper(forType);
        }

        if(!this.classFields.containsKey(forType) || !this.fieldPointer.containsKey(forType)){
            //add a dummy id
            int id = counter++;
            return String.valueOf(id);
        }

        int ptr = this.fieldPointer.remove(forType);
        String fieldName = this.classFields.get(forType).get(ptr);
        this.fieldPointer.put(forType, ptr+1);
        return fieldName;
    }


    /**
     * Deserializes the array of given type mapped to the name from this serialized object.
     *
     * @param name The name the array is mapped to.
     * @param classOfT The element type of the array.
     * @return The deserialized array or an empty optional if there was no array of given type mapped to the name.
     */
    @SuppressWarnings("unchecked")
    protected <T> Optional<T[]> getArrayHelper(@NotNull String name, @NotNull Class<T> classOfT) {
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            T[] array;
            if(classOfT.isPrimitive()){
                array = (T[]) this.getPrimitiveArray(name, ClassUtils.primitiveToWrapper(classOfT));
            }else{
                array = this.getPrimitiveArray(name, classOfT);
            }

            if(array.length > 0)
                return Optional.of(array);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            Class<? extends SerializableObject> c = (Class<? extends SerializableObject>) classOfT;
            Optional<Collection<SerializableObject>> listOpt = this.getList(name, c);
            if(listOpt.isPresent()){
                Collection<SerializableObject> list = listOpt.get();
                T[] array = (T[]) Array.newInstance(classOfT, list.size());
                int i = 0;
                for(SerializableObject val : list){
                    array[i++] = (T) val;
                }
                return Optional.of(array);
            }

        }else{
            throw new SerializationException("Could not deserialize array");
        }

        return Optional.empty();
    }


    /**
     * Write the given array into this serialized object mapped to the provided name.
     *
     * @param name The name the array will be mapped to.
     * @param array The array to write.
     * @param classOfT The type of the array elements.
     */
    protected <T> void setArray(@NotNull String name, T @NotNull[] array, @NotNull Class<T> classOfT){
        if(ClassUtils.isPrimitiveWrapper(classOfT)){
            this.setPrimitiveArray(name, array, classOfT);

        }else if(classOfT == String.class){
            List<String> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add((String) val);
            }

            this.setStringList(name, list);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<SerializableObject> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add((SerializableObject) val);
            }
            this.setList(name, list);

        }else{
            throw new SerializationException("Could not serialize array");
        }
    }


    @SuppressWarnings("unchecked")
    private <T> T[] getPrimitiveArray(@NotNull String name, @NotNull Class<T> classOfT){
        if(classOfT == Short.class || classOfT == Integer.class){
            Optional<Collection<Integer>> listOpt = this.getIntList(name);
            if(listOpt.isPresent()){
                Collection<Integer> list = listOpt.get();
                if(classOfT == Short.class){
                    Short[] array = new Short[list.size()];
                    int i = 0;
                    for(Integer val : list){
                        array[i++] = val.shortValue();
                    }
                    return (T[]) array;
                }

                Integer[] array = new Integer[list.size()];
                int i = 0;
                for(Integer val : list){
                    array[i++] = val;
                }
                return (T[]) array;
            }

        }else if(classOfT == Long.class){
            Optional<Collection<Long>> listOpt = this.getLongList(name);
            if(listOpt.isPresent()){
                Collection<Long> list = listOpt.get();
                Long[] array = new Long[list.size()];
                int i = 0;
                for(Long val : list){
                    array[i++] = val;
                }
                return (T[]) array;
            }

        }else if(classOfT == Float.class || classOfT == Double.class){
            return this.getPrimitiveDecimalArray(name, classOfT);

        }else{
            return getPrimitiveArray0(name, classOfT);
        }

        return (T[]) Array.newInstance(classOfT, 0);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] getPrimitiveDecimalArray(@NotNull String name, @NotNull Class<T> classOfT){
        if(classOfT == Float.class || classOfT == Double.class){
            Optional<Collection<Double>> listOpt = this.getDoubleList(name);
            if(listOpt.isPresent()){
                Collection<Double> list = listOpt.get();
                if(classOfT == Float.class){
                    Float[] array = new Float[list.size()];
                    int i = 0;
                    for(Double val : list){
                        array[i++] = val.floatValue();
                    }
                    return (T[]) array;
                }

                Double[] array = new Double[list.size()];
                int i = 0;
                for(Double val : list){
                    array[i++] = val;
                }
                return (T[]) array;
            }
        }

        return (T[]) Array.newInstance(classOfT, 0);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] getPrimitiveArray0(@NotNull String name, @NotNull Class<T> classOfT){
        if(classOfT == Byte.class || classOfT == Boolean.class){
            Optional<Collection<Byte>> listOpt = this.getByteList(name);
            if(listOpt.isPresent()){
                Collection<Byte> list = listOpt.get();
                if(classOfT == Boolean.class){
                    Boolean[] array = new Boolean[list.size()];
                    int i = 0;
                    for(Byte val : list){
                        array[i++] = val == (byte) 1;
                    }
                    return (T[]) array;
                }

                Byte[] array = new Byte[list.size()];
                int i = 0;
                for(Byte val : list){
                    array[i++] = val;
                }
                return (T[]) array;

            }

        }else if(classOfT == Character.class){
            Optional<Collection<String>> listOpt = this.getStringList(name);
            if(listOpt.isPresent()){
                Collection<String> list = listOpt.get();
                Character[] array = new Character[list.size()];
                int i = 0;
                for(String val : list){
                    array[i++] = val.charAt(0);
                }
                return (T[]) array;
            }
        }else if(classOfT == String.class){
            Optional<Collection<String>> listOpt = this.getStringList(name);
            if(listOpt.isPresent()){
                Collection<String> list = listOpt.get();
                String[] array = new String[list.size()];
                int i = 0;
                for(String val : list){
                    array[i++] = val;
                }
                return (T[]) array;
            }
        }

        return (T[]) Array.newInstance(classOfT, 0);
    }

    private <T> void setPrimitiveArray(@NotNull String name, T @NotNull[] array, @NotNull Class<T> classOfT){
        if(classOfT == Byte.class || classOfT == Boolean.class){
            List<Byte> list = new ArrayList<>(array.length);
            for(T val : array){
                byte bVal;
                if(classOfT == Boolean.class)
                    bVal = (boolean) val ? (byte) 1 : (byte) 0;
                else
                    bVal = (byte) val;

                list.add(bVal);
            }

            this.setByteList(name, list);

        }else if(classOfT == Short.class || classOfT == Integer.class){
            List<Integer> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add((int) val);
            }

            this.setIntList(name, list);

        }else if(classOfT == Long.class){
            List<Long> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add((long) val);
            }

            this.setLongList(name, list);

        }else if(classOfT == Float.class || classOfT == Double.class){
            List<Double> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add((double) val);
            }

            this.setDoubleList(name, list);

        }else if(classOfT == Character.class){
            List<String> list = new ArrayList<>(array.length);
            for(T val : array){
                list.add(String.valueOf((char) val));
            }

            this.setStringList(name, list);
        }
    }

    protected Object extractPrimitive(@NotNull String value, @NotNull Class<?> clazz){
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
