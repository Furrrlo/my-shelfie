package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;

import java.rmi.RemoteException;

class RmiAdapter {

    protected void adapt(RmiRunnable runnable) throws DisconnectedException {
        try {
            runnable.run();
        } catch (RemoteException e) {
            throw new DisconnectedException(e);
        }
    }

    protected <T> T adapt(RmiSupplier<T> supplier) throws DisconnectedException {
        try {
            return supplier.get();
        } catch (RemoteException e) {
            throw new DisconnectedException(e);
        }
    }

    interface RmiRunnable {

        void run() throws RemoteException;
    }

    interface RmiSupplier<T> {

        T get() throws RemoteException;
    }
}
