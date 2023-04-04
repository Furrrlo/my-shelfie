package it.polimi.ingsw.updater;

import it.polimi.ingsw.DelegatingLobbyUpdater;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.*;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.time.Clock;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdatersIntegrationTest {

    public static void doTestUpdaters(Function<ServerController, Closeable> bindServerController,
                                      Supplier<ClientNetManager> clientNetManagerFactory)
            throws Exception {
        final String nick = "test_nickname";
        final var rnd = new Random();

        final var serverJoinedNick = new CompletableFuture<String>();

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverLobbyToSerialize = new CompletableFuture<LobbyView>();

        final var serverGameToSerialize = new CompletableFuture<Game>();

        try (Closeable ignored = bindServerController.apply(new ServerController() {

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
                final LobbyUpdaterFactory wrappedFactory = lobby -> new DelegatingLobbyUpdater(
                        lobbyUpdaterFactory.create(lobby)) {
                    @Override
                    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
                        final var gameUpdater = super.updateGame(gameAndController);
                        serverGameToSerialize.complete(gameAndController.game());
                        return gameUpdater;
                    }
                };
                var lobby = super.joinGame(nick, heartbeatHandler, observableTracker, wrappedFactory, lobbyControllerFactory,
                        gameControllerFactory);
                serverJoinedNick.complete(nick);
                serverLobbyToSerialize.complete(lobby);
                return lobby;
            }
        })) {

            LobbyView lobbyView = clientNetManagerFactory.get().joinGame(nick).lobby();
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

            final var serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            ensurePropertyUpdated(
                    "joinedPlayers",
                    List.of(serverLobby.joinedPlayers().get().get(0), // Our own player should already be in there 
                            new LobbyPlayer("player2"), new LobbyPlayer("player3"), new LobbyPlayer("player4")),
                    serverLobby.joinedPlayers(),
                    lobbyView.joinedPlayers());

            for (int i = 0; i < serverLobby.joinedPlayers().get().size(); i++) {
                final var serverLobbyPlayer = serverLobby.joinedPlayers().get().get(i);
                final var clientLobbyPlayer = lobbyView.joinedPlayers().get().get(i);
                ensurePropertyUpdated(
                        serverLobbyPlayer.getNick() + ".ready",
                        true,
                        serverLobbyPlayer.ready(),
                        clientLobbyPlayer.ready());
            }

            final var gamePromise = new CompletableFuture<GameAndController<?>>();
            lobbyView.game().registerObserver(gamePromise::complete);

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

            final var clientGame = gamePromise.get().game();
            assertEquals(
                    serverGameToSerialize.get(500, TimeUnit.MILLISECONDS),
                    clientGame);

            ensurePropertyUpdated(
                    "currentTurn",
                    serverGame.getPlayers().get(0),
                    clientGame.getPlayers().get(0),
                    serverGame.currentTurn(),
                    clientGame.currentTurn());
            ensurePropertyUpdated(
                    "firstFinisher",
                    serverGame.getPlayers().get(0),
                    clientGame.getPlayers().get(0),
                    serverGame.firstFinisher(),
                    clientGame.firstFinisher());

            for (int i = 0; i < serverGame.getPlayers().size(); i++) {
                final var serverPlayer = serverGame.getPlayers().get(i);
                final var clientPlayer = clientGame.getPlayers().get(i);

                for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverPlayer.getShelfie().tiles()::iterator)
                    ensurePropertyUpdated(
                            serverPlayer.getNick() + "ShelfTile" + tile.row() + "x" + tile.col(),
                            new Tile(Color.values()[rnd.nextInt(Color.values().length)]),
                            serverPlayer.getShelfie().tile(tile.row(), tile.col()),
                            clientPlayer.getShelfie().tile(tile.row(), tile.col()));
            }

            for (var tile : (Iterable<TileAndCoords<Property<@Nullable Tile>>>) serverGame.getBoard().tiles()::iterator)
                ensurePropertyUpdated(
                        "board" + tile.row() + "x" + tile.col(),
                        new Tile(Color.values()[rnd.nextInt(Color.values().length)]),
                        serverGame.getBoard().tile(tile.row(), tile.col()),
                        clientGame.getBoard().tile(tile.row(), tile.col()));

            for (int i = 0; i < serverGame.getCommonGoals().size(); i++) {
                final var serverCommonGoal = serverGame.getCommonGoals().get(i);
                final var clientCommonGoal = clientGame.getCommonGoals().get(i);

                ensurePropertyUpdated(
                        "commonGoal",
                        List.of(serverGame.getPlayers().get(0)),
                        List.of(clientGame.getPlayers().get(0)),
                        serverCommonGoal.achieved(),
                        clientCommonGoal.achieved());
            }
        }
    }

    private static <T> void ensurePropertyUpdated(String name,
                                                  T value,
                                                  Property<? extends T> serverProperty,
                                                  Provider<? extends T> clientProvider)
            throws ExecutionException, InterruptedException {
        assertEquals(
                serverProperty.get(),
                clientProvider.get(),
                "Starting value of property " + name);

        ensurePropertyUpdated(name, value, value, serverProperty, clientProvider);

        assertEquals(
                serverProperty.get(),
                clientProvider.get(),
                "Final value of property " + name);
    }

    @SuppressWarnings("unchecked")
    private static <S, C> void ensurePropertyUpdated(String name,
                                                     S serverValue,
                                                     C clientValue,
                                                     Property<? extends S> serverProperty,
                                                     Provider<? extends C> clientProvider)
            throws ExecutionException, InterruptedException {

        final CompletableFuture<C> received = new CompletableFuture<>();
        final Consumer<C> observer;
        clientProvider.registerObserver(observer = received::complete);

        ((Property<S>) serverProperty).set(serverValue);
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
