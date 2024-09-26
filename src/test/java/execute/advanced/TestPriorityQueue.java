package execute.advanced;

import net.configuration.advanced.BinaryQueue;
import net.configuration.advanced.PriorityQueue;
import net.configuration.advanced.Tuple;
import net.configuration.serializable.api.ObjectStorage;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPriorityQueue {

    @ParameterizedTest
    @EnumSource(PQTypes.class)
    @DisplayName("Test Build PQ")
    void testBuildPQ(@NotNull PQTypes type){
        PriorityQueue<Integer, String> queue = type.createNew(Comparator.naturalOrder(), 2, String.class);
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
        assertFalse(queue.contains(min));

        queue.clear();

    }

    @ParameterizedTest
    @EnumSource(PQTypes.class)
    @DisplayName("Test PQ DecreaseKey")
    void testPQDecreaseKey(@NotNull PQTypes type){
        PriorityQueue<Integer, String> queue =  type.createNew(Comparator.naturalOrder(), 2, String.class);
        queue.build(2, new String[]{"This", "is", "a", "Test", "message"});
        assertEquals("This", queue.peek());

        queue.decreaseKey("Test", 1);
        assertEquals("Test", queue.peek());
    }

    @ParameterizedTest
    @EnumSource(PQTypes.class)
    @SuppressWarnings("unchecked")
    @DisplayName("Test PQ To Array")
    void testPQToArray(@NotNull PQTypes type){
        PriorityQueue<Integer, Integer> queue = type.createNew(Comparator.naturalOrder(), 2, Integer.class);
        queue.build(9, new Integer[]{3,3,3,3,3});
        var array = queue.toArray();

        for(var e : array){
            Tuple<Integer, Integer> tuple = (Tuple<Integer, Integer>) e;
            assertEquals(9, tuple.getKey());
            assertEquals(3, tuple.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(PQTypes.class)
    @DisplayName("Test Sort With PQ")
    void testSortWithPQ(@NotNull PQTypes type){
        Integer[] numbers = new Integer[]{0, 8, 3, 2, 7, 1, 9, 5, 6, 4};
        PriorityQueue<Integer, Integer> queue = type.createNew(Comparator.naturalOrder(), 20, Integer.class);
        queue.build(10, numbers);

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
