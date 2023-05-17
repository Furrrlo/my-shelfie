package it.polimi.ingsw.socket;

import it.polimi.ingsw.DisconnectionIntegrationTest;
import it.polimi.ingsw.ImproperShutdownSocket;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SocketDisconnectionTest {

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
    @ParameterizedTest(name = "testSocketDisconnection_clientCloseInEmptyLobby_{0}")
    void testSocketDisconnection_clientCloseInEmptyLobby(@SuppressWarnings("unused") String name,
                                                         Supplier<Socket> socketFactory,
                                                         ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
        try {
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInEmptyLobby(
                    serverController -> {
                        try {
                            final ServerSocket serverSocket = new ServerSocket(0);
                            choosenPort.set(serverSocket.getLocalPort());
                            return new SocketConnectionServerController(serverController, serverSocket,
                                    -1, TimeUnit.MILLISECONDS,
                                    1, TimeUnit.SECONDS);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to bind SocketConnectionServerController", e);
                        }
                    },
                    nick -> {
                        try {
                            final Socket s = socketFactory.get();
                            socket.set(s);
                            return SocketClientNetManager.connect(
                                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                    1, TimeUnit.SECONDS, s,
                                    nick);
                        } catch (IOException | NickNotValidException e) {
                            throw new RuntimeException("Failed to create SocketClientNetManager", e);
                        }
                    },
                    () -> Objects.requireNonNull(socket.get()).close());
        } finally {
            if (socket.get() != null)
                cleanup.accept(socket.get());
        }
    }

    @MethodSource("socketSource")
    @ParameterizedTest(name = "testSocketDisconnection_clientCloseInLobby_{0}")
    void testSocketDisconnection_clientCloseInLobby(@SuppressWarnings("unused") String name,
                                                    Supplier<Socket> socketFactory,
                                                    ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
        try {
            Function<String, ClientNetManager> defaultSocketSupplier;
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInLobby(
                    serverController -> {
                        try {
                            final ServerSocket serverSocket = new ServerSocket(0);
                            choosenPort.set(serverSocket.getLocalPort());
                            return new SocketConnectionServerController(serverController, serverSocket,
                                    -1, TimeUnit.MILLISECONDS,
                                    1, TimeUnit.SECONDS);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to bind SocketConnectionServerController", e);
                        }
                    },
                    nick -> {
                        try {
                            final Socket s = socketFactory.get();
                            socket.set(s);
                            return SocketClientNetManager.connect(
                                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                    1, TimeUnit.SECONDS, s,
                                    nick);
                        } catch (IOException | NickNotValidException e) {
                            throw new RuntimeException("Failed to create SocketClientNetManager", e);
                        }
                    },
                    defaultSocketSupplier = nick -> {
                        try {
                            return SocketClientNetManager.connect(
                                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                    1, TimeUnit.SECONDS,
                                    nick);
                        } catch (IOException | NickNotValidException e) {
                            throw new RuntimeException("Failed to create SocketClientNetManager", e);
                        }
                    },
                    defaultSocketSupplier,
                    () -> Objects.requireNonNull(socket.get()).close());
        } finally {
            if (socket.get() != null)
                cleanup.accept(socket.get());
        }
    }

    @MethodSource("socketSource")
    @ParameterizedTest(name = "testSocketDisconnection_clientCloseInGame_{0}")
    void testSocketDisconnection_clientCloseInGame(@SuppressWarnings("unused") String name,
                                                   Supplier<Socket> socketFactory,
                                                   ThrowingConsumer<Socket> cleanup)
            throws Throwable {
        final AtomicInteger chosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
        try {
            Function<String, ClientNetManager> defaultSocketSupplier;
            DisconnectionIntegrationTest.doTestDisconnection_clientCloseInGame(
                    serverController -> {
                        try {
                            final ServerSocket serverSocket = new ServerSocket(0);
                            chosenPort.set(serverSocket.getLocalPort());
                            return new SocketConnectionServerController(serverController, serverSocket,
                                    -1, TimeUnit.MILLISECONDS,
                                    1, TimeUnit.SECONDS);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to bind SocketConnectionServerController", e);
                        }
                    },
                    defaultSocketSupplier = nick -> {
                        try {
                            return SocketClientNetManager.connect(
                                    new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                    1, TimeUnit.SECONDS,
                                    nick);
                        } catch (IOException | NickNotValidException e) {
                            throw new RuntimeException("Failed to create SocketClientNetManager", e);
                        }
                    },
                    nick -> {
                        try {
                            final Socket s = socketFactory.get();
                            socket.set(s);
                            return SocketClientNetManager.connect(
                                    new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                    1, TimeUnit.SECONDS, s,
                                    nick);
                        } catch (IOException | NickNotValidException e) {
                            throw new RuntimeException("Failed to create SocketClientNetManager", e);
                        }
                    },
                    defaultSocketSupplier,
                    () -> Objects.requireNonNull(socket.get()).close(),
                    defaultSocketSupplier);
        } finally {
            if (socket.get() != null)
                cleanup.accept(socket.get());
        }
    }
}