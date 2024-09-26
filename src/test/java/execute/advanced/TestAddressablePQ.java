package execute.advanced;

import net.configuration.advanced.*;
import net.configuration.serializable.api.ObjectStorage;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAddressablePQ {

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Build APQ")
    void testBuildAPQ(@NotNull APQType type){
        AddressablePriorityQueue<Integer, String> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, String.class);

        queue.build(2, new String[]{"This", "is", "a", "Test", "message"});

        //test methods for java.util.Queue
        assert !queue.isEmpty();
        assert queue.size() == 5;
        assert queue.contains("is");
        assert !queue.contains("Queue");
        assert queue.containsAll(List.of("This", "a", "message"));
        assert !queue.containsAll(List.of("this", "addressable"));

        //test PQ method
        String min = queue.peek();
        String minDel = queue.poll();

        assertEquals("This", min);
        assertEquals(min, minDel);
        assertEquals(min, minDel);
        assert !queue.contains(min);

        queue.clear();

    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @EnumSource(APQType.class)
    @DisplayName("Test APQ To Array")
    void testPQToArray(@NotNull APQType type){
        AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, Integer.class);
        queue.build(2, new Integer[]{3,4,1,2,5});
        queue.decreaseKey(2, 1);
        var array = queue.toArray();

        //heap order of elements
        int[] result = this.arrayResult(type);

        for(int i = 0; i < array.length; i++){
            Tuple<Integer, Integer> tuple = (Tuple<Integer, Integer>) array[i];
            int resultVal = result[i];
            int resultKey = result[i + array.length];

            assertEquals(tuple.getKey(), resultKey);
            assertEquals(tuple.getValue(), resultVal);
        }
    }

    private int[] arrayResult(@NotNull APQType type){
        if(type == APQType.BINARY){
            //here heap order, else insertion order
            return  new int[]{2,3,1,4,5, 1,2,2,2,2};
        }

        //insertion order
        return new int[]{2,3,4,1,5, 1,2,2,2,2};
    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Sort With APQ")
    void testSortWithPQ(@NotNull APQType type){
        Integer[] numbers = new Integer[]{10, 8, 3, 2, 7, 1, 9, 5, 6, 4};
        AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                10, Integer.class);
        queue.build(9, numbers);

        queue.decreaseKey(1, 0);
        queue.decreaseKey(2, 2);
        queue.decreaseKey(3, 6);
        queue.decreaseKey(4, 8);
        queue.decreaseKey(5, 3);
        queue.decreaseKey(6, 4);

        queue.updateKey(7, 1);
        queue.updateKey(8, 5);
        queue.updateKey(9, 7);
        queue.updateKey(10, 9);

        int[] result = new int[]{1,7,2,5,6,8,3,9,4,10};
        int i = 0;
        while(!queue.isEmpty()){
            int min = queue.poll();
            assertEquals(min, result[i++]);
        }
    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test APQ Iterator")
    void testPQIterator(@NotNull APQType type){
        Integer[] numbers = new Integer[]{10, 8, 3, 2, 7, 1, 9, 5, 6, 4};
        AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                50, Integer.class);
        queue.build(1, numbers);

        //Heap ordered iterator
        int[] result = new int[]{10,8,3,2,7,1,9,5,6,4};
        Iterator<Integer> iter = queue.iterator();
        int i = 0;
        while(iter.hasNext()){
            int elem = iter.next();
            assertEquals(elem, result[i++]);
        }
    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Remove Element")
    void testRemoveElement(@NotNull APQType type){
        AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, Integer.class);
        queue.build(5, new Integer[]{3,4,1,2,5});
        queue.updateKey(1, 67);
        queue.updateKey(4, 54);
        assertTrue(queue.remove(1));

        assert !queue.contains(1);
    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Update Key")
    void testUpdateKey(@NotNull APQType type){
        AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, Integer.class);
        queue.build(5, new Integer[]{3,4,2,1,5});

        assertEquals(3, queue.peek());
        queue.updateKey(3, 6);
        assertEquals(4, queue.peek());


    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Merge APQ")
    void testMergeAPQ(@NotNull APQType type){
        //build two queues
        AddressablePriorityQueue<Integer, Integer> queue1 = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, Integer.class);
        Integer[] array1 = new Integer[]{3,4,2,1,5};
        queue1.build(5, array1);
        for(int i = 0; i < queue1.size(); i++){
            queue1.updateKey(array1[i], i);
        }

        AddressablePriorityQueue<Integer, Integer> queue2 = this.createTestQueue(type, Comparator.naturalOrder(),
                Integer.MAX_VALUE, Integer.class);
        Integer[] array2 = new Integer[]{7,8,9,6};
        queue2.build(8, array2);
        for(int i = 0; i < queue2.size(); i++){
            queue2.updateKey(array2[i], i);
        }

        int old = queue1.size();
        queue1.merge(queue2);
        assertEquals(queue1.size(), old + queue2.size());

        int[] result = new int[]{3,7,4,8,2,9,1,6,5};

        for(int i = 0; i < queue1.size(); i++){
            int min = queue1.poll();
            assertEquals(min, result[i]);
        }
    }

    @ParameterizedTest
    @EnumSource(APQType.class)
    @DisplayName("Test Serialize APQ")
    void testSerializeAPQ(@NotNull APQType type){
        for(SerializableType serializableType : SerializableType.values()){
            Integer[] numbers = new Integer[]{10, 8, 3, 2, 7, 1, 9, 5, 6, 4};
            AddressablePriorityQueue<Integer, Integer> queue = this.createTestQueue(type, Comparator.naturalOrder(),
                    Integer.MAX_VALUE, Integer.class);
            queue.build(10, numbers);
            for(Integer i : numbers){
                queue.decreaseKey(i, i);
            }

            SerializedObject obj = ObjectStorage.serialize(queue, serializableType).orElseThrow();

            AddressablePriorityQueue<?,?> read = ObjectStorage.deserialize(obj, type.apqClass).orElseThrow();
            assertEquals(queue, read);
        }
    }

    private <K extends Comparable<? super K>,E> AddressablePriorityQueue<K,E> createTestQueue(@NotNull APQType type, @NotNull Comparator<K> comparator,
                                                                @NotNull K defaultKey, @NotNull Class<E> elementType){
        return type.createNew(comparator, defaultKey, elementType);
    }

    @SuppressWarnings("rawtypes")
    private enum APQType{

        BINARY(BinaryHeap.class),
        BUCKET(BucketQueue.class);
        //PAIRING_HEAP(PairingHeap.class),
        //FIBONACCI(FibonacciHeap.class);

        @NotNull private final Class<? extends AddressablePriorityQueue> apqClass;

        APQType(@NotNull Class<? extends AddressablePriorityQueue> apqClass){
            this.apqClass = apqClass;
        }

        @SuppressWarnings("unchecked")
        public <K extends Comparable<? super K>,E> AddressablePriorityQueue<K,E> createNew(@NotNull Comparator<K> comparator, @NotNull K defaultKey,
                                                             @NotNull Class<E> elementType){
            try {
                Constructor<?> con = this.apqClass.getDeclaredConstructor(Comparator.class, Comparable.class, Class.class);
                con.setAccessible(true);
                return (AddressablePriorityQueue<K, E>) con.newInstance(comparator, defaultKey, elementType);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new SerializationException(e);
            }
        }

    }


}
