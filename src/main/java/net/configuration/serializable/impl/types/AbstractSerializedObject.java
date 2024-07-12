package net.configuration.serializable.impl.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.configuration.serializable.api.IgnoreSerialization;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
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
        this.loadClassFields();
    }

    @Override
    public void flush() {
        //reset field pointers
        this.fieldPointer.replaceAll((k, v) -> 0);
        this.counter = 0;
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
     * Check if a field for the given type is or will be serialized in the current class.
     *
     * @param forType The type to check.
     * @return If such a field exists.
     */
    protected boolean containsField(@NotNull Class<?> forType){
        try{
            getFieldName(forType);
            //field was found so reset field pointer from above method call
            int ptr = this.fieldPointer.remove(forType);
            this.fieldPointer.put(forType, ptr-1);
            return true;
        }catch(SerializationException e){
            return false;
        }
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

}
