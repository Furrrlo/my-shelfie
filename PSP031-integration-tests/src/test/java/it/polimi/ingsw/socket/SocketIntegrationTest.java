package it.polimi.ingsw.socket;

import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import it.polimi.ingsw.updater.UpdatersIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SocketIntegrationTest {
    @Test
    void testSocketUpdaters() throws Exception {
        final int port = 12345;
        UpdatersIntegrationTest.doTestUpdaters(
                serverController -> {
                    try {
                        new Thread(new SocketConnectionServerController(serverController, port)).start();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to bind SocketConnectionServerController", e);
                    }
                },
                () -> {
                    try {
                        return new SocketClientNetManager(new InetSocketAddress(InetAddress.getLocalHost(), port));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create SocketClientNetManager", e);
                    }
                });
    }
}
