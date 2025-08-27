package net.configuration.deleteMe;

import org.jetbrains.annotations.NotNull;

import java.util.*;

//Template for the actual Pairing Heap advanced data structure
public class PairingHeap<K extends Comparable<? super K>, V> {

    private HeapNode<K,V> root;
    private final Map<V, List<HeapNode<K,V>>> cachedNodes = new HashMap<>();

    public boolean isEmpty(){
        return this.root == null;
    }

    public V top(){
        if(this.isEmpty())
            return null;

        return this.root.value;
    }

    public V pop(){
        if(this.isEmpty())
            return null;

        HeapNode<K,V> min = this.root;
        this.root = this.twoPassMerge(this.root.leftChild);
        return min.value;
    }

    public void merge(PairingHeap<K,V> other){
        this.root = this.merge(this.root, other.root);
    }

    public void push(@NotNull K key, V value){
        this.root = this.push(this.root, key, value);
    }

    public void updateKey(V value, @NotNull K newKey){
        HeapNode<K,V> node = this.cachedNodes.get(value).get(0);
        int cmp = newKey.compareTo(node.key);

        if(cmp < 0){
            this.decreaseKey(value, newKey);
        }else if(cmp > 0){
            this.increaseKey(value, newKey);
        }

    }

    /**
     * Increase the key for the first heap node that has the given value and with its key possible for an increase. I.e.,
     * if the heap contains the tuples {@code [(8, "This value"), (2, "This value"), (7, "This value")]} and this operation is
     * called with {@code increaseKey(5, "This Value")}, then the first entry is not possible for increase, since 8 > 5 already,
     * so it is skipped and therefore the first possible candidate (2, "This Value") is considered. Then its key will be
     * updated, in this example to 5. If there are any other possible candidates after that, then they will be ignored.
     * Moreover, if no candidate is found, i.e. no node can increase its key with the given newKey, then an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param value The value whose key should be increased.
     * @param newKey The new increased key value.
     */
    private void increaseKey(V value, @NotNull K newKey){
        List<HeapNode<K,V>> nodes = this.cachedNodes.remove(value);

        Optional<HeapNode<K,V>> opt = nodes.stream().filter(n -> newKey.compareTo(n.key) > 0).findFirst();
        if(opt.isEmpty())
            throw new IllegalArgumentException("No candidate found for decrease key. New key is larger than previous");

        HeapNode<K,V> node = opt.get();
        node.key = newKey;
        this.cachedNodes.put(value, nodes);
        //unlink children from node and merge again

        if(node.parent != null)
            node.parent.removeChild(node);

        HeapNode<K,V> next = node.nextChild;
        HeapNode<K,V> left = node.leftChild;
        if(next != null) {
            node.removeChild(next);
            this.root = this.twoPassMerge(next);
        }

        if(left != null){
            node.removeChild(left);
            this.root = this.twoPassMerge(left);
        }

        this.root = this.merge(this.root, node);
    }

    public void decreaseKey(V value, @NotNull K newKey) {
        List<HeapNode<K,V>> nodes = this.cachedNodes.remove(value);
        //find first match for decreaseKey if possible, i.e. if the heap contains three elements with the same value
        //(2, "This value"), (8, "This value"), (7, "This value"); and the new key is set to 5, then the entry with key
        //2 is ignored since it cannot be decreased here and the first fit (8, "This value") is chosen for the decreaseKey

        Optional<HeapNode<K,V>> opt = nodes.stream().filter(n -> newKey.compareTo(n.key) < 0).findFirst();
        if(opt.isEmpty())
            throw new IllegalArgumentException("No candidate found for decrease key. New key is larger than previous");

        HeapNode<K,V> node = opt.get();
        node.key = newKey;
        this.cachedNodes.put(value, nodes);

        if(node.parent != null){
            //if the parent is null, then node is the root, so we are done, so consider the other case here
            node.parent.removeChild(node);
            this.root = this.merge(this.root, node);
        }
    }

    private HeapNode<K,V> push(HeapNode<K,V> node, @NotNull K key, V value){
        HeapNode<K,V> newNode = new HeapNode<>(key, value, null, null);
        if(this.cachedNodes.containsKey(value)){
            this.cachedNodes.get(value).add(newNode);
        }else{
            this.cachedNodes.put(value, new ArrayList<>(List.of(newNode)));
        }

        return this.merge(node, newNode);
    }

    private HeapNode<K,V> merge(HeapNode<K,V> a, HeapNode<K,V> b){
        if(a == null) return b;
        if(b == null) return a;

        if(a.key.compareTo(b.key) < 0){
            a.addChild(b);
            return a;

        }else{
            b.addChild(a);
            return b;
        }
    }

    private HeapNode<K,V> twoPassMerge(HeapNode<K,V> node){
        if(node == null || node.nextChild == null){
            return node;
        }

        HeapNode<K,V> b = node.nextChild;
        HeapNode<K,V> newNode = node.nextChild.nextChild;

        node.nextChild = null;
        b.nextChild = null;

        return this.merge(this.merge(node, b), this.twoPassMerge(newNode));
    }


    private static class HeapNode<K extends Comparable<? super K>, V> {

        @NotNull private K key;
        private final V value;
        private HeapNode<K,V> leftChild;
        private HeapNode<K,V> nextChild;
        private HeapNode<K,V> parent;

        public HeapNode(@NotNull K key, V value, HeapNode<K,V> leftChild, HeapNode<K,V> nextChild){
            this.key = key;
            this.value = value;
            this.leftChild = leftChild;
            this.nextChild = nextChild;
        }


        public void removeChild(@NotNull HeapNode<K,V> child) {
            if (child.equals(this.leftChild)) {
                this.leftChild = this.leftChild.nextChild;
            } else if (child.equals(this.nextChild)) {
                this.nextChild = null;
            }

            child.parent = null;
        }

        public void addChild(final @NotNull HeapNode<K,V> node) {
            if (this.leftChild != null) {
                // Find the end of node's nextChild chain to attach this.leftChild
                HeapNode<K,V> temp = node;
                while (temp.nextChild != null) {
                    temp = temp.nextChild;
                }

                //remove temp from its current tree to avoid circular dependencies
                if(temp.parent != null)
                    temp.parent.removeChild(temp);

                // Attach current left child to the end of the new node's nextChild chain
                temp.nextChild = this.leftChild;
            }

            this.leftChild = node;
            node.parent = this;

        }

        @Override
        public boolean equals(Object o) {
            if(o == null) return false;
            if (this == o) return true;
            if(!HeapNode.class.isAssignableFrom(o.getClass())) return false;


            var heapNode = this.getClass().cast(o);
            return key.equals(heapNode.key) && Objects.equals(value, heapNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

}
