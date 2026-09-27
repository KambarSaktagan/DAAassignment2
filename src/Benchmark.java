import java.util.Random;

public class Benchmark {
    private static final int[] N_VALUES = {100, 1000, 10000, 100000};
    private static final int REPETITIONS = 5;
    private static final int SEED = 42; // Fixed seed for reproducible experiments

    public static void main(String[] args) {
        System.out.println("Starting Full Benchmark Suite...\n");
        runWorkload1();
        runWorkload2();
        runWorkload3();
        runWorkload4();
    }

    private static void runWorkload1() {
        System.out.println("--- Workload 1: Random Access (get) ---");
        System.out.printf("%-7s | %-15s | %-15s | %-20s | %-20s%n", "n", "Array Time (ns)", "List Time (ns)", "Array Accesses (O(1))", "List Accesses (O(n))");

        for (int n : N_VALUES) {
            long totalTimeArray = 0;
            long totalTimeList = 0;

            for (int r = 0; r < REPETITIONS; r++) {
                Random rand = new Random(SEED);
                DynamicArray arr = new DynamicArray();
                LinkedList list = new LinkedList();

                // Input generation not included in measured time
                for (int i = 0; i < n; i++) {
                    int val = rand.nextInt();
                    arr.add(val);
                    list.add(val);
                }

                int[] indices = new int[10000];
                for (int i = 0; i < 10000; i++) {
                    indices[i] = rand.nextInt(n);
                }

                long startArray = System.nanoTime();
                for (int index : indices) { arr.get(index); }
                totalTimeArray += (System.nanoTime() - startArray);

                long startList = System.nanoTime();
                for (int index : indices) { list.get(index); }
                totalTimeList += (System.nanoTime() - startList);
            }

            // Theoretical accesses: Array is 1 per get. List has to traverse avg n/2 nodes per get[cite: 1].
            long arrayAccesses = 10000;
            long listAccesses = 10000L * (n / 2);

            System.out.printf("n=%-5d | %-15d | %-15d | %-20d | %-20d%n",
                    n, (totalTimeArray / REPETITIONS), (totalTimeList / REPETITIONS), arrayAccesses, listAccesses);
        }
    }

    private static void runWorkload2() {
        System.out.println("\n--- Workload 2: Search (contains) ---");
        System.out.printf("%-7s | %-15s | %-15s | %-20s%n", "n", "Array Time (ns)", "List Time (ns)", "Comparisons (O(n))");

        for (int n : N_VALUES) {
            long totalTimeArray = 0;
            long totalTimeList = 0;

            for (int r = 0; r < REPETITIONS; r++) {
                Random rand = new Random(SEED);
                DynamicArray arr = new DynamicArray();
                LinkedList list = new LinkedList();

                for (int i = 0; i < n; i++) {
                    int val = rand.nextInt();
                    arr.add(val);
                    list.add(val);
                }

                int[] searchValues = new int[1000];
                for (int i = 0; i < 1000; i++) {
                    searchValues[i] = rand.nextInt();
                }

                long startArray = System.nanoTime();
                for (int val : searchValues) { arr.contains(val); }
                totalTimeArray += (System.nanoTime() - startArray);

                long startList = System.nanoTime();
                for (int val : searchValues) { list.contains(val); }
                totalTimeList += (System.nanoTime() - startList);
            }

            // Theoretical comparisons: 1000 searches * n comparisons per search (worst case miss)[cite: 1].
            long totalComparisons = 1000L * n;

            System.out.printf("n=%-5d | %-15d | %-15d | %-20d%n",
                    n, (totalTimeArray / REPETITIONS), (totalTimeList / REPETITIONS), totalComparisons);
        }
    }

