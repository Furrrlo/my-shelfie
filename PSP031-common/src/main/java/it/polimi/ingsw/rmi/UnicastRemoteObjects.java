package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UnicastRemoteObjects {

    private UnicastRemoteObjects() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Remote> T export(T remote, int port) throws RemoteException {
        return (T) UnicastRemoteObject.exportObject(remote, port);
    }
}
