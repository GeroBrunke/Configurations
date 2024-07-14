package net.configuration.serializable.api;

import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.ObjectSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ObjectStorage {

    private ObjectStorage(){} //Hide implicit

    /**
     * Serialize the given object into the provided format.
     *
     * @param data The object to serialize.
     * @param type The format of the serialized data.
     * @return A serialized version of the provided object or an empty optional, if any exception occurred during serialization.
     */
    @NotNull
    public static Optional<SerializedObject> serialize(@NotNull SerializableObject data, @NotNull SerializableType type){
        try{
            SerializedObject obj = type.createEmpty(data.getClass());
            data.write(obj);
            obj.flush();

            return Optional.of(obj);

        }catch(Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Restore a serialized object from the given serialized data in the provided representation.
     *
     * @param rwaData The serialized version of the object.
     * @param classOfT The type of the original object.
     * @return The restored version of the serialized object. If any error occurred during the deserialization, then an
     * empty optional is returned.
     */
    @NotNull
    public static <T extends SerializableObject> Optional<T> deserialize(@NotNull SerializedObject rwaData, @NotNull Class<T> classOfT){
        try{
            Creator<T> creator = Creator.getCreator(classOfT);
            T val = creator.read(rwaData);

            return Optional.of(val);
        }catch(Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Serialize the given nullable object into the given format type.
     *
     * @param object The nullable object to serialize.
     * @param type The serialization type.
     * @return An optional containing the serialized version of the object or an empty optional, if the original could not
     * be serialized.
     */
    @NotNull
    public static Optional<SerializedObject> serializeObject(Object object, @NotNull SerializableType type){
        if(object == null){
            NullSerializable ns = new NullSerializable("object");
            return serialize(ns, type);
        }

        if(object instanceof SerializableObject ser){
            return serialize(ser, type);
        }

        ObjectSerializable o = new ObjectSerializable(object);
        return serialize(o, type);

    }

    /**
     * Deserialize the given object to its original form.
     *
     * @param obj The object to deserialize.
     * @param classOfT The type of the object serialized in the given object.
     * @return An optional containing the deserialized object or an empty optional, if the object could not be deserialized.
     * Note that an empty optional could also indicate that the serialized object stored a null value.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> deserializeObject(@NotNull SerializedObject obj, @NotNull Class<T> classOfT){
        if(obj.getForClass().orElse(null) == NullSerializable.class || obj.isNextNull("object", classOfT)){
            return Optional.empty();
        }

        if(SerializableObject.class.isAssignableFrom(classOfT)){
            var opt = deserialize(obj, (Class<? extends SerializableObject>) classOfT);
            if(opt.isPresent()){
                T val = (T) opt.get();
                return Optional.of(val);
            }
        }

        var opt = deserialize(obj, ObjectSerializable.class);
        if(opt.isPresent()){
            return opt.get().getData(classOfT);
        }

        return Optional.empty();
    }

}
