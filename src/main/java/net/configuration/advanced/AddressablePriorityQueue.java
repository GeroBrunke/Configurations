package net.configuration.advanced;


import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public abstract class AddressablePriorityQueue<K extends Comparable<? super K>, E> implements PriorityQueue<K, E> {

    @NotNull
    protected final transient Comparator<K> comparator;
    protected transient Class<E> classOfE;
    protected transient K defaultKey;

    /**
     * Create a new addressable priority queue based on the given key comparator.
     *
     * @param comparator The key comparator used on this queue.
     * @param defaultKey The default element key for every element if no key was provided when adding an element.
     * @param elementType The type of elements stored in this queue.
     */
    protected AddressablePriorityQueue(@NotNull Comparator<K> comparator, @NotNull K defaultKey, @NotNull Class<E> elementType){
        this.comparator = comparator;
        this.defaultKey = defaultKey;
        this.classOfE = elementType;
    }

    //Used for serialization API
    protected AddressablePriorityQueue(){
        this.comparator = createComparator();
    }

    /**
     * Add the given element with the given key to this queue.
     *
     * @param key The key of this element.
     * @param element The element to add.
     * @return If the element was added.
     */
    public abstract boolean add(@NotNull K key, @NotNull E element);

    /**
     * Update the key value for the given element. If there are multiple elements that are equal in this queue,
     * that is {@code e1.equals(e2)} returns true for any pair of elements, then the key is updated for the first
     * found element.
     *
     * @param element The element whose key to update.
     * @param newKey The new key of this element.
     */
    public abstract void updateKey(E element, K newKey);

    /**
     * Merge this and the given {@link AddressablePriorityQueue}. Merging is similar to {@link java.util.Queue#addAll(Collection)}
     * but this method may produce some exceptions depending on what type of queue is used. It is therefor highly
     * recommended to use this method instead.
     *
     * @param other The other queue to merge.
     */
    public abstract void merge(AddressablePriorityQueue<K,E> other);

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull AddressablePriorityQueue<K, E> read(@NotNull SerializedObject src) {
        try {
            Class<K> keyType = (Class<K>) Class.forName(src.getString("keyType").orElseThrow());
            this.defaultKey = src.getObject("defaultKey", keyType).orElseThrow();
            Class<E> valueType = (Class<E>) Class.forName(src.getString("valueType").orElseThrow());
            Map<K, E> map = src.getMap("queue", keyType, valueType).orElseThrow();
            this.build(this.defaultKey, map.values().toArray((E[]) Array.newInstance(valueType, map.size())));

            for(var entry : map.entrySet()){
                this.decreaseKey(entry.getValue(), entry.getKey());
            }

        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }

        return this;
    }

}
