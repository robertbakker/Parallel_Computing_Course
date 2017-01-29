package com.robertmark.csp;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
    void ping() throws RemoteException;
    String sendMessage(String message) throws RemoteException;
    <T> T executeTask(Task<T> t) throws RemoteException;

    void setTask(Task task) throws RemoteException;
    Task getTask() throws RemoteException;
}