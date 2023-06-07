package it.polimi.ingsw;

import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.LobbyView;
import it.polimi.ingsw.rmi.RMIPortCapturingServerSocketFactory;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Disabled // TODO: investigate why they deadlock
public class JoinGameTest {

    private static final Function<JoinGameTest, TestClientNetManagerFactory> rmiFunction = f -> f.rmiClientFactory;
    private static final Function<JoinGameTest, TestClientNetManagerFactory> socketFunction = f -> f.socketClientFactory;

    private volatile ServerController serverController;
    private volatile SocketConnectionServerController socketConnectionServerController;
    private volatile RmiConnectionServerController rmiConnectionServerController;
    private volatile CloseablesTracker clientCloseables;

    private volatile TestClientNetManagerFactory rmiClientFactory;
    private volatile TestClientNetManagerFactory socketClientFactory;

    private interface TestClientNetManagerFactory {

        LobbyAndController<? extends LobbyView> createAndJoin(String nick) throws Exception;
    }

    @BeforeEach
    void setUp() throws IOException {
        final var serverSocket = new ServerSocket(0);
        final var choosenPort = serverSocket.getLocalPort();
        serverController = new ServerController(500, TimeUnit.MILLISECONDS);
        socketConnectionServerController = new SocketConnectionServerController(serverController, serverSocket,
                -1, TimeUnit.MILLISECONDS,
                1, TimeUnit.SECONDS);

        final var remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        rmiConnectionServerController = RmiConnectionServerController.bind(
                LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory),
                remoteName,
                serverController);

        clientCloseables = new CloseablesTracker();
        rmiClientFactory = nick -> clientCloseables.register(RmiClientNetManager.connect(
                null,
                portCapturingServerSocketFactory.getFirstCapturedPort(),
                remoteName,
                nick)).joinGame();
        socketClientFactory = nick -> clientCloseables.register(SocketClientNetManager.connect(
                new InetSocketAddress(InetAddress.getLocalHost(), choosenPort),
                1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
                nick)).joinGame();
    }

    @AfterEach
    @SuppressWarnings("EmptyTryBlock")
    void tearDown() throws IOException {
        try (var ignored1 = serverController;
             var ignored2 = socketConnectionServerController;
             var ignored3 = rmiConnectionServerController;
             var ignored4 = clientCloseables;) {
            // Close all of them, but try even if one fails
        }
    }

    public static Stream<Arguments> twoPlayersTest() {
        return Stream.of(
                Arguments.of(rmiFunction, socketFunction),
                Arguments.of(socketFunction, rmiFunction),
                Arguments.of(rmiFunction, rmiFunction),
                Arguments.of(socketFunction, socketFunction));
    }

    @ParameterizedTest
    @MethodSource
    void twoPlayersTest(Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory1,
                        Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory2) {
        assertDoesNotThrow(() -> clientNetManagerFactory1.apply(this).createAndJoin("test_nick"), "First join failed");
        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory2.apply(this).createAndJoin("test_nick"),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersConcurrentTest(Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory1,
                                  Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory2)
            throws InterruptedException {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();

        Thread t1 = new Thread(() -> {
            try {
                clientNetManagerFactory1.apply(this).createAndJoin("test_nick");
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                clientNetManagerFactory2.apply(this).createAndJoin("test_nick");
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        t1.start();
        t2.start();

        t1.join(500);
        assertFalse(t1.isAlive());
        t2.join(500);
        assertFalse(t2.isAlive());

        assertEquals(1, throwableList.size());
        assertInstanceOf(NickNotValidException.class, throwableList.get(0));
    }

    public static Stream<Arguments> threePlayersTest() {
        return Stream.of(
                Arguments.of(rmiFunction, rmiFunction, rmiFunction),
                Arguments.of(rmiFunction, rmiFunction, socketFunction),
                Arguments.of(rmiFunction, socketFunction, rmiFunction),
                Arguments.of(rmiFunction, socketFunction, socketFunction),
                Arguments.of(socketFunction, rmiFunction, rmiFunction),
                Arguments.of(socketFunction, rmiFunction, socketFunction),
                Arguments.of(socketFunction, socketFunction, rmiFunction),
                Arguments.of(socketFunction, socketFunction, socketFunction));
    }

    @ParameterizedTest
    @MethodSource("threePlayersTest")
    void threePlayersGameStartedTest(Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory1,
                                     Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory2,
                                     Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory3)
            throws Exception {
        CompletableFuture<GameView> game1 = new CompletableFuture<>();
        CompletableFuture<GameView> game2 = new CompletableFuture<>();
        assertDoesNotThrow(() -> {
            var player1 = clientNetManagerFactory1.apply(this).createAndJoin("player1");
            player1.lobby().game().registerObserver(g -> {
                try {
                    assertNotNull(g);
                    game1.complete(g.game());
                } catch (Throwable t) {
                    game1.completeExceptionally(t);
                }
            });
            player1.controller().ready(true);
            player1.controller().setRequiredPlayers(0);
        });
        assertDoesNotThrow(() -> {
            var player2 = clientNetManagerFactory2.apply(this).createAndJoin("player2");
            player2.lobby().game().registerObserver(g -> {
                try {
                    assertNotNull(g);
                    game2.complete(g.game());
                } catch (Throwable t) {
                    game2.completeExceptionally(t);
                }
            });
            player2.controller().ready(true);
        });

        game1.get(500, TimeUnit.MILLISECONDS);
        game2.get(500, TimeUnit.MILLISECONDS);

        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory3.apply(this).createAndJoin("player2"),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersGameStartedConcurrentReadyTest(Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory1,
                                                  Function<JoinGameTest, TestClientNetManagerFactory> clientNetManagerFactory2)
            throws Exception {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();
        var player1 = clientNetManagerFactory1.apply(this).createAndJoin("player1");
        var player2 = clientNetManagerFactory2.apply(this).createAndJoin("player2");

        Thread t1 = new Thread(() -> {
            try {
                Random r = new Random();
                for (int i = 0; i < 20; i++) {
                    player1.controller().ready(r.nextBoolean());
                    Thread.sleep(r.nextInt(10));
                }
                player1.controller().ready(true);
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Random r = new Random();
                for (int i = 0; i < 20; i++) {
                    player2.controller().ready(r.nextBoolean());
                    Thread.sleep(r.nextInt(10));
                }
                player2.controller().ready(true);
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        t1.start();
        t2.start();

        t1.join(500);
        assertFalse(t1.isAlive());
        t2.join(500);
        assertFalse(t2.isAlive());

        assertDoesNotThrow(() -> {
            if (!throwableList.isEmpty()) {
                var ex = new Exception("Test failed");
                throwableList.forEach(ex::addSuppressed);
                throw ex;
            }
        });
    }
}
