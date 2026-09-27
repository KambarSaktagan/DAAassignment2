# Assignment 2: Algorithmic Analysis, Correctness, and Performance Trade-offs

## 1. Overview
This project provides custom Java implementations of three fundamental data structures: a **Dynamic Array**, a singly **Linked List**, and a binary **Min-Heap**. The objective is to evaluate theoretical asymptotic complexity against empirical measurements across four controlled workloads, analyze algorithmic correctness using formal loop invariants, and explore architectural factors affecting real-world software performance (such as CPU cache locality and reference indirection).

---

## 2. Complexity Analysis

### Complexity Table
| Data Structure | Operation | Best Case | Average Case | Worst Case | Auxiliary Space |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Dynamic Array** | `add(x)` | Theta(1) | Theta(1) | Theta(n) | Theta(1) (amortized) |
| | `add(index, x)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| | `remove(index)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| | `get(index)` | Theta(1) | Theta(1) | Theta(1) | Theta(1) |
| | `contains(x)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| **Linked List** | `add(x)` | Theta(1) | Theta(1) | Theta(1) | Theta(1) |
| | `add(index, x)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| | `remove(index)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| | `get(index)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| | `contains(x)` | Theta(1) | Theta(n) | Theta(n) | Theta(1) |
| **Min-Heap** | `insert(x)` | Theta(1) | Theta(log n) | Theta(log n) | Theta(1) |
| | `peekMin()` | Theta(1) | Theta(1) | Theta(1) | Theta(1) |
| | `extractMin()` | Theta(1) | Theta(log n) | Theta(log n) | Theta(1) |

### Justifications
* **Dynamic Array:** Elements occupy contiguous memory. Random access via `get(index)` computes the physical memory address as `base + index * size` in Theta(1) time. `add(x)` runs in amortized Theta(1) because capacity doubling occurs exponentially infrequently (O(n) cost spread over n additions). Adding or removing at arbitrary indices requires shifting up to n adjacent elements right or left, resulting in Theta(n) operations.
* **Linked List:** Nodes are non-contiguous objects connected via references. Adding to the tail with a maintained tail pointer is Theta(1). Accessing by index (`get`) or searching (`contains`) requires sequential pointer traversal starting at the head node, requiring on average n/2 steps (Theta(n)). Inserting or removing at the head (index 0) requires updating node references only, taking Theta(1).
* **Min-Heap:** Represented as a complete binary tree inside a flat array where indices satisfy `left(i) = 2i + 1` and `right(i) = 2i + 2`. The tree height is bounded by floor(log2(n)). `insert` appends to the next open leaf and restores heap order by bubbling up at most log2(n) levels. `extractMin` swaps the root with the last element and bubbles it down by comparing against child nodes up to log2(n) times.

---

## 3. Correctness Proofs (Loop Invariants)

### Operation 1: Linear Search (`DynamicArray.contains`)

```java
public boolean contains(int x) {
    for (int i = 0; i < size; i++) {
        if (data[i] == x) {
            return true;
        }
    }
    return false;
}
```

* **Loop Invariant:** At the start of each iteration of the `for` loop, the search target `x` does not appear in the subarray `data[0 ... i-1]`.
* **Initialization:** Prior to the first iteration, `i = 0`. The subarray `data[0 ... -1]` is empty. An empty set contains no elements, so `x` trivially does not exist in it. The invariant holds.
* **Maintenance:** Assume the invariant holds at the start of iteration `i`, meaning `x` is not in `data[0 ... i-1]`. During iteration `i`, the algorithm evaluates whether `data[i] == x`:
  * If `data[i] == x`, the method immediately returns `true`, which is correct since `x` is found at index `i`.
  * If `data[i] != x`, the loop completes the iteration and increments `i` to `i+1`. Since `x` is not in `data[0 ... i-1]` and `data[i] != x`, it follows that `x` is not in `data[0 ... i]`. The invariant is maintained for the next iteration.
* **Termination:** The loop terminates when `i = size` without having found a match.
* **Correctness:** At termination, `i = size`. Substituting this into the invariant confirms that `x` does not appear anywhere in `data[0 ... size-1]`. The method returns `false`, which correctly identifies that the element is absent.

---

### Operation 2: Heap Extraction Sift-Down (`MinHeap.heapifyDown`)

```java
private void heapifyDown(int index) {
    int current = index;
    while (leftChild(current) < size) {
        int smallestChildIndex = leftChild(current);
        if (rightChild(current) < size && heap[rightChild(current)] < heap[smallestChildIndex]) {
            smallestChildIndex = rightChild(current);
        }
        if (heap[current] <= heap[smallestChildIndex]) {
            break;
        }
        swap(current, smallestChildIndex);
        current = smallestChildIndex;
    }
}
```

