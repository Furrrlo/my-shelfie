package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static it.polimi.ingsw.updater.UpdatersIntegrationTest.ensurePropertyUpdated;
import static org.junit.jupiter.api.Assertions.*;

public class DisconnectionIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectionIntegrationTest.class);

    public static void doTestDisconnection_clientCloseInEmptyLobby(Function<ServerController, Closeable> bindServerController,
                                                                   Function<String, ClientNetManager> clientNetManagerFactory,
                                                                   Executable close)
            throws Throwable {

        final String nick = "test_nickname";

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverJoined = new CompletableFuture<Void>();
        final var serverPlayerRemoved = new CompletableFuture<Void>();

        try (var serverController = new ServerController(5, TimeUnit.SECONDS) {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lockedLobby.lobby().getUnsafe());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      LobbyControllerFactory lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                LOGGER.trace("joining");
                var lobby = super.joinGame(nick, observableTracker, lobbyUpdaterFactory, lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.complete(null);
                return lobby;
            }
        };
             var ignored = bindServerController.apply(serverController);
             var clientNetManager = clientNetManagerFactory.apply(nick)) {

            LobbyView lobbyView = clientNetManager.joinGame().lobby();
            ServerLobby serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            serverJoined.get(500, TimeUnit.MILLISECONDS);

            assertSame(1, serverLobby.joinedPlayers().get().size());
            serverController.runOnOnlyLobbyLocks(
                    () -> serverLobby.joinedPlayers().registerObserver(value -> serverPlayerRemoved.complete(null)));
            close.execute();
            serverPlayerRemoved.get(10, TimeUnit.SECONDS);
            assertSame(0, serverLobby.joinedPlayers().get().size());
        }
    }

    public static void doTestDisconnection_clientCloseInLobby(Function<ServerController, Closeable> bindServerController,
                                                              Function<String, ClientNetManager> clientNetManagerFactory1,
                                                              Function<String, ClientNetManager> clientNetManagerFactory2,
                                                              Function<String, ClientNetManager> clientNetManagerFactory3,
                                                              Executable disconnect)
            throws Throwable {
        final String testNickname = "test_nickname";

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverLobbyCount = new CountDownLatch(3);
        final var serverJoined = new CountDownLatch(3);
        final var serverPlayerRemoved = new CompletableFuture<Void>();

        try (var serverController = new ServerController(5, TimeUnit.SECONDS) {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                if (nick.equals(testNickname))
                    serverLobbyPromise.complete(lockedLobby.lobby().getUnsafe());
                serverLobbyCount.countDown();
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      LobbyControllerFactory lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                var lobby = super.joinGame(nick, observableTracker, lobbyUpdaterFactory, lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.countDown();
                return lobby;
            }
        }; var ignored = bindServerController.apply(serverController);
             var closeables = new CloseablesTracker()) {

            ClientNetManager clientManager1 = closeables.register(clientNetManagerFactory1.apply(testNickname));
            var player1 = clientManager1.joinGame();
            player1.controller().setRequiredPlayers(0);

            ClientNetManager clientManager2 = closeables.register(clientNetManagerFactory2.apply("p2"));
            LobbyView lobbyView2 = clientManager2.joinGame().lobby();

            ClientNetManager clientManager3 = closeables.register(clientNetManagerFactory3.apply("p3"));
            LobbyView lobbyView3 = clientManager3.joinGame().lobby();

            assertTrue(serverJoined.await(500, TimeUnit.MILLISECONDS));

            ServerLobby serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);

            assertSame(3, serverLobby.joinedPlayers().get().size());

            serverController.runOnOnlyLobbyLocks(
                    () -> serverLobby.joinedPlayers().registerObserver(value -> serverPlayerRemoved.complete(null)));
            disconnect.execute();

            serverPlayerRemoved.get(10, TimeUnit.SECONDS);
            assertSame(2, serverLobby.joinedPlayers().get().size());
            assertTrue(serverLobby.joinedPlayers().get().stream()
                    .map(LobbyPlayer::getNick)
                    .noneMatch(s -> s.equals(testNickname)));
        }

    }

    public static void doTestDisconnection_clientCloseInGame(Function<ServerController, Closeable> bindServerController,
                                                             Function<String, ClientNetManager> clientNetManagerFactory1,
                                                             Function<String, ClientNetManager> clientNetManagerFactory2,
                                                             Function<String, ClientNetManager> clientNetManagerFactory3,
                                                             Executable disconnect,
                                                             Function<String, ClientNetManager> clientNetManagerFactory2New)
            throws Throwable {
        final var rnd = new Random();

        final AtomicInteger joinedPlayers = new AtomicInteger();
        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        final var serverAllJoined = new CompletableFuture<Void>();
        final var serverPlayerDisconnected = new CompletableFuture<Boolean>();
        final var client2Connected = new CompletableFuture<Boolean>();

        try (var serverController = new ServerController(5, TimeUnit.SECONDS) {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lockedLobby.lobby());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      LobbyControllerFactory lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                var lobby = super.joinGame(nick, observableTracker, lobbyUpdaterFactory, lobbyControllerFactory,
                        gameControllerFactory);
                if (joinedPlayers.incrementAndGet() >= 3)
                    serverAllJoined.complete(null);
                return lobby;
            }
        };
             var ignored = bindServerController.apply(serverController);
             var closeables = new CloseablesTracker()) {

            //Connect 3 client

            var player1 = closeables.register(clientNetManagerFactory1.apply("test_1")).joinGame();
            player1.controller().setRequiredPlayers(0);

            var clientNetManager2 = closeables.register(clientNetManagerFactory2.apply("test_2"));
            LobbyView lobbyView2 = clientNetManager2.joinGame().lobby();

            LobbyView lobbyView3 = closeables.register(clientNetManagerFactory3.apply("test_3")).joinGame().lobby();

            var lockedServerLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            serverAllJoined.get(500, TimeUnit.MILLISECONDS);

            assertSame(3, lockedServerLobby.getUnsafe().joinedPlayers().get().size());

            final var gamePromise = new CompletableFuture<GameAndController<?>>();
            lobbyView2.game().registerObserver(gamePromise::complete);

            //Start game
            //Players should be "ready" before starting a game, but we don't care in this test
            final ServerGame serverGame;
            try (var lobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = lobbyCloseable.obj();
                serverLobby.game().set(new ServerGameAndController<>(
                        serverGame = LobbyServerController.createGame(0, serverLobby.joinedPlayers().get()),
                        new GameServerController(new LockProtected<>(serverGame, lockedServerLobby.getLock()))));
            }

            final var client2Game = gamePromise.get(500, TimeUnit.MILLISECONDS).game();

            //Set random tiles to serverPlayers
            //No need to check client updates in this test
            serverController.runOnOnlyLobbyLocks(() -> {
                for (int i = 0; i < serverGame.getPlayers().size(); i++) {
                    final var serverPlayer = serverGame.getPlayers().get(i);
                    for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverPlayer.getShelfie()
                            .tiles()::iterator)
                        serverPlayer.getShelfie().tile(tile.row(), tile.col())
                                .set(new Tile(Color.values()[rnd.nextInt(Color.values().length)]));
                }
            });

            //Disconnect player test_2
            final var serverPlayer2 = serverGame.getPlayers().stream()
                    .filter(p -> p.getNick().equals("test_2"))
                    .findFirst()
                    .orElseThrow();

            // Make sure the current turn is not set on serverPlayer2, as we are going to disconnect it.
            // That would cause a change between the old game instance (which will no longer be updated) and
            // the new one, so we would not be able to easily compare the two and check that they are the same
            serverController.runOnOnlyLobbyLocks(() -> {
                if (serverGame.currentTurn().get().equals(serverPlayer2))
                    serverGame.currentTurn().set(serverGame.getPlayers().stream()
                            .filter(p -> !p.equals(serverPlayer2))
                            .findFirst()
                            .orElseThrow());
            });

            serverController.runOnOnlyLobbyLocks(
                    () -> {
                        serverPlayer2.connected().registerObserver(serverPlayerDisconnected::complete);
                        Objects.requireNonNull(lobbyView2.game().get()).game().thePlayer().connected()
                                .registerObserver(client2Connected::complete);
                    });
            disconnect.execute();
            assertFalse(serverPlayerDisconnected.get(10, TimeUnit.SECONDS));
            assertFalse(client2Connected.get(15, TimeUnit.SECONDS));

            //Restart player test_2 creating a new client
            clientNetManager2 = closeables.register(clientNetManagerFactory2New.apply("test_2"));
            LobbyView lobbyView2_new = clientNetManager2.joinGame().lobby();

            final var newClient2GameAndController = lobbyView2_new.game().get();
            assertNotNull(newClient2GameAndController);
            final var newClient2Game = newClient2GameAndController.game();

            assertTrue(serverPlayer2.connected().get());

            //Cast and set connected = true to compare the old game with the new one
            ((Property<Boolean>) client2Game.thePlayer().connected()).set(true);

            assertEquals(newClient2Game, client2Game);
            assertNotSame(newClient2Game, client2Game);

            //ensure that updaters work on new player
            for (int i = 0; i < serverGame.getPlayers().size(); i++) {
                final var serverPlayer = serverGame.getPlayers().get(i);
                final var clientPlayer = newClient2Game.getPlayers().get(i);

                for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverPlayer.getShelfie().tiles()::iterator)
                    ensurePropertyUpdated(
                            serverPlayer.getNick() + "ShelfTile" + tile.row() + "x" + tile.col(),
                            new Tile(Color.values()[rnd.nextInt(Color.values().length)]),
                            serverController,
                            serverPlayer.getShelfie().tile(tile.row(), tile.col()),
                            clientPlayer.getShelfie().tile(tile.row(), tile.col()));
            }

            //check that the old game is no longer updated, just to be sure
            assertNotEquals(newClient2Game, client2Game);
        }
    }

}