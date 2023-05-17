package it.polimi.ingsw.rmi;

import org.jetbrains.annotations.Nullable;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    /**
     * Interface used to export remote objects which also tracks which object have been exported
     * and allows bulk-unexporting them
     */
    public interface TrackingExporter extends Exporter {

        /** Returns the underlying exporter used to actually export objects */
        Exporter getUnderlyingExporter();

        /**
         * Exports the remote object to make it available to receive incoming
         * calls, using the particular supplied port. Optionally, the object can
         * also be excluded from tracking and, therefore, from bulk-unregistering
         * <p>
         * The object is exported using settings provided when creating
         * this exporter.
         *
         * @param obj the remote object to be exported
         * @param port the port to export the object on
         * @param track whether the object needs to be tracked or not
         * @return remote object stub
         * @throws RemoteException if export fails
         */
        <T extends Remote> T export(T obj, int port, boolean track) throws RemoteException;

        /**
         * Unexport all objects previously exported with tracking by this exporter
         * 
         * @param force if true, unexports the object even if there are pending or in-progress calls;
         *        if false, only unexports the object if there are no pending or in-progress calls
         * @see UnicastRemoteObject#unexportObject(Remote, boolean)
         */
        void unexportAll(boolean force);
    }

    /**
     * Creates a UnicastRemoteObjectTrackingExporter which exports objects using
     * the given exporter and tracks them for bulk-unexporting
     *
     * @return a UnicastRemoteObjectExporter using the global RMISocketFactory
     * @see TrackingExporter
     * @see #createExporter()
     * @see #createExporter(RMISocketFactory)
     * @see #createExporter(RMIClientSocketFactory, RMIServerSocketFactory)
     * @see UnicastRemoteObject#unexportObject(Remote, boolean)
     */
    public static TrackingExporter createTrackingExporter(Exporter exporter) {
        return new DefaultTrackingExporter(exporter);
    }

    /** Implementation for the {@link #createTrackingExporter(Exporter)} method */
    private record DefaultTrackingExporter(Exporter exporter,
            Set<Remote> exportedRemotes,
            Lock unexportLock) implements TrackingExporter {

        private DefaultTrackingExporter(Exporter exporter) {
            this(exporter, ConcurrentHashMap.newKeySet(), new ReentrantLock());
        }

        @Override
        public Exporter getUnderlyingExporter() {
            return exporter;
        }

        @Override
        public <T extends Remote> T export(T obj, int port, boolean track) throws RemoteException {
            var exported = exporter.export(obj, port);
            if (track)
                exportedRemotes.add(obj);
            return exported;
        }

        @Override
        public <T extends Remote> T export(T obj, int port) throws RemoteException {
            return export(obj, port, true);
        }

        @Override
        public void unexportAll(boolean force) {
            Set<Remote> toRemove;
            // Make sure that this call will unexport object at most once
            unexportLock.lock();
            try {
                toRemove = new HashSet<>(exportedRemotes);
                exportedRemotes.removeAll(toRemove); // Remove only the ones we are going to unexport
            } finally {
                unexportLock.unlock();
            }

            List<Throwable> exs = new ArrayList<>();
            for (Remote e : toRemove) {
                try {
                    UnicastRemoteObject.unexportObject(e, force);
                } catch (NoSuchObjectException ex) {
                    exs.add(new IllegalStateException("object " + e + " not exported", ex));
                }
            }

            if (!exs.isEmpty()) {
                var ex = new IllegalStateException("Some object have already been unexported");
                exs.forEach(ex::addSuppressed);
                throw ex;
            }
        }
    }
}