* **Loop Invariant:** At the start of each iteration of the `while` loop, the binary trees rooted at every node in the heap satisfy the min-heap property (`A[parent] <= A[child]`), with the single possible exception of the subtree rooted at `current`.
* **Initialization:** Prior to the first iteration, `extractMin()` moved the last leaf in the heap to root index `0` and decremented the size. Since the subtrees rooted at indices `1` and `2` were valid min-heaps and remained untouched, the heap property holds everywhere except potentially at index `0`. Thus, at `current = 0`, the invariant holds.
* **Maintenance:** At iteration step `current`:
  * If `heap[current] <= heap[smallestChildIndex]`, the value at `current` is smaller than or equal to both its children. The heap property is satisfied globally, and the loop breaks.
  * If `heap[current] > heap[smallestChildIndex]`, swapping `heap[current]` with `heap[smallestChildIndex]` ensures that the new parent is strictly smaller than both children. The only node that might now violate the heap property is the swapped element in its new position at `smallestChildIndex`. Setting `current = smallestChildIndex` restores the invariant for the next iteration.
* **Termination:** The loop terminates when either `leftChild(current) >= size` (the node is a leaf and has no children to violate the property) or `heap[current] <= heap[smallestChildIndex]`.
* **Correctness:** Upon termination, `current` is either a leaf or satisfies the min-heap condition relative to its children. Because all other subtrees were invariant-maintained, the entire tree satisfies the min-heap property globally.

---

## 4. Experimental Setup
* **Input Sizes (n):** 100, 1,000, 10,000, 100,000.
* **Repetitions:** 5 independent iterations per workload; reported values are the arithmetic mean execution time.
* **Timing Mechanism:** `System.nanoTime()` wrapping exclusively the workload operation loops. Input data generation and console printing were excluded from timing.
* **Randomness Control:** Fixed pseudo-random seed (`Random(42)`) to ensure identical test sequences across executions.

---

## 5. Results

### Workload 1: Random Access (`10,000 get` operations)
| n | Array Time (ns) | List Time (ns) | Array Accesses | List Accesses | Theoretical Complexity |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 100 | 323,820 | 1,913,100 | 10,000 | 500,000 | Theta(1) / Theta(n) |
| 1,000 | 20,880 | 8,484,640 | 10,000 | 5,000,000 | Theta(1) / Theta(n) |
| 10,000 | 26,200 | 97,028,000 | 10,000 | 50,000,000 | Theta(1) / Theta(n) |
| 100,000 | 34,620 | 700,904,700 | 10,000 | 500,000,000 | Theta(1) / Theta(n) |

### Workload 2: Search (`1,000 contains` operations)
| n | Array Time (ns) | List Time (ns) | Total Comparisons | Theoretical Complexity |
| :--- | :--- | :--- | :--- | :--- |
| 100 | 311,760 | 249,360 | 100,000 | Theta(n) |
| 1,000 | 863,600 | 1,639,780 | 1,000,000 | Theta(n) |
| 10,000 | 1,472,300 | 13,562,880 | 10,000,000 | Theta(n) |
| 100,000 | 14,920,520 | 139,907,880 | 100,000,000 | Theta(n) |

### Workload 3: Insertions and Removals (1,000 operations)
| n | Arr Ins(0) (ns) | List Ins(0) (ns) | Arr Rem(0) (ns) | List Rem(0) (ns) | Arr Ins(Mid) (ns) | List Ins(Mid) (ns) | Arr Rem(Mid) (ns) | List Rem(Mid) (ns) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 100 | 727,460 | 60,380 | 1,214,020 | 40,840 | 391,260 | 605,920 | 229,780 | 724,880 |
| 1,000 | 137,320 | 33,260 | 113,600 | 22,060 | 93,760 | 1,123,100 | 97,620 | 1,049,240 |
| 10,000 | 591,460 | 11,180 | 478,040 | 3,680 | 265,620 | 7,036,940 | 257,800 | 7,750,500 |
| 100,000 | 5,060,120 | 4,920 | 4,321,020 | 3,360 | 2,376,700 | 66,843,080 | 2,455,420 | 67,109,680 |

### Workload 4: Priority Processing (Min-Heap)
| n | Insert Total (ns) | Extract Total (ns) | Est. Insert Comparisons | Est. Extract Comparisons | Theoretical Complexity |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 100 | 30,800 | 84,120 | 664 | 1,328 | Theta(n log n) |
| 1,000 | 84,860 | 170,380 | 9,965 | 19,931 | Theta(n log n) |
| 10,000 | 427,560 | 649,300 | 132,877 | 265,754 | Theta(n log n) |
| 100,000 | 1,838,660 | 6,727,300 | 1,660,964 | 3,321,928 | Theta(n log n) |

