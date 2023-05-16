package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.DisconnectedException;
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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RmiConnectionServerController implements RmiConnectionController, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiConnectionServerController.class);

    private final ServerController controller;
    private final Registry registry;
    private final String remoteName;
    private final UnicastRemoteObjects.Exporter unicastRemoteObjects;
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
        this.unicastRemoteObjects = unicastRemoteObjects;
    }

    @Override
    public void joinGame(String nick,
                         RmiHeartbeatHandler handler,
                         RmiLobbyUpdaterFactory updaterFactory)
            throws NickNotValidException {
        var connection = new PlayerConnection(controller, nick);
        connections.add(connection);
        try {
            controller.joinGame(
                    nick,
                    new RmiHeartbeatHandler.Adapter(handler, connection::disconnectPlayer),
                    connection,
                    new RmiLobbyUpdaterFactory.Adapter(updaterFactory),
                    controller -> {
                        try {
                            var lobbyController = new RmiLobbyServerController(nick, controller, connection::disconnectPlayer);
                            connection.lobbyControllerRemote = lobbyController;
                            return new RmiLobbyController.Adapter(unicastRemoteObjects.export(lobbyController, 0));
                        } catch (RemoteException e) {
                            throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                        }
                    },
                    (player, game) -> {
                        try {
                            var gameController = new RmiGameServerController(player, game, connection::disconnectPlayer);
                            connection.gameControllerRemote = gameController;
                            return new RmiGameController.Adapter(unicastRemoteObjects.export(gameController, 0));
                        } catch (RemoteException e) {
                            throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                        }
                    });
        } catch (NickNotValidException e) {
            try {
                connection.close();
            } catch (IOException ex) {
                LOGGER.error("Failed to close player", ex);
            }
            throw e;
        } catch (Throwable e) {
            connection.disconnectPlayer(e);
        }
    }

    @Override
    public void close() throws IOException {
        for (PlayerConnection connection : connections) {
            try {
                connection.close();
            } catch (IOException ex) {
                LOGGER.error("Failed to close player RMI objects", ex);
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

        volatile @Nullable Remote lobbyControllerRemote;
        volatile @Nullable Remote gameControllerRemote;

        private final AtomicBoolean unexported = new AtomicBoolean();

        public PlayerConnection(ServerController controller, String nick) {
            super(controller, nick);
        }

        @Override
        public void close() throws IOException {
            try {
                // Un-exporting multiple times throws exceptions
                if (!unexported.getAndSet(true)) {
                    var lobbyControllerRemote = this.lobbyControllerRemote;
                    if (lobbyControllerRemote != null)
                        UnicastRemoteObject.unexportObject(lobbyControllerRemote, true);
                    var gameControllerRemote = this.gameControllerRemote;
                    if (gameControllerRemote != null)
                        UnicastRemoteObject.unexportObject(gameControllerRemote, true);
                }
            } finally {
                connections.remove(this);
            }
        }
    }
}
