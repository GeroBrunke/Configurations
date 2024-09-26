package execute.advanced;

import net.configuration.advanced.*;
import net.configuration.serializable.api.SerializationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

@SuppressWarnings("rawtypes")
enum PQTypes {

    BINARY_QUEUE(BinaryQueue.class),
    BUCKET_QUEUE(BucketQueue.class),
    //FIBONACCI_HEAP(FibonacciHeap.class),
    //PAIRING_HEAP(PairingHeap.class),
    BINARY_HEAP(BinaryHeap.class);

    @NotNull private final Class<? extends PriorityQueue> clazz;

    PQTypes(@NotNull Class<? extends PriorityQueue> clazz){
        this.clazz = clazz;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <E, K extends Comparable<? super K>, T extends PriorityQueue<K, E>> T createNew(@NotNull Comparator<K> comp,
                                                                                   @NotNull K defaultKey,
                                                                                   @NotNull Class<E> elemClass){
        try{
            if(!AddressablePriorityQueue.class.isAssignableFrom(this.clazz)){
                //normal PQ
                Constructor<T> con = (Constructor<T>) this.clazz.getDeclaredConstructor(Comparator.class);
                return con.newInstance(comp);
            }
            //Addressable PQ
            Constructor<T> con = (Constructor<T>) this.clazz.getDeclaredConstructor(Comparator.class, Comparable.class, Class.class);
            return con.newInstance(comp, defaultKey, elemClass);

        }catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            throw new SerializationException(e);
        }
    }

}
