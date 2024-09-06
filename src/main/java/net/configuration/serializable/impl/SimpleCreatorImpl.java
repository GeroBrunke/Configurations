package net.configuration.serializable.impl;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public record SimpleCreatorImpl<T extends SerializableObject>(Class<T> classOfT) implements Creator<T> {

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull
    T read(@NotNull SerializedObject src) {
        if (src.getForClass().isEmpty()) {
            src.setForClass(classOfT);
        }

        T instance = this.createInstance();
        return (T) instance.read(src);
    }

    /**
     * Create an instance of the serializable object from the generic type T by invoking the classes default constructor.
     *
     * @return A new instance of the generic type T.
     */
    private T createInstance() {
        try {
            Constructor<T> con = classOfT.getDeclaredConstructor();
            con.setAccessible(true);
            return con.newInstance();

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException(e);
        }
    }
}
