package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.function.Executable;

import java.io.Closeable;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static it.polimi.ingsw.updater.UpdatersIntegrationTest.ensurePropertyUpdated;
import static org.junit.jupiter.api.Assertions.*;

public class DisconnectionIntegrationTest {

    public static void doTestDisconnection_clientCloseInEmptyLobby(Function<ServerController, Closeable> bindServerController,
                                                                   Supplier<ClientNetManager> clientNetManagerFactory,
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
                                      HeartbeatHandler heartbeatHandler,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                System.out.println("joining");
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, lobbyUpdaterFactory,
                        lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.complete(null);
                return lobby;
            }
        }; var ignored = bindServerController.apply(serverController)) {
            ClientNetManager clientNetManager = clientNetManagerFactory.get();
            LobbyView lobbyView = clientNetManager.joinGame(nick).lobby();
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
                                                              Supplier<ClientNetManager> clientNetManagerFactory1,
                                                              Supplier<ClientNetManager> clientNetManagerFactory2,
                                                              Supplier<ClientNetManager> clientNetManagerFactory3,
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
                                      HeartbeatHandler heartbeatHandler,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, lobbyUpdaterFactory,
                        lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.countDown();
                return lobby;
            }
        }; var ignored = bindServerController.apply(serverController)) {
            ClientNetManager clientManager1 = clientNetManagerFactory1.get();
            LobbyView lobbyView = clientManager1.joinGame(testNickname).lobby();

            ClientNetManager clientManager2 = clientNetManagerFactory2.get();
            LobbyView lobbyView2 = clientManager2.joinGame("p2").lobby();

            ClientNetManager clientManager3 = clientNetManagerFactory3.get();
            LobbyView lobbyView3 = clientManager3.joinGame("p3").lobby();

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
                                                             Supplier<ClientNetManager> clientNetManagerFactory1,
                                                             Supplier<ClientNetManager> clientNetManagerFactory2,
                                                             Supplier<ClientNetManager> clientNetManagerFactory3,
                                                             Executable disconnect,
                                                             Supplier<ClientNetManager> clientNetManagerFactory2New)
            throws Throwable {
        final var rnd = new Random();

        final AtomicInteger joinedPlayers = new AtomicInteger();
        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        final var serverAllJoined = new CompletableFuture<Void>();
        final var serverPlayerDisconnected = new CompletableFuture<Void>();

        try (var serverController = new ServerController(5, TimeUnit.SECONDS) {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lockedLobby.lobby());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      HeartbeatHandler heartbeatHandler,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
                    throws DisconnectedException {
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, lobbyUpdaterFactory,
                        lobbyControllerFactory,
                        gameControllerFactory);
                if (joinedPlayers.incrementAndGet() >= 3)
                    serverAllJoined.complete(null);
                return lobby;
            }
        }; var ignored = bindServerController.apply(serverController)) {

            //Connect 3 client
            LobbyView lobbyView = clientNetManagerFactory1.get().joinGame("test_1").lobby();

            ClientNetManager socketClientManager2 = clientNetManagerFactory2.get();
            LobbyView lobbyView2 = socketClientManager2.joinGame("test_2").lobby();

            LobbyView lobbyView3 = clientNetManagerFactory3.get().joinGame("test_3").lobby();

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
                        serverGame = LobbyServerController.createGame(0, new Random(), serverLobby.joinedPlayers().get()),
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

            serverController.runOnOnlyLobbyLocks(
                    () -> serverPlayer2.connected().registerObserver(value -> serverPlayerDisconnected.complete(null)));
            disconnect.execute();
            serverPlayerDisconnected.get(10, TimeUnit.SECONDS);
            assertFalse(serverPlayer2.connected().get());

            //Client gets stuck on receive(), kill it
            //socketClientManager2.kill();

            //Restart player test_2 creating a new client
            socketClientManager2 = clientNetManagerFactory2New.get();
            LobbyView lobbyView2_new = socketClientManager2.joinGame("test_2").lobby();
            final var gamePromise2 = new CompletableFuture<GameAndController<?>>();
            lobbyView2_new.game().registerObserver(gamePromise2::complete);

            //Workaround to made it work with rmi:
            // with rmi joinGame returns a lobby with already a game
            // with sockets the game is added with a subsequent packet.
            if (lobbyView2_new.game().get() != null)
                gamePromise2.complete(lobbyView2_new.game().get());

            final var newClient2Game = gamePromise2.get(500, TimeUnit.MILLISECONDS).game();

            assertTrue(serverPlayer2.connected().get());

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