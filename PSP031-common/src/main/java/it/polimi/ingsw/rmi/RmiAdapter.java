package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;

import java.rmi.RemoteException;

/**
 * Adapter base class to adapt RMI interfaces with their {@link RemoteException}s to the more generic
 * network interfaces, which instead declare {@link DisconnectedException}s
 */
public class RmiAdapter {

    /**
     * Run the given runnable, catching and converting any {@code RemoteException}s to {@code DisconnectedException}s
     *
     * @param runnable task to run
     * @throws DisconnectedException if any {@link RemoteException} is thrown
     */
    protected void adapt(RmiRunnable runnable) throws DisconnectedException {
        try {
            runnable.run();
        } catch (RemoteException e) {
            throw new DisconnectedException(e);
        }
    }

    /**
     * Invoke the given supplier and return its result, catching and converting any {@code RemoteException}s
     * to {@code DisconnectedException}s
     *
     * @param supplier supplier to invoke
     * @throws DisconnectedException if any {@link RemoteException} is thrown
     */
    protected <T> T adapt(RmiSupplier<T> supplier) throws DisconnectedException {
        try {
            return supplier.get();
        } catch (RemoteException e) {
            throw new DisconnectedException(e);
        }
    }

    /** Runnable interface which can throw {@link RemoteException}s */
    @FunctionalInterface
    protected interface RmiRunnable {

        void run() throws RemoteException;
    }

    /** Supplier interface which can throw {@link RemoteException}s */
    @FunctionalInterface
    protected interface RmiSupplier<T> {

        T get() throws RemoteException;
    }
}