### Experimental Plots
![Plot 1: Execution Time vs. n]
<img width="3314" height="1695" alt="plot1" src="https://github.com/user-attachments/assets/2afcefeb-a37f-4437-b738-896f2209ee7c" />

*Figure 1: Workload Execution Time scaling over increasing input sizes.*

![Plot 2: Operations vs. n]
<img width="3318" height="1657" alt="plot2" src="https://github.com/user-attachments/assets/995c61d1-a71a-4640-ae43-4db1136e056e" />

*Figure 2: Empirical operation and traversal count scaling over increasing input sizes.*

---

## 6. Discussion

#### 1. How does increasing n affect each workload?
* **Workload 1:** Dynamic Array random access remains essentially constant (~20-35 us), whereas Linked List scales strictly linearly with n, rising from 1.9 ms at n=100 to over 700 ms at n=100,000.
* **Workload 2:** Both structures scale linearly with n because searching an unsorted sequence requires checking elements until a match is found.
* **Workload 3:** Head operations on Linked List remain constant-time regardless of n. In contrast, Dynamic Array head operations scale linearly because every element must shift. For middle operations, Linked List scales linearly due to node traversal overhead, while Dynamic Array requires shifting n/2 elements.
* **Workload 4:** Insertion and extraction times grow at an O(n log n) rate, demonstrating steady logarithmic scaling per item.

#### 2. Which experimental results agree with the theoretical complexity?
The asymptotic trends strongly agree with theory:
* Dynamic Array `get` matches Theta(1).
* Linked List `get` and `contains` match Theta(n).
* Linked List head operations match Theta(1).
* Min-Heap operations scale with tree height Theta(log n), accumulating to Theta(n log n) for n items.

#### 3. Where do the experimental results differ from the theoretical prediction?
At n=100, execution times are often higher than at n=1,000 (e.g., Dynamic Array `get` took 323 us at n=100 vs 20 us at n=1,000). This divergence is caused by **JVM warmup effects**: class loading, bytecode interpretation, and Just-In-Time (JIT) compilation overhead occur during initial passes before hotspot optimizations take effect.

#### 4. Why can two algorithms with the same Big-O complexity have different running times?
Both Dynamic Array and Linked List search in O(n) time during Workload 2, but Dynamic Array ran approximately 9x faster at n=100,000 (14.9 ms vs 139.9 ms). Asymptotic notation drops lower-order terms and constant coefficients (c * n). Dynamic Array benefits from smaller constant factors due to sequential hardware cache lines, whereas Linked List traverses non-contiguous memory pointers that cause frequent CPU cache misses.

#### 5. How do constant factors and implementation details affect performance?
Contiguous arrays exploit CPU spatial locality: fetching `data[i]` pulls adjacent values into the L1/L2 cache automatically. Linked Lists store `Node` objects scattered across the heap, requiring pointer dereferencing on every step and incurring higher memory footprint per element (node references + object headers).

#### 6. Why is a Dynamic Array preferable for some workloads?
It provides O(1) direct index calculations and minimal memory overhead per element, making it ideal for workloads dominated by reads, sorting, index lookup, or appending to the end.

#### 7. When can a Linked List be useful?
When operations are restricted to insertions and deletions at the extremities (such as implementing Stacks or Queues via head/tail pointers), as these run in deterministic O(1) time without resizing or array shifts.

#### 8. Why is a Heap appropriate for priority-based processing?
A heap avoids the O(n) insertion cost of an ordered list and the O(n) extraction cost of an unsorted list. It provides a balanced compromise: both `insert` and `extractMin` run in guaranteed logarithmic time (O(log n)), allowing priorities to be managed efficiently.

#### 9. How does the workload influence the choice of data structure?
Structure choice depends entirely on access patterns:
* Frequent indexed retrieval -> **Dynamic Array**.
* Continuous head/tail modifications without random access -> **Linked List**.
* Dynamic priority retrieval -> **Min-Heap**.

---

## 7. Design Recommendations
* **Random Access / Batch Queries:** Use Dynamic Arrays. Contiguous allocation optimizes CPU cache usage.
* **FIFO Queue / Front-Heavy Streaming:** Use Linked Lists with head and tail pointers. Eliminates shifting costs.
* **Task Schedulers / Event Simulation:** Use Min-Heaps. Logarithmic insertion and extraction prevent throughput bottlenecks.

---

## 8. Conclusion
The experimental results validate theoretical asymptotic bounds once JVM warmup transients are accounted for. Dynamic Arrays offer performance advantages in read-heavy applications, while Linked Lists provide constant-time modifications at the endpoints. The Min-Heap provides predictable O(log n) performance for priority queues.
