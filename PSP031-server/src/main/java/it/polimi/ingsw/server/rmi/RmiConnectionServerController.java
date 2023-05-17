package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.server.controller.BaseServerConnection;
import it.polimi.ingsw.server.controller.ServerController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RmiConnectionServerController implements RmiConnectionController, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiConnectionServerController.class);

    private final ServerController controller;
    private final Registry registry;
    private final String remoteName;
    private final UnicastRemoteObjects.Exporter underlyingUnicastRemoteObjects;
    private final Set<PlayerConnection> connections = ConcurrentHashMap.newKeySet();

    public static RmiConnectionServerController bind(ServerController controller) throws RemoteException {
        return bind(LocateRegistry.createRegistry(Registry.REGISTRY_PORT), RmiConnectionController.REMOTE_NAME, controller);
    }

    @VisibleForTesting
    public static RmiConnectionServerController bind(Registry registry,
                                                     String remoteName,
                                                     ServerController controller)
            throws RemoteException {
        return bind(registry, remoteName, controller, new RMITimeoutClientSocketFactory(), null);
    }

    @VisibleForTesting
    public static RmiConnectionServerController bind(Registry registry,
                                                     String remoteName,
                                                     ServerController controller,
                                                     @Nullable RMIClientSocketFactory csf,
                                                     @Nullable RMIServerSocketFactory ssf)
            throws RemoteException {
        RmiConnectionServerController rmiController;
        final var exporter = UnicastRemoteObjects.createExporter(csf, ssf);
        registry.rebind(remoteName, exporter
                .export(rmiController = new RmiConnectionServerController(controller, registry, remoteName, exporter), 0));
        return rmiController;
    }

    private RmiConnectionServerController(ServerController controller,
                                          Registry registry,
                                          String remoteName,
                                          UnicastRemoteObjects.Exporter unicastRemoteObjects) {
        this.controller = controller;
        this.registry = registry;
        this.remoteName = remoteName;
        this.underlyingUnicastRemoteObjects = unicastRemoteObjects;
    }

    @Override
    public RmiConnectionController.ConnectedController doConnect(String nick, RmiHeartbeatHandler handler)
            throws RemoteException, NickNotValidException {

        var connection = new PlayerConnection(controller, nick,
                UnicastRemoteObjects.createTrackingExporter(underlyingUnicastRemoteObjects));
        connections.add(connection);
        try {
            controller.connectPlayer(nick, new RmiHeartbeatHandler.Adapter(handler, connection::disconnectPlayer));
            return connection.unicastRemoteObjects().export(new ConnectedControllerImpl(connection), 0);
        } catch (NickNotValidException e) {
            try {
                connection.close();
            } catch (Throwable t) {
                LOGGER.error("Failed to close player", t);
            }
            throw e;
        } catch (Throwable e) {
            connection.disconnectPlayer(e);
            throw new IllegalStateException("Internal server error");
        }
    }

    private class ConnectedControllerImpl implements RmiConnectionController.ConnectedController {

        private final PlayerConnection connection;

        public ConnectedControllerImpl(PlayerConnection connection) {
            this.connection = connection;
        }

        @Override
        public void joinGame(RmiLobbyUpdaterFactory updaterFactory) throws RemoteException {
            var nick = connection.getNick();

            try {
                controller.joinGame(
                        nick,
                        connection,
                        new RmiLobbyUpdaterFactory.Adapter(updaterFactory),
                        controller -> {
                            try {
                                return new RmiLobbyController.Adapter(connection.unicastRemoteObjects().export(
                                        new RmiLobbyServerController(nick, controller, connection::disconnectPlayer), 0));
                            } catch (RemoteException e) {
                                throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                            }
                        },
                        (player, game) -> {
                            try {
                                return new RmiGameController.Adapter(connection.unicastRemoteObjects
                                        .export(new RmiGameServerController(player, game, connection::disconnectPlayer), 0));
                            } catch (RemoteException e) {
                                throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                            }
                        });
            } catch (Throwable e) {
                connection.disconnectPlayer(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (PlayerConnection connection : connections) {
            try {
                connection.close();
            } catch (Throwable t) {
                LOGGER.error("Failed to close player RMI objects", t);
            }
        }

        try {
            registry.unbind(remoteName);
        } catch (NotBoundException e) {
            throw new IOException("Failed to unbind object from registry", e);
        }

        UnicastRemoteObject.unexportObject(this, true);
    }

    private class PlayerConnection extends BaseServerConnection {

        private final UnicastRemoteObjects.TrackingExporter unicastRemoteObjects;

        public PlayerConnection(ServerController controller, String nick,
                                UnicastRemoteObjects.TrackingExporter unicastRemoteObjects) {
            super(controller, nick);
            this.unicastRemoteObjects = unicastRemoteObjects;
        }

        public UnicastRemoteObjects.TrackingExporter unicastRemoteObjects() {
            return unicastRemoteObjects;
        }

        @Override
        public void close() {
            try {
                unicastRemoteObjects.unexportAll(true);
            } finally {
                connections.remove(this);
            }
        }
    }
}
