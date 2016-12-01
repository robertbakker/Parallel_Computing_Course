package com.robertmark;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static final int SIZE = Integer.MAX_VALUE / 100;
    public static final int N_THREADS = Runtime.getRuntime().availableProcessors();

    private static final StopWatch stopWatch = new StopWatch();

    private int[] array = new int[SIZE];

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
//        main.runOnSingleThread();
        main.runOnTwoThreads();
        main.runOnNThreads();
    }

    public void runOnSingleThread() {

        System.out.println(String.format("Filling array with %s values in sequence(sorted)", SIZE));
        for (int i = 0; i < SIZE; i++) {
            array[i] = i;
        }

        printNElements(10);
        stopWatch.start();
        MergeSort.sort(array);
        stopWatch.stop();
        System.out.println(String.format("Merge sort took: %dms on a sorted array \nof %d elements, ranging from 0 to %s\n", stopWatch.getTime(), SIZE, SIZE));
        printNElements(10);

        // Reusing array for new values
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = random.nextInt(SIZE);
        }
        System.out.println("Filling array with random values...\n");
        printNElements(10);

        stopWatch.reset();
        stopWatch.start();
        MergeSort.sort(array);
        stopWatch.stop();
        System.out.println(String.format("Merge sort took: %dms on a array of %d elements, \nwith random values between from 0 and %s", stopWatch.getTime(), SIZE, SIZE));
        printNElements(10);
    }

    public void runOnTwoThreads() throws InterruptedException {
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = random.nextInt(SIZE);
        }
        System.out.println("Filling array with random values...\n");
        printNElements(10);

        stopWatch.reset();
        stopWatch.start();
        int[] subArr1 = new int[array.length / 2];
        int[] subArr2 = new int[array.length - array.length / 2];
        System.arraycopy(array, 0, subArr1, 0, array.length / 2);
        System.arraycopy(array, array.length / 2, subArr2, 0, array.length - array.length / 2);

        MergeSortThread runner1 = new MergeSortThread(subArr1);
        MergeSortThread runner2 = new MergeSortThread(subArr2);
        runner1.start();
        runner2.start();
        runner1.join();
        runner2.join();
        array = finalMerge(runner1.getInternal(), runner2.getInternal());
        stopWatch.stop();
        System.out.println("2-thread MergeSort takes: " + stopWatch.getTime() + " ms");
        printNElements(10);
    }

    private void printNElements(int n) {
        System.out.print(String.format("--> First %s elements of the array: ", n));
        for (int i = 0; i < n; i++) {
            System.out.print(array[i] + ",");
        }
        System.out.println();
    }

    public static int[] finalMerge(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0;
        int j = 0;
        int r = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                result[r] = a[i];
                i++;
                r++;
            } else {
                result[r] = b[j];
                j++;
                r++;
            }
            if (i == a.length) {
                while (j < b.length) {
                    result[r] = b[j];
                    r++;
                    j++;
                }
            }
            if (j == b.length) {
                while (i < a.length) {
                    result[r] = a[i];
                    r++;
                    i++;
                }
            }
        }
        return result;
    }

    public void runOnNThreads() throws InterruptedException {
        System.out.println("\nMerge sort on multiple cores next.");
        System.out.println(String.format("Available cores: %d\n", N_THREADS));


        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = random.nextInt(SIZE);
        }

        stopWatch.reset();
        stopWatch.start();
        int chunk = (int) Math.ceil(array.length / N_THREADS);

        MergeSortThread[] threads = new MergeSortThread[N_THREADS];
        for (int i = 0; i < N_THREADS; i++) {
            threads[i] = new MergeSortThread(Arrays.copyOfRange(array, i, Math.min(array.length, i * chunk + chunk)));
        }

        for (int i = 0; i < N_THREADS; i++) {
            threads[i].start();
        }

        for (int i = 0; i < N_THREADS; i++) {
            threads[i].join();
        }
        array = finalMerge(ArrayUtils.addAll(threads[0].getInternal(), threads[1].getInternal()),
                ArrayUtils.addAll(threads[2].getInternal(), threads[3].getInternal()));
        stopWatch.stop();

        System.out.println(N_THREADS + "-threads MergeSort takes: " + stopWatch.getTime() + " ms");
        printNElements(10);
    }
}
