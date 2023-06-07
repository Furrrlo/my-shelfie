package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Disabled // TODO: investigate why they deadlock
public class JoinGameTest {
    private volatile ServerController serverController;
    private final AtomicInteger choosenPort = new AtomicInteger();
    private volatile RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory;
    private volatile String remoteName;
    private volatile SocketConnectionServerController socketConnectionServerController;
    private volatile RmiConnectionServerController rmiConnectionServerController;

    private static final TestClientNetManagerFactory rmiFunction = (f, nick) -> RmiClientNetManager.connect(
            null,
            f.portCapturingServerSocketFactory.getFirstCapturedPort(),
            f.remoteName,
            nick);
    private static final TestClientNetManagerFactory socketFunction = (f, nick) -> SocketClientNetManager.connect(
            new InetSocketAddress(InetAddress.getLocalHost(), f.choosenPort.get()),
            1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
            nick);

    private interface TestClientNetManagerFactory {

        ClientNetManager create(JoinGameTest test, String nick) throws Exception;
    }

    @BeforeEach
    void setUp() throws IOException {
        remoteName = "rmi_e2e_" + System.currentTimeMillis();
        var serverSocket = new ServerSocket(0);
        choosenPort.set(serverSocket.getLocalPort());
        serverController = new ServerController(500, TimeUnit.MILLISECONDS);
        socketConnectionServerController = new SocketConnectionServerController(serverController, serverSocket,
                -1, TimeUnit.MILLISECONDS,
                1, TimeUnit.SECONDS);
        portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        rmiConnectionServerController = RmiConnectionServerController.bind(
                LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory),
                remoteName,
                serverController);
    }

    @AfterEach
    void tearDown() throws IOException {
        serverController.close();
        socketConnectionServerController.close();
        rmiConnectionServerController.close();
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
    void twoPlayersTest(TestClientNetManagerFactory clientNetManagerFactory1,
                        TestClientNetManagerFactory clientNetManagerFactory2) {
        assertDoesNotThrow(() -> clientNetManagerFactory1.create(this, "test_nick").joinGame(), "First join failed");
        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory2.create(this, "test_nick").joinGame(),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersConcurrentTest(TestClientNetManagerFactory clientNetManagerFactory1,
                                  TestClientNetManagerFactory clientNetManagerFactory2)
            throws InterruptedException {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();

        Thread t1 = new Thread(() -> {
            try {
                clientNetManagerFactory1.create(this, "test_nick").joinGame();
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                clientNetManagerFactory2.create(this, "test_nick").joinGame();
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
    void threePlayersGameStartedTest(TestClientNetManagerFactory clientNetManagerFactory1,
                                     TestClientNetManagerFactory clientNetManagerFactory2,
                                     TestClientNetManagerFactory clientNetManagerFactory3)
            throws Exception {
        CompletableFuture<GameView> game1 = new CompletableFuture<>();
        CompletableFuture<GameView> game2 = new CompletableFuture<>();
        assertDoesNotThrow(() -> {
            var player1 = clientNetManagerFactory1.create(this, "player1").joinGame();
            player1.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game1.complete(g.game());
            });
            player1.controller().ready(true);
            player1.controller().setRequiredPlayers(0);
        });
        assertDoesNotThrow(() -> {
            var player2 = clientNetManagerFactory2.create(this, "player2").joinGame();
            player2.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game2.complete(g.game());
            });
            player2.controller().ready(true);
        });

        game1.get(500, TimeUnit.MILLISECONDS);
        game2.get(500, TimeUnit.MILLISECONDS);

        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory3.create(this, "player2").joinGame(),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersGameStartedConcurrentReadyTest(TestClientNetManagerFactory clientNetManagerFactory1,
                                                  TestClientNetManagerFactory clientNetManagerFactory2)
            throws Exception {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();
        AtomicReference<LobbyAndController<? extends LobbyView>> player1 = new AtomicReference<>();
        AtomicReference<LobbyAndController<? extends LobbyView>> player2 = new AtomicReference<>();
        player1.set(clientNetManagerFactory1.create(this, "player1").joinGame());
        player2.set(clientNetManagerFactory2.create(this, "player2").joinGame());
        Thread t1 = new Thread(() -> {
            try {
                Random r = new Random();
                for (int i = 0; i < 20; i++) {
                    Objects.requireNonNull(player1.get()).controller().ready(r.nextBoolean());
                    Thread.sleep(r.nextInt(10));
                }
                Objects.requireNonNull(player1.get()).controller().ready(true);
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Random r = new Random();
                for (int i = 0; i < 20; i++) {
                    Objects.requireNonNull(player2.get()).controller().ready(r.nextBoolean());
                    Thread.sleep(r.nextInt(10));
                }
                Objects.requireNonNull(player2.get()).controller().ready(true);
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

        throwableList.forEach(Throwable::printStackTrace);
        assertTrue(throwableList.isEmpty());
    }
}
