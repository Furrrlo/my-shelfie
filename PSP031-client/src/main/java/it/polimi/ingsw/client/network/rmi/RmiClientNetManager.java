package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NetworkConstants;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RmiClientNetManager extends RmiAdapter implements ClientNetManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiClientNetManager.class);

    private final @Nullable String host;
    private final int port;
    private final String remoteName;
    private final String nick;
    private final RmiConnectionController.ConnectedController server;
    private final UnicastRemoteObjects.TrackingExporter unicastRemoteObjects;

    private volatile @Nullable Lobby lobby;

    public static ClientNetManager connect(@Nullable String host, int port, String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, RmiConnectionController.REMOTE_NAME, nick);
    }

    @VisibleForTesting
    public static RmiClientNetManager connect(@Nullable String host, int port, String remoteName, String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, remoteName, new RMITimeoutClientSocketFactory(), null, nick);
    }

    @VisibleForTesting
    public static RmiClientNetManager connect(@Nullable String host,
                                              int port,
                                              String remoteName,
                                              @Nullable RMIClientSocketFactory csf,
                                              @Nullable RMIServerSocketFactory ssf,
                                              String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, remoteName, UnicastRemoteObjects.createExporter(csf, ssf), nick);
    }

    private static RmiClientNetManager connect(@Nullable String host,
                                               int port,
                                               String remoteName,
                                               UnicastRemoteObjects.Exporter unicastRemoteObjectsIn,
                                               String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        var unicastRemoteObjects = UnicastRemoteObjects.createTrackingExporter(unicastRemoteObjectsIn);

        final Registry registry = LocateRegistry.getRegistry(host, port);
        final var server = (RmiConnectionController) registry.lookup(remoteName);

        // If the client has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        // @see https://bugs.openjdk.org/browse/JDK-8042232
        // To work around this, either run the JVM with the parameter -Djava.rmi.server.hostname=<server address> or
        // we ask the server to give back the remote client address.
        if (System.getProperty("java.rmi.server.hostname") == null) {
            var addrStr = server.getClientAddressHost();
            System.setProperty("java.rmi.server.hostname", addrStr);
            LOGGER.info("Detected java.rmi.server.hostname='{}'", addrStr);
        }

        AtomicReference<RmiClientNetManager> netManagerRef = new AtomicReference<>();
        var readTimeoutMillis = Long.getLong("sun.rmi.transport.tcp.readTimeout", NetworkConstants.READ_TIMEOUT.toMillis());
        var heartbeatClientHandler = new RmiHeartbeatClientHandler(readTimeoutMillis, TimeUnit.MILLISECONDS, () -> {
            var netManager = netManagerRef.get();
            if (netManager != null)
                netManager.close();
        });
        RmiHeartbeatHandler exportedHeartbeatHandler = unicastRemoteObjects.export(heartbeatClientHandler, 0);
        try {
            var connectedServer = server.doConnect(nick, exportedHeartbeatHandler);
            heartbeatClientHandler.start();

            var netManager = new RmiClientNetManager(host, port, remoteName, nick, connectedServer, unicastRemoteObjects);
            netManagerRef.set(netManager);
            return netManager;
        } catch (Throwable t) {
            unicastRemoteObjects.unexportAll(true);
            throw t;
        }
    }

    @VisibleForTesting
    private RmiClientNetManager(@Nullable String host,
                                int port,
                                String remoteName,
                                String nick,
                                RmiConnectionController.ConnectedController server,
                                UnicastRemoteObjects.TrackingExporter unicastRemoteObjects) {
        this.port = port;
        this.server = server;
        this.remoteName = remoteName;
        this.nick = nick;
        this.host = host;
        this.unicastRemoteObjects = unicastRemoteObjects;
    }

    @Override
    public ClientNetManager recreateAndReconnect() throws NotBoundException, RemoteException, NickNotValidException {
        return RmiClientNetManager.connect(host, port, remoteName, unicastRemoteObjects.getUnderlyingExporter(), nick);
    }

    @Override
    public String getHost() {
        return host == null ? "localhost" : host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public LobbyAndController<Lobby> joinGame() throws RemoteException {
        final InterceptingFactory updaterFactory = new InterceptingFactory(unicastRemoteObjects);
        server.joinGame(unicastRemoteObjects.export(updaterFactory, 0, false));
        UnicastRemoteObject.unexportObject(updaterFactory, true);

        var lobbyAndController = updaterFactory.getUpdater().getLobbyAndController();
        lobby = lobbyAndController.lobby();
        return lobbyAndController;
    }

    @Override
    public void close() {
        try {
            unicastRemoteObjects.unexportAll(true);
        } finally {
            var lobby = this.lobby;
            if (lobby != null)
                lobby.disconnectThePlayer(nick);
        }
    }

    private static class InterceptingFactory implements RmiLobbyUpdaterFactory {

        private final UnicastRemoteObjects.Exporter unicastRemoteObjects;
        private @Nullable RmiLobbyClientUpdater updater;

        public InterceptingFactory(UnicastRemoteObjects.Exporter unicastRemoteObjects) {
            this.unicastRemoteObjects = unicastRemoteObjects;
        }

        public RmiLobbyClientUpdater getUpdater() {
            return Objects.requireNonNull(
                    updater,
                    "Expected ConnectionServerController to invoke " +
                            "GameCreationStateUpdaterFactory exactly once, was 0");
        }

        @Override
        public RmiLobbyUpdater create(LobbyAndController<Lobby> lobby) throws RemoteException {
            if (updater != null)
                throw new UnsupportedOperationException("Expected ConnectionServerController to invoke " +
                        "GameCreationStateUpdaterFactory exactly once, was 2+");

            updater = new RmiLobbyClientUpdater(unicastRemoteObjects, lobby);
            return unicastRemoteObjects.export(updater, 0);
        }
    }
}
