package net.configuration.advanced;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;

interface BinaryHeapDefaults {

    default <K extends Comparable<? super K>, E> void build(PriorityQueue<K,E> queue, List<Tuple<K, Integer>> heap,
                                                            List<E> elem, E[] elements, K defaultKey, Comparator<K> comp){
        queue.clear();

        for(int i = 0; i < elements.length; i++){
            elem.add(elements[i]);
            heap.add(new Tuple<>(defaultKey, i));
        }

        for(int i = (int) Math.floor(queue.size() / 2.0); i >= 0; i--){
            this.siftDown(i, queue, comp, heap);
        }
    }

    /**
     * Invariant: Heap property holds except at pos. So sift element at {@code pos} down until heap property is fulfilled again.
     *
     * @param pos Position of the element that may violate the heap property.
     * @param heap The heap representation.
     * @param comp The comparator used for checking the heap property.
     */
    default <K> void siftUp(int pos, List<Tuple<K, Integer>> heap, Comparator<K> comp){
        int cmp = comp.compare(heap.get(pos / 2).getKey(), heap.get(pos).getKey());
        if(pos == 0 || cmp <= 0){ //cmp <= 0 means heap[pos/2].key <= heap[pos].key
            return;
        }

        Collections.swap(heap, pos, pos/2);
        siftUp(pos / 2, heap, comp);
    }

    /**
     * Invariant: Heap property holds except at pos. So sift element at {@code pos} down until heap property is fulfilled again.
     *
     * @param pos Position of the element that may violate the heap property.
     */
    default <K extends Comparable<? super K>, E> void siftDown(int pos, PriorityQueue<K,E> queue, Comparator<K> comp, List<Tuple<K, Integer>> heap){
        if(2*pos+1 < queue.size()){
            int cmp = comp.compare(heap.get(2*pos).getKey(), heap.get(2*pos + 1).getKey());
            int m = (2*pos + 1 > queue.size() || cmp <= 0) ? 2*pos : 2*pos+1;
            if(comp.compare(heap.get(pos).getKey(), heap.get(m).getKey()) > 0){
                Collections.swap(heap, pos, m);
                siftDown(m, queue, comp, heap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    default <K extends Comparable<? super K>, E> Tuple<K,E>[] toArray(PriorityQueue<K,E> queue, List<Tuple<K, Integer>> heap, List<E> elements) {
        Tuple<K,E>[] array = (Tuple<K, E>[]) Array.newInstance(Tuple.class, queue.size());
        for(int i = 0; i < array.length; i++){
            Tuple<K, Integer> heapEntry = heap.get(i);
            K key = heapEntry.getKey();
            E value = elements.get(heapEntry.getValue());
            array[i] = new Tuple<>(key, value);
        }
        return array;
    }

    @NotNull
    default <K,E> String toString(@NotNull List<Tuple<K, Integer>> heap, @NotNull List<E> elements){
        JsonArray array = new JsonArray();
        for(var heapEntry : heap){
            K key = heapEntry.getKey();
            E elem = elements.get(heapEntry.getValue());

            JsonObject pair = new JsonObject();
            pair.addProperty("key", key.toString());
            pair.addProperty("value", elem.toString());
            array.add(pair);
        }
        return array.toString();
    }


    default <K, E> void write(@NotNull SerializedObject dest, @NotNull List<Tuple<K, Integer>> heap, @NotNull List<E> elements, @NotNull K defaultKey) {
        dest.setObject("defaultKey", defaultKey);
        dest.setString("keyType", heap.get(0).getKey().getClass().getName());
        dest.setString("valueType", elements.get(0).getClass().getName());


        Map<K, E> map = new HashMap<>();
        for(var tuple : heap){
            K key = tuple.getKey();
            E elem = elements.get(tuple.getValue());
            map.put(key, elem);
        }
        dest.setMap("queue", map);
    }


}
