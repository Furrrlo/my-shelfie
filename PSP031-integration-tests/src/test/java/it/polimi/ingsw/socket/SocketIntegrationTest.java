package it.polimi.ingsw.socket;

import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
                        new Thread(new SocketConnectionServerController(serverController, serverSocket)).start();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to bind SocketConnectionServerController", e);
                    }
                },
                () -> {
                    try {
                        return new SocketClientNetManager(new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }
}
