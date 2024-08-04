package net.configuration.advanced;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;

public class BinaryQueue<K extends Comparable<? super K>, E> implements PriorityQueue<K, E>, BinaryHeapDefaults {

    @SuppressWarnings({"unused", "rawtypes"})
    @SerializationAPI
    private static final Creator<BinaryQueue> CREATOR = new SimpleCreatorImpl<>(BinaryQueue.class);

    private static final String CANNOT_REMOVE = "Only the first element can be removed with poll().";

    @NotNull private final transient Comparator<K> comparator; //comparator.compare(o1, o2); //-> neg: o1 < o2, 0: o1 == o2, pos: o1 > o2
    @NotNull private final transient List<E> elements = new ArrayList<>();
    @NotNull private final transient List<Tuple<K, Integer>> heap = new ArrayList<>();
    private transient K defaultKey;

    @SuppressWarnings("unused") //Called via reflection API
    protected BinaryQueue(){
        this.comparator = this.createComparator();
    }

    public BinaryQueue(@NotNull Comparator<K> comparator){
        this.comparator = comparator;
    }

    @Override
    public void build(@NotNull K defaultKey, @NotNull E[] elements) {
        this.defaultKey = defaultKey;
        this.build(this, this.heap, this.elements, elements, defaultKey, this.comparator);
    }

    @Override
    public void decreaseKey(@NotNull E element, @NotNull K newKey) {
        if(!this.contains(element))
            throw new NoSuchElementException();

        int elementIndex = this.elements.indexOf(element);
        int heapIndex = 0;
        for(Tuple<K, Integer> e : this.heap){
            if(e.getValue() == elementIndex)
                break;
            heapIndex++;
        }

        Tuple<K, Integer> entry = this.getHeapEntry(this.elements.indexOf(element));
        K oldKey = entry.getKey();
        if(this.comparator.compare(oldKey, newKey) <= 0){
            //not a decrease
            throw new IllegalArgumentException("The new key is greater than the previous key");
        }

        entry.setKey(newKey);
        this.siftUp(heapIndex, this.heap, this.comparator);

    }

    @Override
    public String toString() {
        return this.toString(this.heap, this.elements);
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.elements.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new HeapIterator();
    }

    @Override
    public Tuple<K,E>[] toArray() {
        return this.toArray(this, this.heap, this.elements);
    }

    @Override
    public <T> T[] toArray(T @NotNull [] a) {
        throw new UnsupportedOperationException("Cannot convert 2d array into 1d template T. Use toArray() instead");
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Cannot add elements after the queue was created.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(CANNOT_REMOVE);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(this.elements).containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException("Cannot add elements after the queue was created.");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException(CANNOT_REMOVE);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException(CANNOT_REMOVE);
    }

    @Override
    public void clear() {
        this.elements.clear();
        this.heap.clear();
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }

    @Override
    public E remove() {
        if(this.isEmpty())
            throw new IllegalStateException("Queue is empty");

        return this.poll();
    }

    @Override
    public E poll() {
        E elem = this.peek();
        this.removeElement();

        return elem;
    }

    @Override
    public E element() {
        if(this.isEmpty())
            throw new IllegalStateException("Queue is empty");

        return this.peek();
    }

    @Override
    public E peek() {
        return this.elements.get(this.heap.get(0).getValue());
    }

    /**
     * Removes the element with index {@code index} from the heap and restores heap property.
     */
    private void removeElement(){
        Collections.swap(this.heap, 0, this.size() - 1);

        //pop element previous at elementIndex from heap
        Tuple<K, Integer> entry = this.heap.remove(this.heap.size() - 1);
        this.elements.remove(entry.getValue().intValue());
        this.siftDown(0, this, this.comparator, this.heap); //elementIndex

        //update indices in heap, since the new vector is shorter
        for(Tuple<K, Integer> e : this.heap){
            if(e.getValue() > entry.getValue()){ //index
                int old = e.getValue();
                e.setValue(old-1);
            }
        }
    }

    /**
     * Get the heap entry, that points to the given element index in the {@link BinaryQueue#elements} list.
     *
     * @param elementIndex The index in the {@link BinaryQueue#elements} list.
     * @return The entry in the heap pointing on the given index in the {@link BinaryQueue#elements} list.
     */
    @NotNull
    private Tuple<K, Integer> getHeapEntry(int elementIndex){
        int cnt = 0;
        for(Tuple<K, Integer> e : this.heap){
            if(e.getValue() == elementIndex)
                break;
            cnt++;
        }

        return this.heap.get(cnt);
    }

    /**
     * Iterator for iterating over the elements in the heap order. Note that this iterator will not iterate over
     * this queue elements in sorted order, rather it will just go through the heap structure in an BFS-like order.
     */
    private class HeapIterator implements Iterator<E>{

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < heap.size();
        }

        @Override
        public E next() {
            if(!hasNext())
                throw new NoSuchElementException();

            return elements.get(heap.get(index++).getValue());
        }
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        if(this.isEmpty())
            return;

        this.write(dest, this.heap, this.elements, this.defaultKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull BinaryQueue<K, E> read(@NotNull SerializedObject src) {
        try {
            Class<K> keyType = (Class<K>) Class.forName(src.getString("keyType").orElseThrow());
            Class<E> valueType = (Class<E>) Class.forName(src.getString("valueType").orElseThrow());
            this.defaultKey = src.getObject("defaultKey", keyType).orElseThrow();
            Map<K, E> map = src.getMap("queue", keyType, valueType).orElseThrow();
            this.build(defaultKey, map.values().toArray((E[]) Array.newInstance(valueType, map.size())));

            for(var entry : map.entrySet()){
                this.decreaseKey(entry.getValue(), entry.getKey());
            }
            return this;

        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof BinaryQueue<?,?> that){
            boolean res = true;
            for(var h : that.heap){
                var key = h.getKey();
                E thatElem = (E) that.getElement(key);
                if(thatElem != null)
                    res &= thatElem.equals(this.getElement(key));
                else
                    res = false;
            }

            return res;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparator, elements, heap);
    }

    private E getElement(Comparable<?> key){
        for(var e : heap){
            if(e.getKey().equals(key))
                return this.elements.get(e.getValue());
        }

        return null;
    }
}
