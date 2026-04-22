package structures;
import java.util.ArrayList;

public class PriorityQueue<T extends Comparable<T>> {
    public static interface CustomComparator<T extends Comparable<T>> {
        int compare(T objectA, T objectB);
    }

    protected int initialCapacity;
    private CustomComparator<T> comparator;
    public ArrayList<T> queueList;
    
    public PriorityQueue(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.comparator = null;
        queueList = new ArrayList<>(initialCapacity);
    }

    public PriorityQueue(ArrayList<T> queue) {
        this.initialCapacity = queue.size();
        this.comparator = null;
        queueList = new ArrayList<>(queue);

        for (int i = (queueList.size() / 2) - 1; i >= 0; i--) {
            siftDown(queueList.get(i), i);
        }
    }

    public PriorityQueue(CustomComparator<T> comparator) {
        this.initialCapacity = 0;
        this.comparator = comparator;
        queueList = new ArrayList<>(initialCapacity);
    }

    public PriorityQueue(ArrayList<T> list, CustomComparator<T> comparator) {

        this.initialCapacity = list.size();
        queueList = new ArrayList<>(list);
        this.comparator = comparator;

        for (int i = (queueList.size() / 2) - 1; i >= 0; i--) {
            siftDown(queueList.get(i), i);
        }
    }

    // Adds a new element
    public boolean add(T e) {
        queueList.add(e);
        siftUp(e, queueList.size() - 1);
        return true;
    }

    public int size() {
        return queueList.size();
    }

    public boolean isEmpty() {
        return queueList.isEmpty();
    }

    // Clears the queue
    public void clear() {
        this.queueList.clear();
    }
    
    // Retrieves the head of the queue
    public T peek() {
        if(queueList.isEmpty()) return null;
        return queueList.get(0);
    }

    // Retrives the head of the queue and removes it
    public T poll() {
        if(queueList.isEmpty()) return null;

        // Get first and last item
        T temp = queueList.get(0);
        T last = queueList.remove(queueList.size() - 1);

        // Check again bc it will crash once the last element is removed
        if(queueList.isEmpty()) return temp;

        // Remove the last item and rearrange the list
        queueList.set(0, last);
        siftDown(last, 0);

        return temp;
    }

    // Removes the specified object from the list
    public boolean remove(T object) {
        int index = queueList.indexOf(object);
        if(index == -1) return false;
        
        int lastIndex = queueList.size() - 1;
        T last = queueList.remove(lastIndex);
        if(index == lastIndex) return true;

        queueList.set(index, last);

        int parentIndex = parent(index);
        if(index > 0 && compare(last, queueList.get(parentIndex)) < 0) siftUp(last, index);
        else siftDown(last, index);

        return true;
    }

    // Retrives the head of the queue and removes it
    public T removeLast() {
        if(queueList.isEmpty()) return null;

        // Get last item
        T last = queueList.remove(queueList.size() - 1);

        // Check again bc it will crash once the last element is removed
        if(queueList.isEmpty()) return last;

        // Remove the last item and rearrange the list
        return last;
    }

    private void siftUp(T object, int index) {
        while(isElementNotARoot(index) && isParentGreaterThanNewElement(index, object)) {
            copyParentToNewLocation(index);
            index = parent(index);
        }

        queueList.set(index, object);
    }

    private boolean isElementNotARoot(int index) {
        return index != 0;
    }

    private boolean isParentGreaterThanNewElement(int index, T object) {
        int parentIndex = parent(index);
        T parentElement = queueList.get(parentIndex);

        return compare(parentElement, object) > 0;
    }

    private void copyParentToNewLocation(int index) {
        int parentIndex = parent(index);
        queueList.set(index, queueList.get(parentIndex));
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private void siftDown(T object, int index) {
        queueList.set(index, object);

        while(isGreatestChild(object, index)) {
            moveSmallestChild(index);
            index = smallestChild(index);
        }

        queueList.set(index, object);
    }

    // Checks if the child is the greatest 
    private boolean isGreatestChild(T object, int index) {
        T left = leftChild(index);
        T right = rightChild(index);

        if(left != null && compare(object, left) > 0) return true;
        if(right != null && compare(object, right) > 0) return true;
        return false;
    }
    
    // Gets the child to the left of the root
    private T leftChild(int index) {
        int leftIndex = 2 * index + 1;
        return leftIndex < queueList.size() ? queueList.get(leftIndex) : null;
    }

    // Gets the child to the right of the root
     private T rightChild(int index) {
        int rightIndex = 2 * index + 2;
        return rightIndex < queueList.size() ? queueList.get(rightIndex) : null;
    }

    private void moveSmallestChild(int index) {
        int smallestIndex = smallestChild(index);
        queueList.set(index, queueList.get(smallestIndex));
    }

    private int smallestChild(int index) {
        int leftIndex = 2 * index + 1;
        int rightIndex = 2 * index + 2;

        if(rightIndex >= queueList.size()) {
            return leftIndex;
        }

        T left = queueList.get(leftIndex);
        T right = queueList.get(rightIndex);

        return compare(left, right) < 0 ? leftIndex : rightIndex;
        
    } 

    private int compare(T a, T b) {
        if(comparator != null) {
            return comparator.compare(a,b);
        } 

        return a.compareTo(b);
    }
    
}