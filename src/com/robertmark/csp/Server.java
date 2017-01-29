package com.robertmark.csp;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends UnicastRemoteObject implements Service {

    public static final String DefaultMasterNodeName = "PI11";
    public static final String ServiceName = "TaskServer";
    public static final int DefaultPort = 1199;

    private static String localHostname;

    public Task task;


    public Server() throws RemoteException {
    }

    public static Service connect(String host) {

        Service remoteService = null;

        try {
            System.out.println("Connecting to " + host);

            Registry r = LocateRegistry.getRegistry(host, DefaultPort);
            remoteService = (Service) r.lookup(ServiceName);

        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return remoteService;
    }

    public void ping() throws RemoteException {
        return;
    }

    public String sendMessage(String message) throws RemoteException {
        System.out.println("echo: " + message);
        return message + " received at " + localHostname;
    }

    public <T> T executeTask(Task<T> t) throws RemoteException {
        System.out.println("Executing task");
        System.out.println(t);
        return t.execute();
    }

    public void setTask(Task task) throws RemoteException {
        this.task = task;
    }

    public Task getTask() throws RemoteException {
        return this.task;
    }


    public static void main(String[] args) {
        try {
            // get the hostname of this node
            localHostname = InetAddress.getLocalHost().getHostName();

            // start a new server object
            Server server = new Server();

            // start the registry service on this node
            Registry registry = LocateRegistry.createRegistry(Server.DefaultPort);

            // add binding to this server object and use a specific ServiceName to reference it
            registry.bind(Server.ServiceName, server);


            System.out.println(Server.ServiceName + " running on " + localHostname);

        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

}
