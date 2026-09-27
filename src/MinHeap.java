public class MinHeap {
    private int[] heap;
    private int size;

    public MinHeap() {
        this.heap = new int[10]; // Initial capacity
        this.size = 0;
    }

    private int parent(int index) { return (index - 1) / 2; }
    private int leftChild(int index) { return (2 * index) + 1; }
    private int rightChild(int index) { return (2 * index) + 2; }

    private void resize() {
        int[] newHeap = new int[heap.length * 2];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        this.heap = newHeap;
    }

    private void swap(int index1, int index2) {
        int temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    public void insert(int x) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = x;
        size++;
        heapifyUp(size - 1);
    }

    private void heapifyUp(int index) {
        int current = index;
        while (current > 0 && heap[current] < heap[parent(current)]) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        return heap[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        int min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return min;
    }

    private void heapifyDown(int index) {
        int current = index;
        while (leftChild(current) < size) {
            int smallestChildIndex = leftChild(current);

            // Check if right child exists and is smaller than left child
            if (rightChild(current) < size && heap[rightChild(current)] < heap[smallestChildIndex]) {
                smallestChildIndex = rightChild(current);
            }

            // If the current node is smaller than the smallest child, the heap property is restored
            if (heap[current] <= heap[smallestChildIndex]) {
                break;
            }

            swap(current, smallestChildIndex);
            current = smallestChildIndex;
        }
    }

    public int size() {
        return size;
    }
}