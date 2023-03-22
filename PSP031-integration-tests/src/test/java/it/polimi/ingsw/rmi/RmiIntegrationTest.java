package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.model.ServerCommonGoal;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerLobby;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RmiIntegrationTest {

    @Test
    void testRmiUpdaters() throws Exception {
        final String remoteName = "rmi_e2e_" + System.currentTimeMillis();
        final String nick = "test_nickname";
        final var rnd = new Random();

        final var serverJoinedNick = new CompletableFuture<String>();

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        final var serverLobbyToSerialize = new CompletableFuture<LobbyView>();

        final var serverGameToSerialize = new CompletableFuture<Game>();

        RmiConnectionServerController.bind(remoteName, new ServerController() {

            @Override
            protected ServerLobby getOrCreateLobby(String nick) {
                final var lobby = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lobby);
                return lobby;
            }

            @Override
            public LobbyView joinGame(String nick,
                                      LobbyUpdaterFactory lobbyUpdaterFactory,
                                      Supplier<GameController> gameControllerFactory) {
                final LobbyUpdaterFactory wrappedFactory = lobby -> new DelegatingLobbyUpdater(
                        lobbyUpdaterFactory.create(lobby)) {
                    @Override
                    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
                        final var gameUpdater = super.updateGame(gameAndController);
                        serverGameToSerialize.complete(gameAndController.game());
                        return gameUpdater;
                    }
                };
                var lobby = super.joinGame(nick, wrappedFactory, gameControllerFactory);
                serverJoinedNick.complete(nick);
                serverLobbyToSerialize.complete(lobby);
                return lobby;
            }
        });

        LobbyView lobbyView = new RmiClientNetManager(remoteName).joinGame(nick);
        assertEquals(
                nick,
                serverJoinedNick.get(500, TimeUnit.MILLISECONDS));
        assertEquals(
                serverLobbyToSerialize.get(500, TimeUnit.MILLISECONDS),
                lobbyView);

        final var serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
        ensurePropertyUpdated(
                "joinedPlayers",
                List.of(nick, "player2", "player3", "player4"),
                serverLobby.joinedPlayers(),
                lobbyView.joinedPlayers());

        final var gamePromise = new CompletableFuture<GameAndController<?>>();
        lobbyView.game().registerObserver(gamePromise::complete);

        final ServerGame serverGame;
        final List<ServerPlayer> players;
        serverLobby.game().set(serverGame = new ServerGame(
                0,
                new Board(serverLobby.joinedPlayers().get().size()),
                List.of(),
                players = serverLobby.joinedPlayers().get().stream()
                        .map(n -> new ServerPlayer(n, new PersonalGoal(new Tile[6][5])))
                        .collect(Collectors.toList()),
                rnd.nextInt(players.size()),
                List.of(new ServerCommonGoal(Type.CROSS), new ServerCommonGoal(Type.ALL_CORNERS))));

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

    private static <T> void ensurePropertyUpdated(String name,
                                                  T value,
                                                  Property<T> serverProperty,
                                                  Provider<T> clientProvider)
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

    private static <S, C> void ensurePropertyUpdated(String name,
                                                     S serverValue,
                                                     C clientValue,
                                                     Property<S> serverProperty,
                                                     Provider<? extends C> clientProvider)
            throws ExecutionException, InterruptedException {

        final CompletableFuture<C> received = new CompletableFuture<>();
        final Consumer<C> observer;
        clientProvider.registerObserver(observer = received::complete);

        serverProperty.set(serverValue);
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
