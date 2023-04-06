package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.server.controller.BaseServerConnection;
import it.polimi.ingsw.server.controller.ServerController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RmiConnectionServerController implements RmiConnectionController, Closeable {

    private final ServerController controller;
    private final Registry registry;
    private final String remoteName;
    private final Set<PlayerConnection> connections = ConcurrentHashMap.newKeySet();

    public static RmiConnectionServerController bind(ServerController controller) throws RemoteException {
        return bind(LocateRegistry.createRegistry(Registry.REGISTRY_PORT), RmiConnectionController.REMOTE_NAME, controller);
    }

    @VisibleForTesting
    public static RmiConnectionServerController bind(Registry registry,
                                                     String remoteName,
                                                     ServerController controller)
            throws RemoteException {
        RmiConnectionServerController rmiController;
        registry.rebind(remoteName, UnicastRemoteObjects
                .export(rmiController = new RmiConnectionServerController(controller, registry, remoteName), 0));
        return rmiController;
    }

    private RmiConnectionServerController(ServerController controller, Registry registry, String remoteName) {
        this.controller = controller;
        this.registry = registry;
        this.remoteName = remoteName;
    }

    @Override
    public void joinGame(String nick,
                         RmiHeartbeatHandler handler,
                         RmiLobbyUpdaterFactory updaterFactory) {
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
                            var lobbyController = new RmiLobbyServerController(nick, controller);
                            connection.lobbyControllerRemote = lobbyController;
                            return new RmiLobbyController.Adapter(UnicastRemoteObjects.export(lobbyController, 0));
                        } catch (RemoteException e) {
                            throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                        }
                    },
                    (player, game) -> {
                        try {
                            var gameController = new RmiGameServerController(player, game);
                            connection.gameControllerRemote = gameController;
                            return new RmiGameController.Adapter(UnicastRemoteObjects.export(gameController, 0));
                        } catch (RemoteException e) {
                            throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                        }
                    });
        } catch (DisconnectedException e) {
            connection.disconnectPlayer(e);
        }
    }

    @Override
    public void close() throws IOException {
        for (PlayerConnection connection : connections) {
            try {
                connection.close();
            } catch (IOException ex) {
                // TODO: log
                System.err.println("Failed to close player RMI objects");
                ex.printStackTrace();
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

        public PlayerConnection(ServerController controller, String nick) {
            super(controller, nick);
        }

        @Override
        public void close() throws IOException {
            try {
                var lobbyControllerRemote = this.lobbyControllerRemote;
                if (lobbyControllerRemote != null)
                    UnicastRemoteObject.unexportObject(lobbyControllerRemote, true);
                var gameControllerRemote = this.gameControllerRemote;
                if (gameControllerRemote != null)
                    UnicastRemoteObject.unexportObject(gameControllerRemote, true);
            } finally {
                connections.remove(this);
            }
        }
    }
}
