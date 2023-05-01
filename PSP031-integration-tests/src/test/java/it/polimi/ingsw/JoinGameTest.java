package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.rmi.RMIPortCapturingServerSocketFactory;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTest {
    private volatile ServerController serverController;
    private final AtomicInteger choosenPort = new AtomicInteger();
    private volatile RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory;
    private volatile String remoteName;
    private volatile SocketConnectionServerController socketConnectionServerController;
    private volatile RmiConnectionServerController rmiConnectionServerController;

    private final Supplier<ClientNetManager> rmi = () -> new RmiClientNetManager(null,
            portCapturingServerSocketFactory.getFirstCapturedPort(),
            remoteName);
    private final Supplier<ClientNetManager> socket = () -> {
        try {
            return new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()),
                    1, TimeUnit.SECONDS);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    };

    private static final Function<JoinGameTest, Supplier<ClientNetManager>> rmiFunction = f -> f.rmi;
    private static final Function<JoinGameTest, Supplier<ClientNetManager>> socketFunction = f -> f.socket;

    @BeforeEach
    void setUp() throws IOException {
        remoteName = "rmi_e2e_" + System.currentTimeMillis();
        var serverSocket = new ServerSocket(0);
        choosenPort.set(serverSocket.getLocalPort());
        serverController = new ServerController(5, TimeUnit.SECONDS);
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
    void twoPlayersTest(Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory1,
                        Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory2) {
        assertDoesNotThrow(() -> clientNetManagerFactory1.apply(this).get().joinGame("test_nick"), "First join failed");
        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory2.apply(this).get().joinGame("test_nick"),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersConcurrentTest(Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory1,
                                  Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory2)
            throws InterruptedException {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();

        Thread t1 = new Thread(() -> {
            try {
                clientNetManagerFactory1.apply(this).get().joinGame("test_nick");
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                clientNetManagerFactory2.apply(this).get().joinGame("test_nick");
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

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
    void threePlayersGameStartedTest(Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory1,
                                     Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory2,
                                     Function<JoinGameTest, Supplier<ClientNetManager>> clientNetManagerFactory3)
            throws Exception {
        CompletableFuture<GameView> game1 = new CompletableFuture<>();
        CompletableFuture<GameView> game2 = new CompletableFuture<>();
        assertDoesNotThrow(() -> {
            var player1 = clientNetManagerFactory1.apply(this).get().joinGame("player1");
            player1.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game1.complete(g.game());
            });
            player1.controller().ready(true);
        });
        assertDoesNotThrow(() -> {
            var player2 = clientNetManagerFactory2.apply(this).get().joinGame("player2");
            player2.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game2.complete(g.game());
            });
            player2.controller().ready(true);
        });

        game1.get(500, TimeUnit.MILLISECONDS);
        game2.get(500, TimeUnit.MILLISECONDS);

        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory3.apply(this).get().joinGame("player2"),
                "Same nick not failed");

    }
}
