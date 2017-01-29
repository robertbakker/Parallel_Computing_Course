package com.robertmark.csp;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/**
 * Created by Richard on 8/23/2016.
 */
public class OtherClient {

    public static void main(String[] args) throws RemoteException, UnknownHostException, InterruptedException {
        // who am I ?
        String localHostname = InetAddress.getLocalHost().getHostName();
        System.out.println("This is host:" + localHostname);

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


        // get a client
        Task counter = service.getTask();
        // and this client can also count to three :)
        counter.execute();

    }
}
