package structures;
import java.util.ArrayList;

public class PriorityQueue<T extends Comparable<T>> {
    protected int initialCapacity;
    public ArrayList<T> queueList;

    public PriorityQueue(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        queueList = new ArrayList<>(initialCapacity);
    }

     public PriorityQueue(ArrayList<T> queue) {
        this.initialCapacity = queue.size();
        queueList = new ArrayList<>(queue);
    }

    // Adds a new element
    public boolean add(T e) {
        queueList.add(e);
        siftUp(e);
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
        siftDown(last);

        return temp;
    }

    // Removes the specified object from the list
    public boolean remove(T object) {
        siftDown(object);
        return true;
    }

    private void siftUp(T object) {
        int newPositionIndex = queueList.size() - 1;

        while(isElementNotARoot(newPositionIndex) && isParentGreaterThanNewElement(newPositionIndex, object)) {
            copyParentToNewLocation(newPositionIndex);
            newPositionIndex = parent(newPositionIndex);
        }

        queueList.set(newPositionIndex, object);
    }

    private boolean isElementNotARoot(int index) {
        return index != 0;
    }

    private boolean isParentGreaterThanNewElement(int index, T object) {
        int parentIndex = parent(index);
        T parentElement = queueList.get(parentIndex);
        return parentElement.compareTo(object) > 0;
    }

    private void copyParentToNewLocation(int index) {
        int parentIndex = parent(index);
        queueList.set(index, queueList.get(parentIndex));
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private void siftDown(T object) {
        int lastElementIndex = 0;
        queueList.set(0, object);

        while(isGreatestChild(object, lastElementIndex)) {
            moveSmallestChild(lastElementIndex);
            lastElementIndex = smallestChild(lastElementIndex);
        }

        queueList.set(lastElementIndex, object);
    }

    // Checks if the child is the greatest 
    private boolean isGreatestChild(T object, int index) {
        T left = leftChild(index);
        T right = rightChild(index);

        if(left != null && object.compareTo(left) > 0) return true;
        if(right != null && object.compareTo(right) > 0) return true;
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

        return left.compareTo(right) < 0 ? leftIndex : rightIndex;
    } 
    
}