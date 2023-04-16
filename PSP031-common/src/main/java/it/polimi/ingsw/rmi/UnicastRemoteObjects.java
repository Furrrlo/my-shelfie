package it.polimi.ingsw.rmi;

import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class UnicastRemoteObjects {

    private UnicastRemoteObjects() {
    }

    /**
     * Utility method which delegates to {@link UnicastRemoteObject#exportObject(Remote, int)} but casts the result
     *
     * @see UnicastRemoteObject#exportObject(Remote, int)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Remote> T export(T remote, int port) throws RemoteException {
        return (T) UnicastRemoteObject.exportObject(remote, port);
    }

    /**
     * Utility method which delegates to
     * {@link UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)}
     * but casts the result
     * 
     * @see UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Remote> T export(T obj, int port,
                                              @Nullable RMIClientSocketFactory csf,
                                              @Nullable RMIServerSocketFactory ssf)
            throws RemoteException {
        return (T) UnicastRemoteObject.exportObject(obj, port, csf, ssf);
    }

    /** Interface used to export remote objects in a consistent way across the whole application */
    public interface Exporter {

        /**
         * Exports the remote object to make it available to receive incoming
         * calls, using the particular supplied port.
         *
         * The object is exported using settings provided when creating
         * this exporter.
         *
         * @param obj the remote object to be exported
         * @param port the port to export the object on
         * @return remote object stub
         * @throws RemoteException if export fails
         */
        <T extends Remote> T export(T obj, int port) throws RemoteException;
    }

    /**
     * Creates a UnicastRemoteObjectExporter which creates socket using the global RMISocketFactory
     *
     * @return a UnicastRemoteObjectExporter using the global RMISocketFactory
     * @see UnicastRemoteObject#exportObject(Remote, int)
     * @see RMISocketFactory#setSocketFactory(RMISocketFactory)
     */
    public static Exporter createExporter() {
        return new DefaultExporter();
    }

    /** Implementation for the {@link #createExporter()} method */
    private static class DefaultExporter implements Exporter {

        @Override
        public <T extends Remote> T export(T remote, int port) throws RemoteException {
            return UnicastRemoteObjects.export(remote, port);
        }
    }

    /**
     * Creates a UnicastRemoteObjectExporter which creates socket using the given RMISocketFactory
     *
     * The socket factory may be null, in which case the corresponding client or server socket
     * creation method of RMISocketFactory is used instead.
     *
     * @return a UnicastRemoteObjectExporter using the given RMISocketFactory
     * @see UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)
     */
    public static Exporter createExporter(@Nullable RMISocketFactory sf) {
        return createExporter(sf, sf);
    }

    /**
     * Creates a UnicastRemoteObjectExporter which creates socket using the given factories
     *
     * Either socket factory may be null, in which case the corresponding client or server socket
     * creation method of RMISocketFactory is used instead.
     *
     * @return a UnicastRemoteObjectExporter using the given RMISocketFactory
     * @see UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)
     * @see RMISocketFactory#setSocketFactory(RMISocketFactory)
     */
    public static Exporter createExporter(@Nullable RMIClientSocketFactory csf,
                                          @Nullable RMIServerSocketFactory ssf) {
        return new FactoriesExporter(csf, ssf);
    }

    /** Implementation for the {@link #createExporter(RMIClientSocketFactory, RMIServerSocketFactory)} method */
    private record FactoriesExporter(
            @Nullable RMIClientSocketFactory csf,
            @Nullable RMIServerSocketFactory ssf) implements Exporter {

        @Override
        public <T extends Remote> T export(T remote, int port) throws RemoteException {
            return UnicastRemoteObjects.export(remote, port, csf, ssf);
        }
    }
}
