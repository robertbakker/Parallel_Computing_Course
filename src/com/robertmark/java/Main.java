package com.robertmark.java;

import org.apache.commons.lang3.time.StopWatch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final int N_THREADS = Runtime.getRuntime().availableProcessors();

    private static final StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws InterruptedException, IOException {
        Main main = new Main();
        System.out.println("Using " + N_THREADS + " threads.");
        System.out.println();
        main.runSerial("Small", "vissenkom.jpg", "gray_java_vissenkom_serial.jpg");
        main.runParallel("Small", "vissenkom.jpg", "gray_java_vissenkom_parallel.jpg");
        System.out.println();
        main.runSerial("Medium", "hond.jpg", "gray_java_hond_serial.jpg");
        main.runParallel("Medium", "hond.jpg", "gray_java_hond_parallel.jpg");
        System.out.println();
        main.runSerial("Large", "image.jpg", "gray_java_image_serial.jpg");
        main.runParallel("Large", "image.jpg", "gray_java_image_parallel.jpg");
    }

    public void runSerial(String name, String fileName, String outputFileName) throws IOException {
        stopWatch.reset();
        stopWatch.start();
        String path = (new File("")).getAbsolutePath();
        File file = new File(path + "/" + fileName);
        if (file.exists()) {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(path + "/" + outputFileName);
            image.setRGB(
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight(),
                    Utils.makeGray(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())),
                    0,
                    image.getWidth()
            );
            ImageIO.write(image, "jpg", outputFile);
            stopWatch.stop();
            System.out.println(name + " - Single Threaded - Elapsed time:  " + stopWatch.getTime() + " msecs");
        }
    }

    public void runParallel(String name, String fileName, String outputFileName) throws IOException, InterruptedException {
        stopWatch.reset();
        stopWatch.start();
        String path = (new File("")).getAbsolutePath();
        File file = new File(path + "/" + fileName);
        if (file.exists()) {
            BufferedImage image = ImageIO.read(file);
            int[] partitions = Utils.partitionNumber(image.getWidth(), N_THREADS);
            ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
            List<GrayWorker> workers = new ArrayList<>();

            for (int i = 0; i < N_THREADS; i++) {
                int[] rgb = image.getRGB(partitions[0] * i, 0, partitions[i], image.getHeight(), null, 0, partitions[i]);
                workers.add(new GrayWorker(rgb));
            }
            for (GrayWorker worker : workers) {
                executorService.execute(worker);
            }
            executorService.shutdown();
            executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            for (int i = 0; i < N_THREADS; i++) {
                image.setRGB(partitions[0] * i, 0, partitions[i], image.getHeight(), workers.get(0).getRgb(), 0, partitions[i]);
            }
            File outputFile = new File(path + "/" + outputFileName);
            ImageIO.write(image, "jpg", outputFile);
            stopWatch.stop();
            System.out.println(name + " - Multi-Threaded - Elapsed time:  " + stopWatch.getTime() + " msecs");
        }

    }
}

class GrayWorker implements Runnable {
    private int[] rgb;

    GrayWorker(int[] rgb) {
        this.rgb = rgb;
    }

    @Override
    public void run() {

        rgb = Utils.makeGray(rgb);
    }

    int[] getRgb() {
        return rgb;
    }
}
