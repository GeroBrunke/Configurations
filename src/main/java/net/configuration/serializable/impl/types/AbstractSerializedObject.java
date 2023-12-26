package net.configuration.serializable.impl.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.configuration.serializable.api.IgnoreSerialization;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractSerializedObject implements SerializedObject {

    @NotNull protected final Class<?> clazz;
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
    public Class<?> getForClass() {
        return clazz;
    }

    @Override
    public void flush() {
        //reset field pointers
        this.fieldPointer.replaceAll((k, v) -> 0);
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
                    SerializedObject.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type))){

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
            throw new SerializationException("No field found for type " + forType.getName());
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

}
