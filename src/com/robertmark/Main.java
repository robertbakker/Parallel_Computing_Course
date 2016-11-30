package com.robertmark;

import org.apache.commons.lang3.time.StopWatch;

import java.util.Random;

public class Main {

    public static final int SIZE = Integer.MAX_VALUE / 100;
    public static final int THREADS = Runtime.getRuntime().availableProcessors();

    private static final StopWatch stopWatch = new StopWatch();

    private Integer[] array = new Integer[SIZE];

    public static void main(String[] args) {
        Main main = new Main();
        main.runOnSingleThread();

        main.runOnMultipleThreads();
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

    public void runOnMultipleThreads() {
        System.out.println("\nMerge sort on multiple cores next.");
        System.out.println(String.format("Available cores: %d\n", THREADS));

    }

    private void printNElements(int n) {
        System.out.print(String.format("--> First %s elements of the array: ", n));
        for (int i = 0; i < n; i++) {
            System.out.print(array[i] + ",");
        }
        System.out.println();
    }
}
