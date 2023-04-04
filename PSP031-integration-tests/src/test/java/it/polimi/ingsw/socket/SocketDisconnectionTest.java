package it.polimi.ingsw.socket;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.time.Clock;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.polimi.ingsw.updater.UpdatersIntegrationTest.ensurePropertyUpdated;
import static org.junit.jupiter.api.Assertions.*;

public class SocketDisconnectionTest {
    @Test
    void testSocketDisconnection_clientCloseInLobby() throws Exception {
        final String nick = "test_nickname";
        final var rnd = new Random();

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverJoined = new CompletableFuture<Void>();
        final var serverPlayerRemoved = new CompletableFuture<Void>();

        final AtomicInteger choosenPort = new AtomicInteger();
        final ServerSocket serverSocket = new ServerSocket(0);
        choosenPort.set(serverSocket.getLocalPort());

        try (var ignored = new SocketConnectionServerController(new ServerController() {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lockedLobby.lobby().getUnsafe());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      Consumer<Clock> heartbeatHandler,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory) {
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, lobbyUpdaterFactory,
                        lobbyControllerFactory,
                        gameControllerFactory);
                serverJoined.complete(null);
                return lobby;
            }
        }, serverSocket)) {
            var socketClientManager = new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
            LobbyView lobbyView = socketClientManager.joinGame(nick).lobby();
            var serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            serverJoined.get(500, TimeUnit.MILLISECONDS);

            assertSame(1, serverLobby.joinedPlayers().get().size());
            serverLobby.joinedPlayers().registerObserver(value -> serverPlayerRemoved.complete(null));
            socketClientManager.closeSocket();
            serverPlayerRemoved.get(1500, TimeUnit.MILLISECONDS);
            assertSame(0, serverLobby.joinedPlayers().get().size());
        }
    }

    @Test
    void testSocketDisconnection_clientCloseInGame() throws Exception {
        final var rnd = new Random();

        final AtomicInteger joinedPlayers = new AtomicInteger();
        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverAllJoined = new CompletableFuture<Void>();
        final var serverPlayerDisconnected = new CompletableFuture<Void>();

        final AtomicInteger choosenPort = new AtomicInteger();
        final ServerSocket serverSocket = new ServerSocket(0);
        choosenPort.set(serverSocket.getLocalPort());

        try (var ignored = new SocketConnectionServerController(new ServerController() {
            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lockedLobby.lobby().getUnsafe());
                return lockedLobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      Consumer<Clock> heartbeatHandler,
                                      PlayerObservableTracker observableTracker,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                                      BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory) {
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, lobbyUpdaterFactory,
                        lobbyControllerFactory,
                        gameControllerFactory);
                if (joinedPlayers.incrementAndGet() >= 3)
                    serverAllJoined.complete(null);
                return lobby;
            }
        }, serverSocket)) {

            //Connect 3 client
            var socketClientManager = new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
            LobbyView lobbyView = socketClientManager.joinGame("test_1").lobby();

            var socketClientManager2 = new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
            LobbyView lobbyView2 = socketClientManager2.joinGame("test_2").lobby();

            var socketClientManager3 = new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
            LobbyView lobbyView3 = socketClientManager3.joinGame("test_3").lobby();

            var serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            serverAllJoined.get(500, TimeUnit.MILLISECONDS);

            assertSame(3, serverLobby.joinedPlayers().get().size());

            final var gamePromise = new CompletableFuture<GameAndController<?>>();
            lobbyView2.game().registerObserver(gamePromise::complete);

            //Start game
            //Players should be "ready" before starting a game, but we don't care in this test
            final ServerGame serverGame;
            final LockProtected<ServerGame> lockedServerGame;
            final List<ServerPlayer> players;
            serverLobby.game().set(new ServerGameAndController<>(
                    lockedServerGame = new LockProtected<>(serverGame = new ServerGame(
                            0,
                            new Board(serverLobby.joinedPlayers().get().size()),
                            List.of(),
                            players = serverLobby.joinedPlayers().get().stream()
                                    .map(n -> new ServerPlayer(n.getNick(), new PersonalGoal(new Tile[6][5])))
                                    .collect(Collectors.toList()),
                            rnd.nextInt(players.size()),
                            List.of(new ServerCommonGoal(Type.CROSS), new ServerCommonGoal(Type.ALL_CORNERS)))),
                    new GameServerController(lockedServerGame)));

            final var client2Game = gamePromise.get(500, TimeUnit.MILLISECONDS).game();

            //Set random tiles to serverPlayers
            //No need to check client updates in this test
            for (int i = 0; i < serverGame.getPlayers().size(); i++) {
                final var serverPlayer = serverGame.getPlayers().get(i);
                for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverPlayer.getShelfie().tiles()::iterator)
                    serverPlayer.getShelfie().tile(tile.row(), tile.col())
                            .set(new Tile(Color.values()[rnd.nextInt(Color.values().length)]));
            }

            //Disconnect player test_2
            final var serverPlayer2 = serverGame.getPlayers().stream()
                    .filter(p -> p.getNick().equals("test_2"))
                    .findFirst()
                    .get();

            serverPlayer2.connected().registerObserver(value -> serverPlayerDisconnected.complete(null));
            socketClientManager2.closeSocket();
            serverPlayerDisconnected.get(1500, TimeUnit.MILLISECONDS);
            assertFalse(serverPlayer2.connected().get());

            //Client gets stuck on receive(), kill it
            socketClientManager2.kill();

            //Restart player test_2 creating a new client
            socketClientManager2 = new SocketClientNetManager(
                    new InetSocketAddress(InetAddress.getLocalHost(), choosenPort.get()));
            LobbyView lobbyView2_new = socketClientManager2.joinGame("test_2").lobby();
            final var gamePromise2 = new CompletableFuture<GameAndController<?>>();
            lobbyView2_new.game().registerObserver(gamePromise2::complete);

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
                            serverPlayer.getShelfie().tile(tile.row(), tile.col()),
                            clientPlayer.getShelfie().tile(tile.row(), tile.col()));
            }

            //check that the old game is no longer updated, just to be sure
            assertNotEquals(newClient2Game, client2Game);
        }
    }
}