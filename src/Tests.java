import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class Tests {

    public static void main(String[] args) {
        System.out.println("Starting tests...");

        testDynamicArray();
        System.out.println("DynamicArray tests passed.");

        testLinkedList();
        System.out.println("LinkedList tests passed.");

        testMinHeap();
        System.out.println("MinHeap tests passed.");

        System.out.println("All validation tests completed successfully!");
    }

    private static void assertTest(boolean condition, String errorMessage) {
        if (!condition) {
            throw new RuntimeException("TEST FAILED: " + errorMessage);
        }
    }

    private static void testDynamicArray() {
        DynamicArray arr = new DynamicArray();

        // 1. Empty structure & Invalid indices
        assertTest(arr.size() == 0, "Initial size should be 0");
        assertTest(!arr.contains(5), "Empty array should not contain 5");

        boolean thrown = false;
        try { arr.get(0); } catch (IndexOutOfBoundsException e) { thrown = true; }
        assertTest(thrown, "Getting from empty array should throw exception");

        thrown = false;
        try { arr.remove(-1); } catch (IndexOutOfBoundsException e) { thrown = true; }
        assertTest(thrown, "Negative index removal should throw exception");

        // 2. One element
        arr.add(10);
        assertTest(arr.size() == 1, "Size should be 1 after one addition");
        assertTest(arr.get(0) == 10, "Element at index 0 should be 10");
        assertTest(arr.contains(10), "Array should contain 10");

        // 3. Multiple elements & Duplicate values
        arr.add(20);
        arr.add(10); // Duplicate
        assertTest(arr.size() == 3, "Size should be 3");
        assertTest(arr.get(2) == 10, "Duplicate value should be stored at index 2");

        // 4. Boundary indices
        arr.add(0, 5); // Add at beginning
        assertTest(arr.get(0) == 5, "Element at index 0 should be 5 after front insertion");
        assertTest(arr.get(1) == 10, "Previous element shifted to index 1");

        arr.add(arr.size(), 30); // Add at end boundary
        assertTest(arr.get(arr.size() - 1) == 30, "Last element should be 30");

        int removedFirst = arr.remove(0); // Remove from beginning
        assertTest(removedFirst == 5, "Removed element should be 5");
        int removedLast = arr.remove(arr.size() - 1); // Remove from end
        assertTest(removedLast == 30, "Removed element should be 30");

        // 5. Large inputs (Validation against Java's ArrayList)
        DynamicArray largeArr = new DynamicArray();
        ArrayList<Integer> javaList = new ArrayList<>();
        Random rand = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int val = rand.nextInt();
            largeArr.add(val);
            javaList.add(val);
        }

        assertTest(largeArr.size() == javaList.size(), "Large array sizes should match");
        for (int i = 0; i < 10000; i++) {
            assertTest(largeArr.get(i) == javaList.get(i), "Large array elements should match Java ArrayList at index " + i);
        }
    }

    private static void testLinkedList() {
        LinkedList list = new LinkedList();

        // 1. Empty structure & Invalid indices
        assertTest(list.size() == 0, "Initial size should be 0");

        boolean thrown = false;
        try { list.add(1, 10); } catch (IndexOutOfBoundsException e) { thrown = true; }
        assertTest(thrown, "Adding out of bounds should throw exception");

        // 2. One element
        list.add(100);
        assertTest(list.size() == 1, "Size should be 1");
        assertTest(list.get(0) == 100, "Element at index 0 should be 100");

        // 3. Multiple elements & Duplicate values
        list.add(200);
        list.add(100);
        assertTest(list.size() == 3, "Size should be 3");
        assertTest(list.get(2) == 100, "Duplicate element should be 100");

        // 4. Boundary indices
        list.add(0, 50); // Add at head
        assertTest(list.get(0) == 50, "Head should be 50");

        list.add(list.size(), 300); // Add at tail
        assertTest(list.get(list.size() - 1) == 300, "Tail should be 300");

        int removedHead = list.remove(0); // Remove head
        assertTest(removedHead == 50, "Removed head should be 50");

        int removedTail = list.remove(list.size() - 1); // Remove tail
        assertTest(removedTail == 300, "Removed tail should be 300");

        // 5. Large inputs (Triggering multiple nodes)
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        assertTest(list.size() == 1003, "Size should properly track large additions");
    }

    private static void testMinHeap() {
        MinHeap heap = new MinHeap();

        // 1. Empty structure
        assertTest(heap.size() == 0, "Heap should be initially empty");

        boolean thrown = false;
        try { heap.peekMin(); } catch (IllegalStateException e) { thrown = true; }
        assertTest(thrown, "Peeking empty heap should throw exception");

        thrown = false;
        try { heap.extractMin(); } catch (IllegalStateException e) { thrown = true; }
        assertTest(thrown, "Extracting from empty heap should throw exception");

        // 2. One element
        heap.insert(50);
        assertTest(heap.size() == 1, "Size should be 1");
        assertTest(heap.peekMin() == 50, "Min element should be 50");

        // 3. Multiple elements & Duplicate values
        heap.insert(30);
        heap.insert(40);
        heap.insert(30); // Duplicate
        heap.insert(10);

        // Verifying heap property is maintained after insertion
        assertTest(heap.peekMin() == 10, "Min element should be 10 after multiple insertions");

        // 4. Verifying non-decreasing order on extraction (Heap Property validation)
        int prev = heap.extractMin();
        assertTest(prev == 10, "First extracted should be 10");

        int current = heap.extractMin();
        assertTest(current == 30, "Second extracted should be 30");
        assertTest(prev <= current, "Extracted elements should be in non-decreasing order");

        prev = current;
        current = heap.extractMin();
        assertTest(current == 30, "Third extracted should be 30 (duplicate)");
        assertTest(prev <= current, "Extracted elements should be in non-decreasing order");

        // 5. Large inputs & Java Standard Collection Validation
        MinHeap largeHeap = new MinHeap();
        PriorityQueue<Integer> javaPQ = new PriorityQueue<>();
        Random rand = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int val = rand.nextInt(100000);
            largeHeap.insert(val);
            javaPQ.add(val);
        }

        assertTest(largeHeap.size() == javaPQ.size(), "Large heap sizes should match");

        // Verify extractMin() against Java's PriorityQueue poll()
        for (int i = 0; i < 10000; i++) {
            int customMin = largeHeap.extractMin();
            int javaMin = javaPQ.poll();
            assertTest(customMin == javaMin, "Heap extraction mismatch at iteration " + i + ". Expected " + javaMin + ", got " + customMin);
        }

        assertTest(largeHeap.size() == 0, "Heap should be empty after extracting all elements");
    }
}