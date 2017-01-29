package com.robertmark.csp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;


class SlowTask implements Task<Boolean>, Serializable {

    long delay;

    public SlowTask(long delay) {
        this.delay = delay;
    }

    @Override
    public Boolean execute() {
        System.out.println("Start a slow task that takes " + delay + " ms.");
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Slow task done");
        return true;
    }
}



class CountTask implements Task<Void>, Serializable {

    public Void execute() {
        try {
            System.out.println("Count 1");
            Thread.sleep(1000);
            System.out.println("Count 2");
            Thread.sleep(1000);
            System.out.println("Count 3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}


public class Client{


    public static void main(String[] args) throws RemoteException, UnknownHostException, InterruptedException {
        // who am I ?
        String localHostname = InetAddress.getLocalHost().getHostName();
        System.out.println("This is host:" + localHostname );

        // connect to the remote service host
        String serviceHost = Server.DefaultMasterNodeName;
        if (args.length > 0)
            serviceHost = args[0];

        Service service = Server.connect(serviceHost);


        // run some methods/tasks
        long t1, t2;

        t1 = System.currentTimeMillis();
        service.ping();
        t2 = System.currentTimeMillis();
        System.out.println("Ping took " + (t2-t1) + " ms.");

        t1 = System.currentTimeMillis();
        String greeting = service.sendMessage("Hello World at " + LocalDateTime.now());
        t2 = System.currentTimeMillis();

        System.out.println("Client side:" + greeting);
        System.out.println("SendMessage took " + (t2-t1) + " ms.");


        t1 = System.currentTimeMillis();
        Boolean test = service.executeTask(new SlowTask(3000));
        t2 = System.currentTimeMillis();
        System.out.println("Slow task execution took " + (t2-t1) + " ms.");


        // create a task and run it locally
        Task counter = new CountTask();
        counter.execute();
        service.setTask(counter);

    }
}


