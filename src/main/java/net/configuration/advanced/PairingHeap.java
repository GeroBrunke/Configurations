package net.configuration.advanced;

import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class PairingHeap<K extends Comparable<? super K>, V> extends AddressablePriorityQueue<K, V>{

    /**
     * Create a new addressable priority queue based on the given key comparator.
     *
     * @param comparator  The key comparator used on this queue.
     * @param defaultKey  The default element key for every element if no key was provided when adding an element.
     * @param elementType The type of elements stored in this queue.
     */
    public PairingHeap(@NotNull Comparator<K> comparator, @NotNull K defaultKey, @NotNull Class<V> elementType) {
        super(comparator, defaultKey, elementType);
    }

    @Override
    public boolean add(@NotNull K key, @NotNull V element) {
        return false;
    }

    @Override
    public void updateKey(V element, K newKey) {

    }

    @Override
    public void merge(AddressablePriorityQueue<K, V> other) {

    }

    @Override
    public void build(@NotNull K defaultKey, @NotNull V[] elements) {

    }

    @Override
    public void decreaseKey(@NotNull V element, @NotNull K newKey) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return null;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return null;
    }

    @Override
    public boolean add(V v) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(V v) {
        return false;
    }

    @Override
    public V remove() {
        return null;
    }

    @Override
    public V poll() {
        return null;
    }

    @Override
    public V element() {
        return null;
    }

    @Override
    public V peek() {
        return null;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @Override
    public @NotNull AddressablePriorityQueue<K, V> read(@NotNull SerializedObject src) {
        return null;
    }
}
