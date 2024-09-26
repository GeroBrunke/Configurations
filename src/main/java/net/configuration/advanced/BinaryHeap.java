package net.configuration.advanced;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BinaryHeap<K extends Comparable<? super K>, E> extends AddressablePriorityQueue<K, E> implements BinaryHeapDefaults{

    @SuppressWarnings({"unused", "rawtypes"})
    @SerializationAPI
    private static final Creator<BinaryHeap> CREATOR = new SimpleCreatorImpl<>(BinaryHeap.class);

    @NotNull private final transient List<E> elements = new ArrayList<>();
    @NotNull private final transient List<Tuple<K, Integer>> heap = new ArrayList<>();

    @SuppressWarnings("unused")
    public BinaryHeap(@NotNull Comparator<K> comparator, @NotNull K defaultKey, @NotNull Class<E> elementType) {
        super(comparator, defaultKey, elementType);
    }

    @SuppressWarnings("unused") //called via reflection API
    protected BinaryHeap(){
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void merge(AddressablePriorityQueue<K, E> other) {
        var entry = other.toArray();
        for(var e : entry){
            Tuple<K,E> t = (Tuple<K,E>) e;
            this.add(t.getKey(), t.getValue());
        }
    }

    @Override
    public void build(@NotNull K defaultKey, E[] elements) {
        this.build(this, this.heap, this.elements, elements, defaultKey, this.comparator);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public void updateKey(@NotNull E value, K newKey) {
        //Element whose key is elementIndex
        int elementIndex = this.elements.indexOf(value);
        if(elementIndex == -1)
            throw new NullPointerException("The queue does not contain the given value");

        int cnt = 0;
        for(Tuple<K, Integer> e : this.heap){
            if(e.getValue() == elementIndex)
                break;
            cnt++;
        }

        Tuple<K, Integer> pair = this.getHeapEntry(elementIndex);
        K oldKey = pair.setKey(newKey);
        int cmp = this.comparator.compare(oldKey, newKey);

        //restore heap property
        if(cmp < 0){
            this.siftDown(cnt, this, this.comparator, this.heap);
        }else{
            this.siftUp(cnt, this.heap, this.comparator);
        }
    }

    @Override
    public void decreaseKey(@NotNull E element, @NotNull K newKey) {
        K oldKEy = this.getKey(element);
        if(this.comparator.compare(oldKEy, newKey) < 0){
            throw new IllegalArgumentException("New key has no lower value than the old key");
        }

        this.updateKey(element, newKey);
    }

    @Override
    public boolean add(@NotNull K key, @NotNull E element){
        if(this.elements.contains(element)) {
            this.updateKey(element, key);
            return false;
        }

        int size = this.size();
        this.heap.add(new Tuple<>(key, size));
        this.elements.add(element);

        this.siftUp(size, this.heap, this.comparator); //restore heap property
        return true;
    }

    @Override
    public boolean add(E e) {
        return this.add(this.defaultKey, e);
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }

    @Override
    public E remove() {
        if(this.isEmpty())
            throw new ArrayIndexOutOfBoundsException("The queue is empty");
        return this.poll();
    }

    @Override
    public E poll() {
        int idx = this.heap.get(0).getValue();
        E del = this.elements.get(idx);
        this.remove(idx);

        return del;
    }

    @Override
    public E element() {
        if(this.isEmpty())
            throw new ArrayIndexOutOfBoundsException("The queue is empty");
        return this.peek();
    }

    @Override
    public E peek() {
        return this.elements.get(this.heap.get(0).getValue());
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.elements.contains(o);
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        throw new UnsupportedOperationException("Cannot convert 2 dimensional template " + BinaryHeap.class.getName()
                + " into 1d template T. Use toArray() instead");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if(o == null)
            throw new IllegalArgumentException("Cannot remove null values from this queue");

        if(classOfE.isAssignableFrom(o.getClass())){
            E value = (E) o;
            if(this.elements.contains(value)){
                int idx = this.elements.indexOf(value);
                this.remove(idx);
                return true;
            }
            return false;
        }

        throw new IllegalArgumentException("Cannot remove objects of type " + o.getClass().getName());
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(this.elements).containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean result = true;
        for(E v : c)
            result &= this.add(v);

        return result;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean result = true;
        for(Object o : c)
            result &= this.remove(o);

        return result;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        List<E> remove = new ArrayList<>();
        for(E elem : this.elements){
            if(!c.contains(elem)){
                remove.add(elem);
            }
        }
        return this.removeAll(remove);
    }

    @Override
    public void clear() {
        heap.clear();
        elements.clear();
    }

    @Override
    public String toString() {
        return this.toString(this.heap, this.elements);
    }

    @NotNull
    @Override
    public Tuple<K,E> @NotNull [] toArray() {
        return this.toArray(this, this.heap, this.elements);
    }

    /**
     * Removes the element with index {@code index} from the heap and restores heap property.
     *
     * @param elementIndex The index (in heap) which will be removed.
     */
    private void remove(int elementIndex){
        if(elementIndex < this.size() && elementIndex >= 0){
            int heapIndex = this.heap.indexOf(this.getHeapEntry(elementIndex));
            Collections.swap(this.heap, heapIndex, this.size() - 1);

            //pop element previous at elementIndex from heap
            var entry = this.heap.remove(this.heap.size() - 1);
            this.elements.remove(elementIndex);
            this.siftDown(heapIndex, this, this.comparator, this.heap); //elementIndex

            //update indices in heap, since the new vector is shorter
            for(Tuple<K, Integer> e : this.heap){
                if(e.getValue() > entry.getValue()){ //index
                    int old = e.getValue();
                    e.setValue(old-1);
                }
            }

        }else{
            throw new ArrayIndexOutOfBoundsException("Cannot remove index " + elementIndex + " (size: " + this.size() + ")");
        }
    }

    /**
     * Get the key from the given value. If there is no key for this value, this method will return null.
     *
     * @param value The element.
     * @return The key value of the given element, possibly null.
     */
    private K getKey(E value){
        int heapIdx = this.elements.indexOf(value);
        for(Tuple<K, Integer> e : this.heap){
            if(e.getValue() == heapIdx){
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * Get the heap entry, that points to the given element index in the {@link BinaryHeap#elements} list.
     *
     * @param elementIndex The index in the {@link BinaryHeap#elements} list.
     * @return The entry in the heap pointing on the given index in the {@link BinaryHeap#elements} list.
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

    @Override
    public void write(@NotNull SerializedObject dest) {
        if(this.isEmpty())
            return;

        this.write(dest, this.heap, this.elements, this.defaultKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof BinaryHeap<?,?> that){
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
