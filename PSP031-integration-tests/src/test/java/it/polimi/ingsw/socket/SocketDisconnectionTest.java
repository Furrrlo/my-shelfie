package it.polimi.ingsw.socket;

import it.polimi.ingsw.DisconnectionIntegrationTest;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SocketDisconnectionTest {
    @Test
    void testSocketDisconnection_clientCloseInEmptyLobby() throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
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
                () -> {
                    try {
                        final Socket s = new Socket();
                        socket.set(s);
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS, s);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> Objects.requireNonNull(socket.get()).close());
    }

    @Test
    void testSocketDisconnection_clientCloseInLobby() throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
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
                () -> {
                    try {
                        final Socket s = new Socket();
                        socket.set(s);
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS, s);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> {
                    try {
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> {
                    try {
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> Objects.requireNonNull(socket.get()).close());
    }

    @Test
    void testSocketDisconnection_clientCloseInGame() throws Throwable {
        final AtomicInteger chosenPort = new AtomicInteger();
        final AtomicReference<Socket> socket = new AtomicReference<>();
        DisconnectionIntegrationTest.testSocketDisconnection_clientCloseInGame(
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
                () -> {
                    try {
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                1, TimeUnit.SECONDS);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> {
                    try {
                        final Socket s = new Socket();
                        socket.set(s);
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                1, TimeUnit.SECONDS, s);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> {
                    try {
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                1, TimeUnit.SECONDS);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                },
                () -> Objects.requireNonNull(socket.get()).close(),
                () -> {
                    try {
                        return new SocketClientNetManager(
                                new InetSocketAddress(InetAddress.getLocalHost(), chosenPort.get()),
                                1, TimeUnit.SECONDS);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }
}