    private static void runWorkload3() {
        System.out.println("\n--- Workload 3: Insertion and Removal ---");
        System.out.println("Times in nanoseconds (ns). Expected Movements/Accesses are printed below the table.");
        System.out.printf("%-7s | %-12s %-12s | %-12s %-12s | %-12s %-12s | %-12s %-12s%n",
                "n", "Arr Ins(0)", "List Ins(0)", "Arr Rem(0)", "List Rem(0)", "Arr Ins(Mid)", "List Ins(Mid)", "Arr Rem(Mid)", "List Rem(Mid)");

        for (int n : N_VALUES) {
            long arrIns0 = 0, listIns0 = 0;
            long arrRem0 = 0, listRem0 = 0;
            long arrInsMid = 0, listInsMid = 0;
            long arrRemMid = 0, listRemMid = 0;

            for (int r = 0; r < REPETITIONS; r++) {
                DynamicArray arr = new DynamicArray();
                LinkedList list = new LinkedList();
                for (int i = 0; i < n; i++) { arr.add(0); list.add(0); }

                // A. Insert at 0[cite: 1]
                long start = System.nanoTime();
                for(int i=0; i<1000; i++) arr.add(0, 1);
                arrIns0 += (System.nanoTime() - start);

                start = System.nanoTime();
                for(int i=0; i<1000; i++) list.add(0, 1);
                listIns0 += (System.nanoTime() - start);

                // B. Remove at 0 (restores original structure)[cite: 1]
                start = System.nanoTime();
                for(int i=0; i<1000; i++) arr.remove(0);
                arrRem0 += (System.nanoTime() - start);

                start = System.nanoTime();
                for(int i=0; i<1000; i++) list.remove(0);
                listRem0 += (System.nanoTime() - start);

                // C. Insert at Middle (n/2)[cite: 1]
                start = System.nanoTime();
                for(int i=0; i<1000; i++) arr.add(arr.size()/2, 1);
                arrInsMid += (System.nanoTime() - start);

                start = System.nanoTime();
                for(int i=0; i<1000; i++) list.add(list.size()/2, 1);
                listInsMid += (System.nanoTime() - start);

                // D. Remove at Middle (n/2)[cite: 1]
                start = System.nanoTime();
                for(int i=0; i<1000; i++) arr.remove(arr.size()/2);
                arrRemMid += (System.nanoTime() - start);

                start = System.nanoTime();
                for(int i=0; i<1000; i++) list.remove(list.size()/2);
                listRemMid += (System.nanoTime() - start);
            }
            System.out.printf("n=%-5d | %-12d %-12d | %-12d %-12d | %-12d %-12d | %-12d %-12d%n",
                    n, (arrIns0/REPETITIONS), (listIns0/REPETITIONS), (arrRem0/REPETITIONS), (listRem0/REPETITIONS),
                    (arrInsMid/REPETITIONS), (listInsMid/REPETITIONS), (arrRemMid/REPETITIONS), (listRemMid/REPETITIONS));
        }
        System.out.println("> Metrics logic for Plot 2[cite: 1]:");
        System.out.println("  - Array Insert/Remove at 0: ~1000 * n element movements.");
        System.out.println("  - List Insert/Remove at 0: exactly 1000 accesses (O(1)).");
        System.out.println("  - Array Insert/Remove at Mid: ~1000 * (n/2) element movements.");
        System.out.println("  - List Insert/Remove at Mid: ~1000 * (n/2) accesses to traverse.");
    }

    private static void runWorkload4() {
        System.out.println("\n--- Workload 4: Priority Processing (Min-Heap) ---");
        System.out.printf("%-7s | %-15s | %-15s | %-20s | %-20s%n", "n", "Insert Time(ns)", "Extract Time(ns)", "Est. Inserts O(n log n)", "Est. Extracts O(n log n)");

        for (int n : N_VALUES) {
            long totalInsertTime = 0;
            long totalExtractTime = 0;

            for (int r = 0; r < REPETITIONS; r++) {
                Random rand = new Random(SEED);
                MinHeap heap = new MinHeap();
                int[] valuesToInsert = new int[n];

                for (int i = 0; i < n; i++) {
                    valuesToInsert[i] = rand.nextInt();
                }

                // Measure Insertions[cite: 1]
                long startInsert = System.nanoTime();
                for (int val : valuesToInsert) {
                    heap.insert(val);
                }
                totalInsertTime += (System.nanoTime() - startInsert);

                // Measure Extractions[cite: 1]
                long startExtract = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    heap.extractMin();
                }
                totalExtractTime += (System.nanoTime() - startExtract);
            }

            // Theoretical estimated comparisons[cite: 1]
            long estInsertComps = (long) (n * (Math.log(n) / Math.log(2)));
            long estExtractComps = (long) (n * 2 * (Math.log(n) / Math.log(2)));

            System.out.printf("n=%-5d | %-15d | %-15d | %-20d | %-20d%n",
                    n, (totalInsertTime / REPETITIONS), (totalExtractTime / REPETITIONS), estInsertComps, estExtractComps);
        }
    }
}