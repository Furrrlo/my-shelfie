package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.rmi.RMIPortCapturingServerSocketFactory;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameOverIntegrationTest {

    static Stream<Arguments> testSource() {
        TestClientNetManagerFactory socket = (nick, socketPort, remoteName, rmiPort) -> SocketClientNetManager.connect(
                new InetSocketAddress(InetAddress.getLocalHost(), socketPort),
                1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
                nick);
        TestClientNetManagerFactory rmi = (nick, socketPort, remoteName, rmiPort) -> RmiClientNetManager.connect(
                null, rmiPort, remoteName, nick);
        return Stream.of(
                testArgs(socket, rmi),
                testArgs(rmi, socket),
                testArgs(socket, socket),
                testArgs(rmi, rmi));
    }

    private static Arguments testArgs(TestClientNetManagerFactory first,
                                      TestClientNetManagerFactory second) {
        return Arguments.of(first, second);
    }

    @FunctionalInterface
    private interface TestClientNetManagerFactory {

        ClientNetManager create(String nick, int socketPort, String remoteName, int rmiPort) throws Exception;
    }

    @ParameterizedTest
    @MethodSource("testSource")
    public void testGameOver(TestClientNetManagerFactory first, TestClientNetManagerFactory second) throws Throwable {
        var firstNickname = "firstGuy";
        var secondNickname = "secondGuy";

        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        final var serverJoined = new CountDownLatch(2);

        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final var rmiServerSocketFactory = new RMIPortCapturingServerSocketFactory();
        final ServerSocket serverSocket = new ServerSocket(0);
        final var chosenPort = serverSocket.getLocalPort();
        try (var serverController = new ServerController(500, TimeUnit.MILLISECONDS) {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                if (nick.equals(firstNickname))
                    serverLobbyPromise.complete(lockedLobby.lobby());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      PlayerObservableTracker observableTracker,
                                      Runnable onGameOver,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      LobbyControllerFactory lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                var lobby = super.joinGame(nick, observableTracker, onGameOver, lobbyUpdaterFactory, lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.countDown();
                return lobby;
            }
        }; var ignored1 = RmiConnectionServerController.bind(
                LocateRegistry.createRegistry(0, null, rmiServerSocketFactory),
                remoteName,
                serverController);
             var ignored2 = new SocketConnectionServerController(serverController, serverSocket,
                     -1, TimeUnit.MILLISECONDS,
                     1, TimeUnit.SECONDS);
             var closeables = new CloseablesTracker()) {

            // Make sure to connect the socket first
            ClientNetManager firstClient = closeables.register(first.create(firstNickname,
                    chosenPort, remoteName, rmiServerSocketFactory.getFirstCapturedPort()));
            var firstLobby = firstClient.joinGame();
            firstLobby.controller().setRequiredPlayers(0);

            ClientNetManager secondClient = closeables.register(second.create(secondNickname,
                    chosenPort, remoteName, rmiServerSocketFactory.getFirstCapturedPort()));
            var secondLobby = secondClient.joinGame();

            assertTrue(serverJoined.await(500, TimeUnit.MILLISECONDS));
            var lockedServerLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);

            final var firstGamePromise = new CompletableFuture<GameAndController<?>>();
            firstLobby.lobby().game().registerObserver(firstGamePromise::complete);
            final var secondGamePromise = new CompletableFuture<GameAndController<?>>();
            secondLobby.lobby().game().registerObserver(secondGamePromise::complete);

            // Start game
            // Players should be "ready" before starting a game, but we don't care in this test
            final ServerGame serverGame;
            try (var lobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = lobbyCloseable.obj();
                serverLobby.game().set(new ServerGameAndController<>(
                        serverGame = LobbyServerController.createGame(
                                0,
                                // Seed the random to always get the second guy starting
                                new Random(-1744238659L),
                                serverLobby.joinedPlayers().get()),
                        new GameServerController(new LockProtected<>(serverGame, lockedServerLobby.getLock()))));
            }

            final var firstGame = firstGamePromise.get(500, TimeUnit.MILLISECONDS);
            final var secondGame = secondGamePromise.get(500, TimeUnit.MILLISECONDS);

            serverController.runOnOnlyLobbyLocks(() -> {
                // Fill the players shelfies
                for (ServerPlayer player : serverGame.getPlayers()) {
                    var filledShelfie = new Color[][] {
                            //@formatter:off
                            new Color[] { Color.PINK     , null        , Color.PINK  , Color.PINK     , Color.PINK   },
                            new Color[] { Color.GREEN    , Color.PINK  , Color.YELLOW, Color.YELLOW   , Color.GREEN  },
                            new Color[] { Color.PINK     , Color.BLUE  , Color.YELLOW, Color.PINK     , Color.YELLOW },
                            new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW, Color.PINK     , Color.YELLOW },
                            new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN , Color.LIGHTBLUE, Color.WHITE  },
                            new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE  , Color.BLUE     , Color.BLUE   }
                            //@formatter:on
                    };
                    for (int r = filledShelfie.length - 1; r >= 0; r--) {
                        for (int c = 0; c < filledShelfie[r].length; c++) {
                            Property.setNullable(player.getShelfie().tile(r, c),
                                    filledShelfie[r][c] == null ? null : new Tile(filledShelfie[r][c]));
                        }
                    }
                }
            });

            secondGame.controller().makeMove(List.of(new BoardCoord(1, 3)), 1);
            firstGame.controller().makeMove(List.of(new BoardCoord(1, 4)), 1);

            assertTrue(serverGame.endGame().get());
            assertTrue(firstGame.game().endGame().get());
            assertTrue(secondGame.game().endGame().get());

            assertTrue(firstGame.game().thePlayer().connected().get());
            assertTrue(secondGame.game().thePlayer().connected().get());
        }
    }

}