package it.polimi.ingsw.updater;

import it.polimi.ingsw.CloseablesTracker;
import it.polimi.ingsw.DelegatingLobbyUpdater;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdatersIntegrationTest {

    public static void doTestUpdaters(Function<ServerController, Closeable> bindServerController,
                                      Function<String, ClientNetManager> clientNetManagerFactory)
            throws Exception {
        final String nick = "test_nickname";
        final var rnd = new Random();

        final var serverJoinedNick = new CompletableFuture<String>();

        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        final var serverLobbyToSerialize = new CompletableFuture<LobbyView>();

        final var serverGameToSerialize = new CompletableFuture<Game>();

        try (var serverController = new ServerController(500, TimeUnit.MILLISECONDS) {

            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lockedLobby = super.getOrCreateLobby(nick);
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
                final LobbyUpdaterFactory wrappedFactory = lobby -> new DelegatingLobbyUpdater(
                        lobbyUpdaterFactory.create(lobby)) {
                    @Override
                    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
                        final var gameUpdater = super.updateGame(gameAndController);
                        serverGameToSerialize.complete(gameAndController.game());
                        return gameUpdater;
                    }
                };
                LobbyView lobby = super.joinGame(nick, observableTracker, onGameOver, wrappedFactory, lobbyControllerFactory,
                        gameControllerFactory);
                serverJoinedNick.complete(nick);
                serverLobbyToSerialize.complete(lobby);
                return lobby;
            }
        };
             Closeable ignored = bindServerController.apply(serverController);
             var closeables = new CloseablesTracker()) {

            LobbyView lobbyView = closeables.register(clientNetManagerFactory.apply(nick)).joinGame().lobby();
            assertEquals(
                    nick,
                    serverJoinedNick.get(500, TimeUnit.MILLISECONDS));
            // The serialized lobby does not contain the player that just joined
            // (it gets added by an update), while the deserialized lobby is
            // already updated before being returned by the ClientNetManager,
            // so we can't check it
            // assertEquals(
            //      serverLobbyToSerialize.get(500, TimeUnit.MILLISECONDS),
            //      lobbyView);

            final var lockedServerLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            final var serverLobby = lockedServerLobby.getUnsafe();
            ensurePropertyUpdated(
                    "joinedPlayers",
                    List.of(serverLobby.joinedPlayers().get().get(0), // Our own player should already be in there 
                            new LobbyPlayer("player2"), new LobbyPlayer("player3"), new LobbyPlayer("player4")),
                    serverController,
                    serverLobby.joinedPlayers(),
                    lobbyView.joinedPlayers());

            for (int i = 0; i < serverLobby.joinedPlayers().get().size(); i++) {
                final var serverLobbyPlayer = serverLobby.joinedPlayers().get().get(i);
                final var clientLobbyPlayer = lobbyView.joinedPlayers().get().get(i);
                ensurePropertyUpdated(
                        serverLobbyPlayer.getNick() + ".ready",
                        true,
                        serverController,
                        serverLobbyPlayer.ready(),
                        clientLobbyPlayer.ready());
            }

            final var gamePromise = new CompletableFuture<GameAndController<?>>();
            lobbyView.game().registerObserver(gamePromise::complete);

            final ServerGame serverGame;
            try (var ignored1 = lockedServerLobby.use()) {
                serverLobby.game().set(new ServerGameAndController<>(
                        serverGame = LobbyServerController.createGame(0, serverLobby.joinedPlayers().get()),
                        new GameServerController(new LockProtected<>(serverGame, lockedServerLobby.getLock()))));
            }

            final var clientGame = gamePromise.get(500, TimeUnit.MILLISECONDS).game();
            assertEquals(
                    serverGameToSerialize.get(500, TimeUnit.MILLISECONDS),
                    clientGame);

            ensurePropertyUpdated(
                    "currentTurn",
                    serverGame.getPlayers().get(0),
                    clientGame.getPlayers().get(0),
                    serverController,
                    serverGame.currentTurn(),
                    clientGame.currentTurn());
            ensurePropertyUpdated(
                    "firstFinisher",
                    serverGame.getPlayers().get(0),
                    clientGame.getPlayers().get(0),
                    serverController,
                    serverGame.firstFinisher(),
                    clientGame.firstFinisher());

            for (int i = 0; i < serverGame.getPlayers().size(); i++) {
                final var serverPlayer = serverGame.getPlayers().get(i);
                final var clientPlayer = clientGame.getPlayers().get(i);

                ensurePropertyUpdated(
                        serverPlayer.getNick() + ".connected",
                        true,
                        serverController,
                        serverPlayer.connected(),
                        clientPlayer.connected());
                ensurePropertyUpdated(
                        serverPlayer.getNick() + ".isCurrentTurn",
                        serverPlayer,
                        true,
                        serverController,
                        serverGame.currentTurn(),
                        clientPlayer.isCurrentTurn());
                ensurePropertyUpdated(
                        serverPlayer.getNick() + ".isFirstFinisher",
                        serverPlayer,
                        true,
                        serverController,
                        serverGame.firstFinisher(),
                        clientPlayer.isFirstFinisher());
                // Force a score update by resetting and then setting the player as firstFinisher
                serverController.runOnOnlyLobbyLocks(() -> Property.setNullable(serverGame.firstFinisher(), null));
                ensurePropertyUpdated(
                        serverPlayer.getNick() + ".score",
                        serverPlayer,
                        clientPlayer.score().get() + 1,
                        serverController,
                        serverGame.firstFinisher(),
                        clientPlayer.score());

                for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverPlayer.getShelfie().tiles()::iterator)
                    ensurePropertyUpdated(
                            serverPlayer.getNick() + "ShelfTile" + tile.row() + "x" + tile.col(),
                            new Tile(Color.values()[rnd.nextInt(Color.values().length)]),
                            serverController,
                            serverPlayer.getShelfie().tile(tile.row(), tile.col()),
                            clientPlayer.getShelfie().tile(tile.row(), tile.col()));
            }

            for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverGame.getBoard().tiles()::iterator)
                ensurePropertyUpdated(
                        "board" + tile.row() + "x" + tile.col(),
                        new Tile(Color.values()[rnd.nextInt(Color.values().length)]),
                        serverController,
                        serverGame.getBoard().tile(tile.row(), tile.col()),
                        clientGame.getBoard().tile(tile.row(), tile.col()));

            for (int i = 0; i < serverGame.getCommonGoals().size(); i++) {
                final var serverCommonGoal = serverGame.getCommonGoals().get(i);
                final var clientCommonGoal = clientGame.getCommonGoals().get(i);

                ensurePropertyUpdated(
                        "commonGoal",
                        List.of(serverGame.getPlayers().get(0)),
                        List.of(clientGame.getPlayers().get(0)),
                        serverController,
                        serverCommonGoal.achieved(),
                        clientCommonGoal.achieved());
            }

            ensurePropertyUpdated(
                    "message",
                    UserMessage.forEveryone(nick, "", "test message"),
                    List.of(UserMessage.forEveryone(nick, "", "test message")),
                    serverController,
                    serverGame.message(),
                    clientGame.messageList());

            for (int i = 0; i < GameClientUpdater.MAX_MESSAGES_HISTORY + 10; i++)
                serverController.runOnOnlyLobbyLocks(() -> serverGame.message()
                        .set(UserMessage.forEveryone(nick, "", "test message")));
            ensurePropertyUpdated(
                    "maxMessages",
                    UserMessage.forEveryone(nick, "", "test message"),
                    IntStream.range(0, GameClientUpdater.MAX_MESSAGES_HISTORY)
                            .mapToObj(i -> UserMessage.forEveryone(nick, "", "test message"))
                            .toList(),
                    serverController,
                    serverGame.message(),
                    clientGame.messageList());

            ensurePropertyUpdated(
                    "suspended",
                    true,
                    serverController,
                    serverGame.suspended(),
                    clientGame.suspended());
            ensurePropertyUpdated(
                    "endGame",
                    true,
                    serverController,
                    serverGame.endGame(),
                    clientGame.endGame());
        }
    }

    public static <T> void ensurePropertyUpdated(String name,
                                                 T value,
                                                 ServerController serverController,
                                                 Property<? extends T> serverProperty,
                                                 Provider<? extends T> clientProvider)
            throws ExecutionException, InterruptedException {
        assertEquals(
                serverProperty.get(),
                clientProvider.get(),
                "Starting value of property " + name);

        ensurePropertyUpdated(name, value, value, serverController, serverProperty, clientProvider);

        assertEquals(
                serverProperty.get(),
                clientProvider.get(),
                "Final value of property " + name);
    }

    @SuppressWarnings("unchecked")
    public static <S, C> void ensurePropertyUpdated(String name,
                                                    S serverValue,
                                                    C clientValue,
                                                    ServerController serverController,
                                                    Property<? extends S> serverProperty,
                                                    Provider<? extends C> clientProvider)
            throws ExecutionException, InterruptedException {

        final CompletableFuture<C> received = new CompletableFuture<>();
        final Consumer<C> observer;
        clientProvider.registerObserver(observer = received::complete);

        serverController.runOnOnlyLobbyLocks(() -> ((Property<S>) serverProperty).set(serverValue));
        assertEquals(
                serverValue,
                serverProperty.get(),
                "Server set value of " + name);

        try {
            assertEquals(
                    clientValue,
                    received.get(500, TimeUnit.MILLISECONDS),
                    "Received value of property " + name);
        } catch (TimeoutException ex) {
            throw new AssertionError("Failed to wait for update of property " + name, ex);
        } finally {
            clientProvider.unregisterObserver(observer);
        }
    }
}
