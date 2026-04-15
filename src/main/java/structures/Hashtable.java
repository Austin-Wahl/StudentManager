package structures;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Hashtable <K, V> {
    /**
     * Nodes store a key,value, and next. This allows more than one value to have the same index.
     */
    private static class Node<K, V> {
        protected K key = null;
        protected V value = null;
        protected Node<K,V> next = null;

        public Node(K key, V value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;      
        }
    }

    private ArrayList<Node<K,V>> nodes;
    private int size;
    private int capacity;
    private final float loadFactor = 0.75f;
    
    /**
     * Creates a new hashtable 
     */
    public Hashtable(int capacity) {
        // set max size
        this.capacity = capacity;

        // instantiate arrays
        this.nodes = new ArrayList<>(capacity);
        for(int i = 0; i < capacity; i++) nodes.add(null);
    }

    // Generates the index where the key points to
    private int computeListIndex(K key) {
        return Math.abs(key.hashCode() % this.capacity);
    }

    // Divided the following methods between us
    /**
     * Resets the map
     */
    public void clear() {
        nodes.clear();
        size = 0;
    }

    /**
     * Retrieves the value of a key
     */
    public V get(K key) {
       int index = computeListIndex(key);
       Node<K,V> n = nodes.get(index);
    
       while(n != null) {
        if(n.key.equals(key)) return n.value;

        n = n.next;
       }
       return null;
    }

    /**
     * Returns true if the map is empty and false if not
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserts a new value into the map
     */
    public V put(K key, V value) {
        // compute the index
        int index = computeListIndex(key);
        Node<K,V> n = nodes.get(index);

        // check if the key is already in the list
        while(n != null) {
            if(n.key.equals(key)) {
                n.value = value;
                return value;
            }
            n = n.next;
        }

        size++;
        n = nodes.get(index);
        Node<K,V> newNode = new Node<K,V>(key, value, n);
        nodes.set(index, newNode);
        
        if(size / capacity > loadFactor) {
            // save current map in temp
            ArrayList<Node<K,V>> temp = nodes;
            // double capacity and reset size
            capacity *= 2;
            size = 0;

            // instantiate a new array list
            nodes = new ArrayList<>(capacity);
            for(int i = 0; i < capacity; i++) nodes.add(null);
            
            // loop over each node and the child nodes
            for(Node<K,V> node : temp) {
                while(node != null) {
                    // call this function again recursively
                    put(node.key, node.value);
                    node = node.next;
                }
            }
        }

        return value;
    }

    /**
     * Returns the size of the map
     */
    public int size() {
        return this.size;
    }
   
    /**
     * Removes a value from the map
     */
    public V remove(K key) {
        int index = computeListIndex(key);
        Node<K,V> n = nodes.get(index);
        Node<K,V> previous = null;

        // While the current bucket exists
        while (n != null) {
            // If the buckets key == key
            if (n.key.equals(key)) {
                V removed = n.value;
                // If previous is null (so first node in a bucket)
                if (previous == null) {
                    // Set the bucket = to the next bucket
                    nodes.set(index, n.next); 
                } else {
                    // Otherwise, set the previous bucket = to the next bucket
                    previous.next = n.next; 
                }
                size--;
                return removed;
            }
            previous = n;
            n = n.next;
        }

        // If the nodes not found just return null
        return null; 
    }
    /**
     * Replaces an existing value if it exists. If the key is not present, then no value is inserted and null is returned
     */
    public boolean replace(K key, V value) {
        // compute the index
        int index = computeListIndex(key);
        Node<K,V> n = nodes.get(index);

        // check if the key is already in the list
        while(n != null) {
            if(n.key.equals(key)) {
                n.value = value;
                return true;
            }
            n = n.next;
        }

        return false;
    }
    /**
     * Returns a collection of the values of the map
     */
    public ArrayList<V> values() {
        ArrayList<V> values = new ArrayList<V>();

        for(Node<K,V> node : this.nodes) {
            Node<K,V> n = node;
            while(n != null) {
                values.add(n.value);
                n = n.next;
            }
        }

        return values;
    }

    public void forEach(BiConsumer<K,V> action) {
         for(Node<K,V> node : this.nodes) {
            Node<K,V> n = node;
            while(n != null) {
                action.accept(n.key, n.value);
                n = n.next;
            }
        }
    }
}