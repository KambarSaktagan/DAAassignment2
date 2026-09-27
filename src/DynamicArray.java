public class DynamicArray {
    private int[] data;
    private int size;

    public DynamicArray() {
        this.data = new int[10]; // Initial capacity
        this.size = 0;
    }

    // Doubles the array capacity when full
    private void resize() {
        int[] newData = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        this.data = newData;
    }

    public void add(int x) {
        if (size == data.length) {
            resize();
        }
        data[size] = x;
        size++;
    }

    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == data.length) {
            resize();
        }
        // Shift elements to the right
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = x;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int removedElement = data[index];
        // Shift elements to the left
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        return removedElement;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }
}