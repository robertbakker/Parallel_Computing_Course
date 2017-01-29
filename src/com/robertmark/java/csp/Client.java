package com.robertmark.java.csp;

import com.robertmark.java.Utils;
import org.apache.commons.lang3.time.StopWatch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {

    public static final int N_THREADS = Runtime.getRuntime().availableProcessors();

    private static final StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws RemoteException, UnknownHostException, InterruptedException, IOException {
        // who am I ?
        String localHostname = InetAddress.getLocalHost().getHostName();
        System.out.println("This is host:" + localHostname);
        System.out.println("Using " + N_THREADS + " threads.");
        System.out.println();
        Service service = Server.connect(Server.NodeName);
        Client client = new Client();

        client.runSingleNodeSingleTask("Small", "vissenkom.jpg", "gray_java_vissenkom_single_node_single_task.jpg", service);
        client.runSingleNodeMultipleTasks("Small", "vissenkom.jpg", "gray_java_vissenkom_single_node_multiple_tasks.jpg", service);
        System.out.println();
        client.runSingleNodeSingleTask("Medium", "hond.jpg", "gray_java_hond_single_node_single_task.jpg", service);
        client.runSingleNodeMultipleTasks("Medium", "hond.jpg", "gray_java_hond_single_node_multiple_tasks", service);
        System.out.println();
        client.runSingleNodeSingleTask("Large", "image.jpg", "gray_java_image_single_node_single_task.jpg", service);
        client.runSingleNodeMultipleTasks("Large", "image.jpg", "gray_java_image_single_node_multiple_tasks", service);

    }

    public void runSingleNodeSingleTask(String name, String fileName, String outputFileName, Service service) throws IOException, InterruptedException {
        stopWatch.reset();
        stopWatch.start();

        String path = (new File("")).getAbsolutePath();
        File file = new File(path + "/" + fileName);
        if (file.exists()) {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(path + "/" + outputFileName);
            int[] grayRGB = service.executeTask(new GrayTask(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())));
            image.setRGB(0, 0, image.getWidth(), image.getHeight(), grayRGB, 0, image.getWidth());
            ImageIO.write(image, "jpg", outputFile);
            stopWatch.stop();
            System.out.println(name + " - Single Node Single Task - Elapsed time:  " + stopWatch.getTime() + " msecs");
        }
    }

    public void runSingleNodeMultipleTasks(String name, String fileName, String outputFileName, Service service) throws IOException, InterruptedException {
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
                workers.add(new GrayWorker(rgb, service));
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
            System.out.println(name + " - Single Node Multiple tasks - Elapsed time:  " + stopWatch.getTime() + " msecs");
        }
    }
}

    class GrayWorker implements Runnable {
        private int[] rgb;
        private Service service;

        GrayWorker(int[] rgb, Service service) {
            this.rgb = rgb;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                rgb = service.executeTask(new GrayTask(rgb));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        int[] getRgb() {
            return rgb;
        }
    }

    class GrayTask implements Task<int[]>, Serializable {

        private int[] rgbs;

        GrayTask(int[] rgbs) {
            this.rgbs = rgbs;
        }

        @Override
        public int[] execute() {
            long t1, t2;
            t1 = System.currentTimeMillis();
            int[] result = Utils.makeGray(rgbs);
            t2 = System.currentTimeMillis();
            System.out.println("Gray Task took " + (t2-t1) + " ms.");
            return result;
        }
    }
