package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectionIntegrationTest;
import it.polimi.ingsw.ImproperShutdownSocket;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RmiDisconnectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiDisconnectionTest.class);

    static Stream<Arguments> socketSource() {
        return Stream.of(
                socketArgs("goodSocket", Socket::new, s -> {
                }),
                socketArgs("badSocket", ImproperShutdownSocket::new, ImproperShutdownSocket::actuallyClose));
    }

    private static <T extends Socket> Arguments socketArgs(String name,
                                                           Supplier<T> socketFactory,
                                                           ThrowingConsumer<T> cleanup) {
        return Arguments.of(name, socketFactory, cleanup);
    }

    @MethodSource("socketSource")
    @ParameterizedTest(name = "testRmiDisconnection_clientCloseInEmptyLobby_{0}")
    void testRmiDisconnection_clientCloseInEmptyLobby(String name,
                                                      Supplier<Socket> socketFactory,
                                                      ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final String testName = "testRmiDisconnection_clientCloseInEmptyLobby_" + name + "_" + System.currentTimeMillis();
        LOGGER.trace(testName);

        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        final var rmiClientSocketFactory = new DisconnectingSocketFactory(testName, socketFactory);
        try {
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInEmptyLobby(
                    serverController -> {
                        try {
                            return RmiConnectionServerController.bind(
                                    LocateRegistry.createRegistry(0, null, rmiServerSocketFactory),
                                    remoteName,
                                    serverController);
                        } catch (RemoteException e) {
                            throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                        }
                    },
                    nick -> {
                        try {
                            return RmiClientNetManager.connect(
                                    null, rmiServerSocketFactory.getFirstCapturedPort(), remoteName,
                                    rmiClientSocketFactory, null, nick);
                        } catch (RemoteException | NotBoundException | NickNotValidException e) {
                            throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                        }
                    },
                    rmiClientSocketFactory::close);
        } finally {
            for (Socket s : rmiClientSocketFactory.sockets)
                cleanup.accept(s);
            DisconnectingSocketFactory.INSTANCES.remove(testName);
        }
    }

    @MethodSource("socketSource")
    @ParameterizedTest(name = "testRmiDisconnection_clientCloseInLobby_{0}")
    void testRmiDisconnection_clientCloseInLobby(String name,
                                                 Supplier<Socket> socketFactory,
                                                 ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final String testName = "testRmiDisconnection_clientCloseInLobby_" + name + "_" + System.currentTimeMillis();
        LOGGER.trace(testName);

        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        final var rmiClientSocketFactory = new DisconnectingSocketFactory(testName, socketFactory);
        try {
            Function<String, ClientNetManager> defaultSocketSupplier = nick -> {
                try {
                    return RmiClientNetManager.connect(null,
                            rmiServerSocketFactory.getFirstCapturedPort(), remoteName,
                            new RMITimeoutClientSocketFactory(), null, nick);
                } catch (RemoteException | NotBoundException | NickNotValidException e) {
                    throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                }
            };
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInLobby(
                    serverController -> {
                        try {
                            return RmiConnectionServerController.bind(
                                    LocateRegistry.createRegistry(0, null, rmiServerSocketFactory),
                                    remoteName,
                                    serverController);
                        } catch (RemoteException e) {
                            throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                        }
                    },
                    nick -> {
                        try {
                            return RmiClientNetManager.connect(null,
                                    rmiServerSocketFactory.getFirstCapturedPort(), remoteName,
                                    rmiClientSocketFactory, null, nick);
                        } catch (RemoteException | NotBoundException | NickNotValidException e) {
                            throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                        }
                    },
                    defaultSocketSupplier,
                    defaultSocketSupplier,
                    rmiClientSocketFactory::close);
        } finally {
            for (Socket s : rmiClientSocketFactory.sockets)
                cleanup.accept(s);
            DisconnectingSocketFactory.INSTANCES.remove(testName);
        }
    }

    @MethodSource("socketSource")
    @ParameterizedTest(name = "testRmiDisconnection_clientCloseInGame_{0}")
    void testRmiDisconnection_clientCloseInGame(String name,
                                                Supplier<Socket> socketFactory,
                                                ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final String testName = "testRmiDisconnection_clientCloseInGame_" + name + "_" + System.currentTimeMillis();
        LOGGER.trace(testName);

        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        final var rmiClientSocketFactory = new DisconnectingSocketFactory(testName, socketFactory);
        try {
            Function<String, ClientNetManager> defaultSocketSupplier = nick -> {
                try {
                    return RmiClientNetManager.connect(null,
                            rmiServerSocketFactory.getFirstCapturedPort(), remoteName,
                            new RMITimeoutClientSocketFactory(), null, nick);
                } catch (RemoteException | NotBoundException | NickNotValidException e) {
                    throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                }
            };
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInGame(
                    serverController -> {
                        try {
                            return RmiConnectionServerController.bind(
                                    LocateRegistry.createRegistry(0, rmiClientSocketFactory, rmiServerSocketFactory),
                                    remoteName,
                                    serverController);
                        } catch (RemoteException e) {
                            throw new RuntimeException("Failed to bind RmiConnectionServerController", e);
                        }
                    },
                    defaultSocketSupplier,
                    nick -> {
                        try {
                            return RmiClientNetManager.connect(null,
                                    rmiServerSocketFactory.getFirstCapturedPort(), remoteName,
                                    rmiClientSocketFactory, null, nick);
                        } catch (RemoteException | NotBoundException | NickNotValidException e) {
                            throw new RuntimeException("Failed to connect RmiClientNetManager", e);
                        }
                    },
                    defaultSocketSupplier,
                    rmiClientSocketFactory::close,
                    defaultSocketSupplier);
        } finally {
            for (Socket s : rmiClientSocketFactory.sockets)
                cleanup.accept(s);
            DisconnectingSocketFactory.INSTANCES.remove(testName);
        }
    }

    private static class DisconnectingSocketFactory extends RMITimeoutClientSocketFactory {

        public static final Map<String, DisconnectingSocketFactory> INSTANCES = new ConcurrentHashMap<>();

        private final String testName;

        private transient final Supplier<Socket> socketFactory;
        private transient final AtomicBoolean closed = new AtomicBoolean(false);
        private transient final Set<Socket> sockets = ConcurrentHashMap.newKeySet();

        private DisconnectingSocketFactory(String testName, Supplier<Socket> socketFactory) {
            this.testName = testName;
            this.socketFactory = socketFactory;

            if (INSTANCES.putIfAbsent(testName, this) != null)
                throw new IllegalStateException("Initialized DisconnectingSocketFactory multiple times for test " + testName);
        }

        @Serial
        private Object readResolve() throws ObjectStreamException {
            LOGGER.trace("Deserialized DisconnectingSocketFactory {}", testName);
            LOGGER.trace("{}", INSTANCES);
            return Objects.requireNonNull(INSTANCES.get(testName));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            Socket socket = super.createSocket(host, port);
            LOGGER.trace("{}", socket.getClass());
            sockets.add(socket);
            if (closed.get()) {
                socket.close();
                LOGGER.trace("Returning closed socket");
            }
            return socket;
        }

        @Override
        protected Socket doCreateNonConnectedSocket() {
            return socketFactory.get();
        }

        public void close() {
            LOGGER.trace("Closing all sockets");
            closed.set(true);
            sockets.forEach(s -> {
                try {
                    s.close();
                } catch (IOException ex) {
                    throw new UncheckedIOException("Failed to close socket", ex);
                }
            });
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            if (!super.equals(o))
                return false;
            DisconnectingSocketFactory that = (DisconnectingSocketFactory) o;
            return testName.equals(that.testName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), testName);
        }
    }

}