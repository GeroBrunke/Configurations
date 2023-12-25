package net.configuration.serializable.api;

import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public interface Creator<T extends SerializableObject> {

    @NotNull
    T read(@NotNull SerializedObject src);

    /**
     * Read the object of type {@code classOfT} from the serialized version. In order to properly work
     * the class of the object has to have a default constructor or a constructor with only {@link SerializedObject}
     * as a parameter. When it has both, the default constructor is preferred by this method and after initialising
     * the object with that constructor the {@link SerializableObject#read(SerializedObject)} method is invoked.
     * Moreover, if there is no read() inherited from {@link SerializableObject#read(SerializedObject)} in the given class
     * then the abstract {@link Creator#read(SerializedObject)} method of this instance is invoked.
     *
     * @param src The serialized version of the object.
     * @param classOfT The type of the object to read.
     * @return The deserialized version of the given object.
     * @throws SerializationException If any errors occur while reading the necessary methods and constructors from
     * the given class.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default T read(@NotNull SerializedObject src, @NotNull Class<T> classOfT){
        //look for read method inside the given class
        Method readMethod = null;
        for(Method method : classOfT.getSuperclass().getDeclaredMethods()){
            if(method.getAnnotation(SerializationAPI.class) != null){
                readMethod = method;
                break;
            }
        }

        //if no method was found, use the default read method from this class
        if(readMethod == null){
            return read(src);
        }

        //the class of the serialized object has to have a default constructor or a constructor with only
        // a SerializedObject as a parameter in order to work here

        //find default constructor
        Constructor<T> con;
        boolean defaultConstructor = true;
        Optional<Constructor<T>> defConOpt = this.getConstructor(classOfT);
        if(defConOpt.isPresent()){
            con = defConOpt.get();
        }else{
            Optional<Constructor<T>> conOpt = this.getConstructor(classOfT, SerializedObject.class);
            if(conOpt.isPresent()){
                con = conOpt.get();
                defaultConstructor = false;
            }else{
                throw new SerializationException("Could not find matching constructor in " + classOfT);
            }
        }

        //con cannot be null here
        Objects.requireNonNull(con);
        try{
            if(defaultConstructor){
                T instance = con.newInstance();
                return (T) instance.read(src);
            }else{
                //if the constructor already reads from the serialized object, then the read method is not invoked
                return con.newInstance(src);
            }

        }catch(Exception e){
            throw new SerializationException(e);
        }
    }

    /**
     * Read an object of given type {@code classOfT} from the provided serialized object. Note that the creator instance
     * used to deserialize the object is a {@link SimpleCreatorImpl} i.e. it cannot invoke {@link Creator#read(SerializedObject)}.
     *
     * @see Creator#read(SerializedObject, Class)
     * @param src The deserialized version of the object.
     * @param classOfT The type of the object to read.
     * @return A deserialized version of the object or an empty optional if any exception occurred while deserializing.
     */
    @NotNull
    static <T extends SerializableObject> Optional<T> readDefault(@NotNull SerializedObject src, @NotNull Class<T> classOfT){
        try{
            SimpleCreatorImpl<T> creator = new SimpleCreatorImpl<>();
            return Optional.of(creator.read(src, classOfT));
        }catch(SerializationException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @NotNull
    private Optional<Constructor<T>> getConstructor(Class<T> classOfT, Class<?>... params) {
        try {
            return Optional.of(classOfT.getDeclaredConstructor(params));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

}
