package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CollectionSerializable implements SerializableObject {

    private String elementType;
    private Collection<?> collection; //no transient since this class ensures that it is serializable or throws an exception

    public CollectionSerializable(@NotNull Collection<?> collection){
        if(collection.isEmpty()){
            elementType = IgnoreSerialization.class.getName();  //indicates empty collection
        }else{
            Optional<?> object = collection.stream().filter(Objects::nonNull).findFirst();
            if(object.isEmpty()){
                elementType = Creator.class.getName(); //indicates all null elements
            }else{
                elementType = object.get().getClass().getName();
            }
        }

        this.collection = collection;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        try {
            dest.setString(this.elementType);
            if(elementType.equals(IgnoreSerialization.class.getName())){
                //write empty collection
                dest.setInt("size", 0);

            }else if(elementType.equals(Creator.class.getName())){
                //write null collection
                dest.setInt("size", this.collection.size());

            }else{
                this.writeCollection(dest);
            }

        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        try {
            Optional<String> elemOpt = src.getString();
            if(elemOpt.isEmpty())
                throw new SerializationException("Could not deserialize element type");

            this.elementType = elemOpt.get();

            if(elementType.equals(IgnoreSerialization.class.getName())){
                //write empty collection
                Optional<Integer> sizeOpt = src.getInt("size");
                if(sizeOpt.isEmpty() || sizeOpt.get() != 0)
                    throw new SerializationException("Could not deserialize empty collection");

                this.collection = new ArrayList<>();

            }else if(elementType.equals(Creator.class.getName())){
                Optional<Integer> sizeOpt = src.getInt("size");
                if(sizeOpt.isEmpty())
                    throw new SerializationException("Could not deserialize empty collection");

                int size = sizeOpt.get();
                this.collection = new ArrayList<>(size);

            }else{
                this.collection = this.readCollection(src);
            }

            return this;
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Deserialize the underlying collection from the {@link SerializedObject} and add it into the provided
     * collection.
     *
     * @param classOfE The type of elements inside the returned collection.
     * @param into A collection to add the read collection into, usually an empty collection.
     * @param <E> A serializable type of elements inside the collection.
     * @return A collection containing the deserialized collection's elements.
     * @throws ClassNotFoundException If the element type read from the serialized object is not found.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <E, T extends Collection<E>> T getCollection(@NotNull Class<E> classOfE, @NotNull T into) throws ClassNotFoundException {
        if(!classOfE.isAssignableFrom(Class.forName(this.elementType)))
            throw new SerializationException("Element mismatch: " + elementType + " != " + classOfE);

        for(var elem : this.collection){
            into.add((E) elem);
        }

        return into;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void writeCollection(@NotNull SerializedObject dest) throws ClassNotFoundException {
        if(elementType.equals(Double.class.getName())){
            dest.setDoubleList((Collection<Double>) this.collection);

        }else if(elementType.equals(Float.class.getName())){
            //copy floats into double collection
            dest.setDoubleList(this.convertTo(this.collection));

        }else if(elementType.equals(Integer.class.getName())){
            dest.setIntList((Collection<Integer>) this.collection);

        }else if(elementType.equals(Short.class.getName())){
            dest.setIntList(this.convertTo(this.collection));

        }else if(elementType.equals(String.class.getName())){
            dest.setStringList((Collection<String>) this.collection);

        }else if(elementType.equals(Character.class.getName())){
            //copy char into string list
            List<String> list = new ArrayList<>();
            for(char c : (Collection<Character>) this.collection){
                list.add(String.valueOf(c));
            }
            dest.setStringList(list);

        }else if(elementType.equals(Byte.class.getName())){
            dest.setByteList((Collection<Byte>) this.collection);

        }else if(elementType.equals(Boolean.class.getName())){
            //copy booleans into byte list
            List<Byte> list = new ArrayList<>();
            for(boolean b : (Collection<Boolean>) collection){
                list.add(b ? (byte) 1 : (byte) 0);
            }
            dest.setByteList(list);

        }else if(elementType.equals(Long.class.getName())){
            dest.setLongList((Collection<Long>) this.collection);

        }else if(Class.forName(elementType).isEnum()){
            dest.setInt(this.collection.size());
            for(var e : collection){
                if(e == null)
                    throw new SerializationException("Cannot serialize an enum collection with null values");

                dest.setEnum((Enum) EnumSerializable.toEnum(e));
            }

        }else if(SerializableObject.class.isAssignableFrom(Class.forName(elementType))){
            dest.setList((Collection<SerializableObject>) collection);

        }else{
            throw new SerializationException("No a serializable collection element type: " + elementType);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <O,N> Collection<N> convertTo(@NotNull Collection<O> old){
        List<N> list = new ArrayList<>();
        for(O elem : old){
            list.add((N) elem);
        }
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NotNull
    private Collection<?> readCollection(@NotNull SerializedObject src) throws ClassNotFoundException {
        Class<?> elementClass = Class.forName(this.elementType);
        if(ClassUtils.isPrimitiveOrWrapper(elementClass) || elementClass == String.class){
            return this.readPrimitiveCollection(src);

        }else if(Class.forName(elementType).isEnum()){
            Optional<Integer> sizeOpt = src.getInt();
            if(sizeOpt.isEmpty())
                throw new SerializationException("Could not deserialize enum collection");

            int size = sizeOpt.get();
            List<Enum> list = new ArrayList<>(size);
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) Class.forName(elementType);
            for(int i = 0; i < size; i++){
                Optional<? extends Enum<?>> enumOpt = src.getEnum(enumClass);
                if(enumOpt.isEmpty())
                    throw new SerializationException("Could not deserialize enum collection");

                list.add(enumOpt.get());
            }

            return list;

        }else if(SerializableObject.class.isAssignableFrom(Class.forName(elementType))){
            Optional<Collection<SerializableObject>> listOpt = src.getList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize collection");

            return listOpt.get();

        }else{
            throw new SerializationException("No a serializable collection element type: " + elementType);
        }
    }

    private Collection<?> readPrimitiveCollection(@NotNull SerializedObject src) throws ClassNotFoundException {
        Class<?> elementClass = Class.forName(elementType);
        if(elementClass == Double.class || elementClass == Float.class || elementClass == Integer.class
                || elementClass == Short.class || elementClass == Long.class){
            return this.readNumericalCollection(src);

        }else if(elementType.equals(String.class.getName())){
            Optional<Collection<String>> listOpt = src.getStringList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize string collection");

            return listOpt.get();

        }else if(elementType.equals(Character.class.getName())){
            //copy string collection into char collection
            Optional<Collection<String>> listOpt = src.getStringList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize char collection");

            List<Character> list = new ArrayList<>();
            for(String elem : listOpt.get()){
                list.add(elem.charAt(0));
            }
            return list;

        }else if(elementType.equals(Byte.class.getName())){
            Optional<Collection<Byte>> listOpt = src.getByteList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize byte collection");

            return listOpt.get();

        }else if(elementType.equals(Boolean.class.getName())){
            //copy byte collection into boolean collection
            Optional<Collection<Byte>> listOpt = src.getByteList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize boolean collection");

            List<Boolean> list = new ArrayList<>();
            for(Byte elem : listOpt.get()){
                list.add(elem == 1);
            }
            return list;

        }

        return new ArrayList<>();
    }

    private Collection<?> readNumericalCollection(@NotNull SerializedObject src){
        if(elementType.equals(Double.class.getName())){
            Optional<Collection<Double>> listOpt = src.getDoubleList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize double collection");

            return listOpt.get();

        }else if(elementType.equals(Float.class.getName())){
            //copy double collection into float collection
            Optional<Collection<Double>> listOpt = src.getDoubleList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize float collection");

            List<Float> list = new ArrayList<>();
            for(Double elem : listOpt.get()){
                list.add(elem.floatValue());
            }
            return list;

        }else if(elementType.equals(Integer.class.getName())){
            Optional<Collection<Integer>> listOpt = src.getIntList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize int collection");

            return listOpt.get();

        }else if(elementType.equals(Short.class.getName())){
            //copy int collection into short collection
            Optional<Collection<Integer>> listOpt = src.getIntList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize short collection");

            List<Short> list = new ArrayList<>();
            for(Integer elem : listOpt.get()){
                list.add(elem.shortValue());
            }
            return list;

        }else if(elementType.equals(Long.class.getName())){
            Optional<Collection<Long>> listOpt = src.getLongList();
            if(listOpt.isEmpty())
                throw new SerializationException("Could not deserialize long collection");

            return listOpt.get();
        }

        return new ArrayList<>();
    }
}
