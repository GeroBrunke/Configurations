package net.configuration.advanced;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BucketQueue<E> extends AddressablePriorityQueue<Integer, E> {

    @SuppressWarnings({"unused", "rawtypes"})
    @SerializationAPI
    private static final Creator<BucketQueue> CREATOR = new SimpleCreatorImpl<>(BucketQueue.class);

    private static final int INVALID_SIZE = -1;

    private transient List<E>[] buckets;
    private transient int size = INVALID_SIZE;
    private transient int min = 0;

    @SuppressWarnings("unused")
    public BucketQueue(@NotNull Comparator<Integer> comparator, @NotNull Comparable<Integer> defaultKey, @NotNull Class<E> elementType) {
        super(comparator, (Integer) defaultKey, elementType);
    }

    @SuppressWarnings("unused") //called via reflection API
    protected BucketQueue(){
        super();
    }

    @Override
    public boolean add(@NotNull Integer key, @NotNull E element) {
        if(size == INVALID_SIZE)
            this.init();

        return this.buckets[key % size].add(element);
    }

    @Override
    public void updateKey(E element, Integer newKey) {
        int oldKey = this.getKeyModSize(element);
        if(oldKey == INVALID_SIZE)
            throw new IllegalArgumentException("Given element is not in this queue");

        this.buckets[oldKey].remove(element);
        this.buckets[newKey % size].add(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void merge(AddressablePriorityQueue<Integer, E> other) {
        var entry = other.toArray();
        for(var e : entry){
            Tuple<Integer,E> t = (Tuple<Integer,E>) e;
            this.add(t.getKey(), t.getValue());
        }
    }

    @Override
    public void build(@NotNull Integer defaultKey, @NotNull E[] elements) {
        int s = elements.length;
        if(this.size == INVALID_SIZE)
            init(s);

        this.buckets[defaultKey % size].addAll(List.of(elements));

    }

    @Override
    public void decreaseKey(@NotNull E element, @NotNull Integer newKey) {
        int oldKey = this.getKeyModSize(element);
        if(oldKey == INVALID_SIZE || oldKey < newKey){
            throw new IllegalArgumentException("Invalid new key, either no decrease or element not in queue");
        }

        this.updateKey(element, newKey);
    }

    @Override
    public int size() {
        AtomicInteger allSize = new AtomicInteger();
        Arrays.stream(this.buckets).filter(Objects::nonNull).forEach(list -> allSize.addAndGet(list.size()));
        return allSize.get();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if(this.classOfE.isAssignableFrom(o.getClass())){
            return this.getKeyModSize((E) o) != INVALID_SIZE;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new RadixIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tuple<Integer,E> @NotNull [] toArray() {
        Tuple<Integer, E>[] data = (Tuple<Integer, E>[]) Array.newInstance(Tuple.class, this.size());
        int idx = 0;
        for(int i = 0; i < this.size; i++){
            for(E e : this.buckets[i]){
                data[idx++] = new Tuple<>(i, e);
            }
        }
        return data;
    }

    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        throw new UnsupportedOperationException("Cannot convert 2d-array into a 1d template representation");
    }

    @Override
    public boolean add(E e) {
        return this.add(this.defaultKey, e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if(!this.classOfE.isAssignableFrom(o.getClass()))
            throw new IllegalArgumentException("Invalid element type");

        int key = this.getKeyModSize((E) o);
        if(key == INVALID_SIZE)
            return false;

        return this.buckets[key].remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        boolean state = true;
        for(var e : c){
            state &= this.contains(e);
        }
        return state;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean state = true;
        for(E e : c){
            state &= this.add(e);
        }
        return state;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean state = true;
        for(var e : c){
            state &= this.remove(e);
        }
        return state;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        this.size = INVALID_SIZE;
        this.min = 0;
        this.buckets = null;
    }

    @Override
    public boolean offer(E e) {
        return this.add(this.defaultKey, e);
    }

    @Override
    public E remove() {
        return this.poll();
    }

    @Override
    public E poll() {
        int steps = 0;
        while(steps < size && this.buckets[min].isEmpty()){
            this.min = (this.min + 1) % size;
            steps++;
        }

        if(steps == size)
            return null;

        return this.buckets[this.min].remove(0);
    }

    @Override
    public E element() {
        return this.peek();
    }

    @Override
    public E peek() {
        int steps = 0;
        while(steps < size && this.buckets[min].isEmpty()){
            this.min = (this.min + 1) % size;
            steps++;
        }

        if(steps == size)
            return null;

        return this.buckets[this.min].get(0);
    }

    private void init(){
        init(10);
    }

    @Override
    public String toString() {
        JsonObject str = new JsonObject();

        JsonArray data = new JsonArray();
        for(int i = 0; i < size; i++){
            JsonObject entry = new JsonObject();
            entry.addProperty("key", i);
            JsonArray bucket = new JsonArray();
            for(E e : this.buckets[i]){
                bucket.add(e.toString());
            }
            entry.add("data", bucket);

            data.add(entry);
        }

        str.addProperty("min", this.min);
        str.addProperty("size", this.size);
        str.add("data", data);

        return str.toString();
    }

    @SuppressWarnings("unchecked")
    private void init(int size){
        this.buckets = new List[size];
        for(int i = 0; i < size; i++){
            this.buckets[i] = new LinkedList<>();
        }
        this.size = size;
        this.min = 0;
    }

    /**
     * Get the index at which the given element is in the cyclic bucket array.
     *
     * @param elem The element to get the reduced key (actualKey % size) for.
     * @return The index in the bucket array.
     */
    private int getKeyModSize(E elem){
        for(int i = 0; i < this.size; i++){
            if(buckets[i].contains(elem))
                return i;
        }

        return INVALID_SIZE;
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        if(this.isEmpty())
            return;

        dest.setString("valueType", this.classOfE.getName());
        Map<Double, E> map = new HashMap<>();
        for(int key = 0; key < this.buckets.length; key++){
            double idx = 0;
            for(var value : this.buckets[key]){
                map.put(key + idx/this.buckets[key].size(), value);
                idx++;
            }
        }
        dest.setMap("queue", map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull AddressablePriorityQueue<Integer, E> read(@NotNull SerializedObject src) {
        try{
            Class<E> valueType = (Class<E>) Class.forName(src.getString("valueType").orElseThrow());
            Map<Double, E> map = src.getMap("queue", Double.class, valueType).orElseThrow();
            for(var entry : map.entrySet()){
                int key = entry.getKey().intValue();
                E val = entry.getValue();

                this.add(key, val);
            }


        }catch(ClassNotFoundException e){
            throw new SerializationException(e);
        }


        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BucketQueue<?> that)) return false;
        return size == that.size && min == that.min && Arrays.equals(buckets, that.buckets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, min);
        result = 31 * result + Arrays.hashCode(buckets);
        return result;
    }

    private class RadixIterator implements Iterator<E>{

        private int current = BucketQueue.this.min;
        private final int max = BucketQueue.this.min;
        private Iterator<E> bucketIterator;

        @Override
        public boolean hasNext() {
            if(bucketIterator == null)
                bucketIterator = buckets[current].iterator();

            if(bucketIterator.hasNext())
                return true;

            return this.findNextListIterator();
        }

        @Override
        public E next() {
            return bucketIterator.next();
        }

        private boolean findNextListIterator(){
            //update to next iterator
            while((++current) % size != max){
                bucketIterator = buckets[current].iterator();
                if(bucketIterator.hasNext()){
                    return true;
                }
            }
            return false;
        }
    }

}
