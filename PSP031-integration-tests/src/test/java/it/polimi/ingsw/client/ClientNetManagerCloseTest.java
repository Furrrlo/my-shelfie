package it.polimi.ingsw.client;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.server.controller.LockProtected;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.model.ServerLobby;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ClientNetManagerCloseTest {

    public static void doTestSubsequentCloses(Function<ServerController, Closeable> bindServerController,
                                              Function<String, ClientNetManager> clientNetManagerFactory)
            throws Throwable {
        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        try (Closeable ignored = bindServerController.apply(new ServerController(500, TimeUnit.MILLISECONDS));
             var clientNetManager1 = clientNetManagerFactory.apply("p1")) {

            clientNetManager1.joinGame();
            assertDoesNotThrow(clientNetManager1::close, "First close caused an exception");
            assertDoesNotThrow(clientNetManager1::close, "Subsequent close caused an exception");
        }
    }
}
