package net.configuration.advanced;

import net.configuration.serializable.api.SerializableObject;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Queue;

public interface PriorityQueue<K extends Comparable<? super K>, E> extends Queue<E>, SerializableObject {

    /**
     * Build a new priority queue where all the given elements have the same given {@code defaultKey}. Note that if
     * every element has the same key, the priority queue degenerates to a normal FIFO-Queue.
     *
     * @param defaultKey The default key for the elements.
     * @param elements The elements to add.
     */
    void build(@NotNull K defaultKey, @NotNull E[] elements);

    /**
     * Decrease the key of the given element to the new key. Note that this method will throw an exception if the new
     * provided key is "larger or equal" than the current key according to the given {@link java.util.Comparator} of this queue.
     *
     * @param element The element whose key should be updated.
     * @param newKey The new key value.
     */
    void decreaseKey(@NotNull E element, @NotNull K newKey);

    /**
     * Create an induced comparator for the given key type.
     *
     * @return A comparator for the keys.
     */
    @NotNull
    default Comparator<K> createComparator(){
        return (Comparator<K> & Serializable) (c1, c2) -> c1.compareTo(c2);
    }


}
