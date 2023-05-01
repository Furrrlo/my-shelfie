package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.rmi.RMIPortCapturingServerSocketFactory;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTest {
    private static @Nullable ServerController serverController;
    private static @Nullable AtomicInteger choosenPort;
    private static @Nullable RMIPortCapturingServerSocketFactory portCapturingServerSocketFactory;
    private static @Nullable String remoteName;
    private static @Nullable SocketConnectionServerController socketConnectionServerController;
    private static @Nullable RmiConnectionServerController rmiConnectionServerController;
    private static @Nullable Registry registry;

    private static final Supplier<?> rmi = () -> new RmiClientNetManager(null,
            Objects.requireNonNull(portCapturingServerSocketFactory).getFirstCapturedPort(),
            Objects.requireNonNull(remoteName));
    private static final Supplier<?> socket = () -> {
        try {
            return new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), Objects.requireNonNull(choosenPort).get()),
                    1, TimeUnit.SECONDS);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    };

    @BeforeAll
    static void beforeAll() {
        choosenPort = new AtomicInteger();
        portCapturingServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        try {
            registry = LocateRegistry.createRegistry(0, null, portCapturingServerSocketFactory);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        remoteName = "rmi_e2e_" + System.currentTimeMillis();
        var serverSocket = new ServerSocket(0);
        Objects.requireNonNull(choosenPort).set(serverSocket.getLocalPort());
        serverController = new ServerController(5, TimeUnit.SECONDS);
        socketConnectionServerController = new SocketConnectionServerController(serverController, serverSocket,
                -1, TimeUnit.MILLISECONDS,
                1, TimeUnit.SECONDS);
        rmiConnectionServerController = RmiConnectionServerController.bind(
                Objects.requireNonNull(registry),
                Objects.requireNonNull(remoteName),
                serverController);
    }

    @AfterEach
    void tearDown() throws IOException {
        Objects.requireNonNull(serverController).close();
        Objects.requireNonNull(socketConnectionServerController).close();
        Objects.requireNonNull(rmiConnectionServerController).close();
    }

    public static Stream<Arguments> twoPlayersTest() {
        return Stream.of(
                Arguments.of(rmi, socket),
                Arguments.of(socket, rmi),
                Arguments.of(rmi, rmi),
                Arguments.of(socket, socket));
    }

    @ParameterizedTest
    @MethodSource
    void twoPlayersTest(Supplier<ClientNetManager> clientNetManagerFactory1,
                        Supplier<ClientNetManager> clientNetManagerFactory2) {
        assertDoesNotThrow(() -> clientNetManagerFactory1.get().joinGame("test_nick"), "First join failed");
        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory2.get().joinGame("test_nick"),
                "Same nick not failed");
    }

    @ParameterizedTest
    @MethodSource("twoPlayersTest")
    void twoPlayersConcurrentTest(Supplier<ClientNetManager> clientNetManagerFactory1,
                                  Supplier<ClientNetManager> clientNetManagerFactory2)
            throws InterruptedException {
        List<Throwable> throwableList = new CopyOnWriteArrayList<>();

        Thread t1 = new Thread(() -> {
            try {
                clientNetManagerFactory1.get().joinGame("test_nick");
            } catch (Exception e) {
                throwableList.add(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                clientNetManagerFactory2.get().joinGame("test_nick");
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
                Arguments.of(rmi, rmi, rmi),
                Arguments.of(rmi, rmi, socket),
                Arguments.of(rmi, socket, rmi),
                Arguments.of(rmi, socket, socket),
                Arguments.of(socket, rmi, rmi),
                Arguments.of(socket, rmi, socket),
                Arguments.of(socket, socket, rmi),
                Arguments.of(socket, socket, socket));
    }

    @ParameterizedTest
    @MethodSource("threePlayersTest")
    void threePlayersGameStartedTest(Supplier<ClientNetManager> clientNetManagerFactory1,
                                     Supplier<ClientNetManager> clientNetManagerFactory2,
                                     Supplier<ClientNetManager> clientNetManagerFactory3)
            throws Exception {
        CompletableFuture<GameView> game1 = new CompletableFuture<>();
        CompletableFuture<GameView> game2 = new CompletableFuture<>();
        assertDoesNotThrow(() -> {
            var player1 = clientNetManagerFactory1.get().joinGame("player1");
            player1.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game1.complete(g.game());
            });
            player1.controller().ready(true);
        });
        assertDoesNotThrow(() -> {
            var player2 = clientNetManagerFactory2.get().joinGame("player2");
            player2.lobby().game().registerObserver(g -> {
                assertNotNull(g);
                game2.complete(g.game());
            });
            player2.controller().ready(true);
        });

        game1.get(500, TimeUnit.MILLISECONDS);
        game2.get(500, TimeUnit.MILLISECONDS);

        assertThrows(NickNotValidException.class, () -> clientNetManagerFactory3.get().joinGame("player2"),
                "Same nick not failed");

    }
}
