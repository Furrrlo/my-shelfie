package it.polimi.ingsw.socket;

import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.ClientNetManagerCloseTest;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.ControllersIntegrationTest;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketIntegrationTest {
    @Test
    void testSocketUpdaters() throws Exception {
        final AtomicInteger choosenPort = new AtomicInteger();
        UpdatersIntegrationTest.doTestUpdaters(
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
                        return SocketClientNetManager.connect(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS, nick);
                    } catch (IOException | NickNotValidException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }

    @Test
    void testSocketControllers() throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        ControllersIntegrationTest.doTestControllers(
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
                        return SocketClientNetManager.connect(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS, nick);
                    } catch (IOException | NickNotValidException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }

    @Test
    void testClientNetManagerSubsequentCloses() throws Throwable {
        final AtomicInteger choosenPort = new AtomicInteger();
        ClientNetManagerCloseTest.doTestSubsequentCloses(
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
                        return SocketClientNetManager.connect(
                                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                                1, TimeUnit.SECONDS, nick);
                    } catch (IOException | NickNotValidException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }
}
