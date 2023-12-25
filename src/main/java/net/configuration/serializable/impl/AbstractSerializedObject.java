package net.configuration.serializable.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.configuration.serializable.api.SerializableObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSerializedObject implements SerializableObject {

    @NotNull protected final Class<?> clazz;

    //store all field names for a given type in the class
    @NotNull protected final BiMap<Class<?>, List<String>> classFields = HashBiMap.create();

    //store a pointer to the next field name in classFields for a given type
    @NotNull protected final Map<Class<?>, Integer> fieldPointer = new ConcurrentHashMap<>();

    protected AbstractSerializedObject(@NotNull Class<?> clazz){
        this.clazz = clazz;
        this.loadClassFields();
    }

    /**
     *
     */
    protected final void loadClassFields(){
        for(Field field : this.clazz.getDeclaredFields()){

        }
    }

    protected Optional<Field> getField(@NotNull String name){
        try{
            return Optional.of(this.clazz.getDeclaredField(name));
        }catch(NoSuchFieldException e){
            return Optional.empty();
        }
    }

}
