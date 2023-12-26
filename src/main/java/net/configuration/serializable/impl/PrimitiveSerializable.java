package net.configuration.serializable.impl;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PrimitiveSerializable implements SerializableObject  {

    private String clazz;
    private Object data; //cannot be directly transient but the "primitiveness" of the object ensures serializability

    public PrimitiveSerializable(@NotNull Object primitive){
        if(!ClassUtils.isPrimitiveOrWrapper(primitive.getClass()))
            throw new SerializationException("Not a primitive or wrapper: " + primitive.getClass());

        if(primitive.getClass().isPrimitive()){
            this.clazz = ClassUtils.primitiveToWrapper(primitive.getClass()).getName();
        }else{
            this.clazz = primitive.getClass().getName();
        }

        this.data = primitive;
    }

    @SuppressWarnings("unused")
    private PrimitiveSerializable(){/*Called by simple creator*/}

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(this.clazz);
        try {
            this.setPrimitive(dest);
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        try {
            Optional<String> classOpt = src.getString();
            if(classOpt.isEmpty())
                throw new SerializationException("Could not find class data");
            this.clazz = classOpt.get();

            Optional<?> dataOpt = this.getPrimitive(src);
            if(dataOpt.isEmpty())
                throw new SerializationException("Could not find primitive object");
            this.data = dataOpt.get();

            return this;
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Set the primitive object into the given {@link SerializedObject}.
     *
     * @param obj The object in which the primitive is set.
     */
    private void setPrimitive(SerializedObject obj) throws ClassNotFoundException {
        Class<?> clazzInstance = Class.forName(this.clazz);
        if(clazzInstance == String.class){
            obj.setString((String) this.data);

        }else if(clazzInstance == Boolean.class){
            obj.setBoolean((boolean) this.data);

        }else if(clazzInstance == Character.class){
            obj.setChar((char) this.data);

        }else if(clazzInstance == Byte.class){
            obj.setByte((byte) this.data);

        }else if(clazzInstance == Short.class){
            obj.setShort((short) this.data);

        }else if(clazzInstance == Integer.class){
            obj.setInt((int) this.data);

        }else if(clazzInstance == Long.class){
            obj.setLong((long) this.data);

        }else if(clazzInstance == Float.class){
            obj.setFloat((float) this.data);

        }else if(clazzInstance == Double.class){
            obj.setDouble((double) this.data);

        }else{
            throw new IllegalArgumentException("No primitive object parameter " + clazzInstance);
        }
    }

    /**
     * Read the primitive object from the given {@link SerializedObject}.
     *
     * @param obj The object to read the primitive from.
     * @return The primitive object value read from the {@link SerializedObject}.
     */
    @NotNull
    private Optional<?> getPrimitive(SerializedObject obj) throws ClassNotFoundException {
        Class<?> classInstance = Class.forName(this.clazz);
        if(classInstance == String.class){
            return obj.getString();

        }else if(classInstance == Boolean.class){
            return obj.getBoolean();

        }else if(classInstance == Character.class){
            return obj.getChar();

        }else if(classInstance == Byte.class){
            return obj.getByte();

        }else if(classInstance == Short.class){
            return obj.getShort();

        }else if(classInstance == Integer.class){
            return obj.getInt();

        }else if(classInstance == Long.class){
            return obj.getLong();

        }else if(classInstance == Float.class){
            return obj.getFloat();

        }else if(classInstance == Double.class){
            return obj.getDouble();

        }else{
            throw new IllegalArgumentException("No primitive class parameter " + classInstance);
        }

    }
}
