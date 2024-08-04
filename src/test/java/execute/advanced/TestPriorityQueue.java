package execute.advanced;

import net.configuration.advanced.BinaryQueue;
import net.configuration.advanced.PriorityQueue;
import net.configuration.advanced.Tuple;
import net.configuration.serializable.api.ObjectStorage;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPriorityQueue {

    @Test
    @DisplayName("Test Build PQ")
    void testBuildPQ(){
        PriorityQueue<Integer, String> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        assertTrue(queue.isEmpty());

        queue.build(3, new String[]{"Hello", "World,", "this", "is", "a", "dummy", "binary", "priority", "queue."});

        //test methods for java.util.Queue
        assertFalse(queue.isEmpty());
        assertEquals(9, queue.size());
        assertTrue(queue.contains("is"));
        assertFalse(queue.contains("the"));
        assertTrue(queue.containsAll(List.of("this", "a", "queue.")));
        assertFalse(queue.containsAll(List.of("this", "addressable")));

        //test PQ method
        String min = queue.peek();
        String minDel = queue.poll();

        assertEquals("Hello", min);
        assertEquals(min, minDel);
        assertEquals(min, minDel);
        assertFalse(queue.contains(min));

        queue.clear();

    }

    @Test
    @DisplayName("Test PQ DecreaseKey")
    void testPQDecreaseKey(){
        PriorityQueue<Integer, String> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        queue.build(2, new String[]{"This", "is", "a", "Test", "message"});
        assertEquals("This", queue.peek());

        queue.decreaseKey("Test", 1);
        assertEquals("Test", queue.peek());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Test PQ To Array")
    void testPQToArray(){
        PriorityQueue<Integer, Integer> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        queue.build(10, new Integer[]{3,3,3,3,3});
        var array = queue.toArray();

        for(var e : array){
            Tuple<Integer, Integer> tuple = (Tuple<Integer, Integer>) e;
            assertEquals(10, tuple.getKey());
            assertEquals(3, tuple.getValue());
        }
    }

    @Test
    @DisplayName("Test Sort With PQ")
    void testSortWithPQ(){
        Integer[] numbers = new Integer[]{10, 8, 3, 2, 7, 1, 9, 5, 6, 4};
        PriorityQueue<Integer, Integer> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        queue.build(20, numbers);

        for(Integer i : numbers){
            queue.decreaseKey(i, i);
        }

        int last = Integer.MIN_VALUE;
        while(!queue.isEmpty()){
            int elem = queue.poll();
            assertTrue(last < elem);
            last = elem;
        }

    }

    @Test
    @DisplayName("Test PQ Iterator")
    void testPQIterator(){
        String msg = "message.";

        PriorityQueue<Integer, String> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        queue.build(10, new String[]{"This", "is", "a", "Test", msg});
        queue.decreaseKey("is", 4);
        queue.decreaseKey("a", 1);
        queue.decreaseKey("Test", 9);
        queue.decreaseKey(msg, 5);

        //Note iterator is not sorted, but the direct heap iterator.
        String[] expected = new String[]{"a", "is", msg, "Test", "This"};
        int idx = 0;
        for (String s : queue) {
            assertEquals(s, expected[idx++]);
        }

    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Serialize PQ")
    @SuppressWarnings("unchecked")
    void testSerialize(@NotNull SerializableType type){
        Integer[] numbers = new Integer[]{10, 8, 3, 2, 7, 1, 9, 5, 6, 4};
        PriorityQueue<Integer, Integer> queue = new BinaryQueue<>(Comparator.<Integer>naturalOrder());
        queue.build(20, numbers);

        for(Integer i : numbers){
            queue.decreaseKey(i, i);
        }

        SerializedObject obj = ObjectStorage.serialize(queue, type).orElseThrow();

        BinaryQueue<Integer, Integer> read = ObjectStorage.deserialize(obj, BinaryQueue.class).orElseThrow();
        assertEquals(queue, read);

    }

}
