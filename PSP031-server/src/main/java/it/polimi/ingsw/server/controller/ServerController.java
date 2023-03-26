package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerLobby;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerController {

    private final Clock clock;
    private final ScheduledFuture<?> heartbeatTask;

    private final ConcurrentMap<String, HeartbeatHandler> heartbeats = new ConcurrentHashMap<>();
    private final PlayerObservableTracker observableTracker = new PlayerObservableTracker();

    public ServerController() {
        this(Clock.systemUTC(), Executors.newSingleThreadScheduledExecutor(r -> {
            var t = new Thread(r);
            t.setName("ServerController-heartbeat-thread");
            return t;
        }));
    }

    public ServerController(Clock clock, ScheduledExecutorService scheduledExecutorService) {
        this.clock = clock;
        this.heartbeatTask = scheduledExecutorService.scheduleAtFixedRate(
                this::detectDisconnectedPlayers,
                0,
                1000,
                TimeUnit.MILLISECONDS);
    }

    private void detectDisconnectedPlayers() {
        heartbeats.forEach((nick, heartbeatHandler) -> {
            try {
                heartbeatHandler.sendHeartbeat(Instant.now(clock));
            } catch (DisconnectedException ex) {
                disconnectPlayer(nick, ex);
            }
        });
    }

    @VisibleForTesting
    protected @Nullable LockProtected<ServerLobby> getLobbyFor(String nick) {
        // TODO: like search a game
        return null;
    }

    @VisibleForTesting
    protected LockProtected<ServerLobby> getOrCreateLobby(String nick) {
        // TODO: like pick game or create one if needed
        return new LockProtected<>(new ServerLobby(4));
    }

    public LobbyView joinGame(String nick,
                              HeartbeatHandler heartbeatHandler,
                              LobbyUpdaterFactory lobbyUpdaterFactory,
                              Supplier<GameController> gameControllerFactory) {
        heartbeats.put(nick, heartbeatHandler);

        do {
            final LockProtected<ServerLobby> lockedServerLobby = getOrCreateLobby(nick);

            try (var serverLobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = serverLobbyCloseable.obj();

                // This is basically double-checked locking, getOrCreateGameLobbySomehow() checks with no lock
                // so that it can discard options fast, then here we re-check while actually holding the lock
                // to guarantee concurrency
                final List<String> currentPlayers = serverLobby.joinedPlayers().get();
                if (!currentPlayers.contains(nick) && currentPlayers.size() >= serverLobby.getRequiredPlayers())
                    continue;

                if (!currentPlayers.contains(nick)) {
                    serverLobby.joinedPlayers().update(l -> {
                        final var newList = new ArrayList<>(l);
                        newList.add(nick);
                        return newList;
                    });
                }

                final Lobby lobby = new Lobby(serverLobby.getRequiredPlayers(), serverLobby.joinedPlayers().get());

                final LobbyUpdater lobbyUpdater;
                try {
                    lobbyUpdater = lobbyUpdaterFactory.create(lobby);
                } catch (DisconnectedException e) {
                    throw new IllegalStateException("Player disconnected during handshake process");
                }

                final var currGameLocked = serverLobby.game().get();
                try (var currGameCloseable = LockProtected.useNullable(currGameLocked)) {
                    var currGame = currGameCloseable.obj();

                    registerObserverFor(nick, serverLobby.joinedPlayers(), lobbyUpdater::updateJoinedPlayers);
                    registerObserverFor(nick, serverLobby.game(), game -> {
                        if (game != null) {
                            try (var gameCloseable = game.use()) {
                                updateGameForPlayer(nick, gameCloseable.obj(), lobbyUpdater, gameControllerFactory);
                            }
                        }
                    });

                    if (currGame != null)
                        updateGameForPlayer(nick, currGame, lobbyUpdater, gameControllerFactory);
                } catch (DisconnectedException ex) {
                    disconnectPlayer(nick, ex);
                    throw new IllegalStateException("Player disconnected during handshake process");
                }

                return lobby;
            }
        } while (true);
    }

    private void updateGameForPlayer(String nick,
                                     ServerGame game,
                                     LobbyUpdater lobbyUpdater,
                                     Supplier<GameController> gameControllerFactory)
            throws DisconnectedException {

        final Map<String, Player> clientPlayers = game.getPlayers().stream()
                .collect(Collectors.toMap(
                        ServerPlayer::getNick,
                        p -> new Player(p.getNick(), p.getShelfie())));
        final ServerPlayer thePlayer = game.getPlayers().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("" +
                        "Missing player " + nick + " which is supposed to be ingame " +
                        "(found players: " + game.getPlayers() + ")"));

        final GameUpdater gameUpdater = lobbyUpdater.updateGame(new GameAndController<>(
                new Game(
                        game.getGameID(),
                        game.getBoard(),
                        // Use another stream 'cause we need to keep order
                        game.getPlayers().stream()
                                .map(p -> clientPlayers.get(p.getNick()))
                                .collect(Collectors.toList()),
                        game.getPlayers().indexOf(game.currentTurn().get()),
                        game.getCommonGoals().stream()
                                .map(goal -> new CommonGoal(
                                        goal.getType(),
                                        goal.achieved().get().stream()
                                                .map(p -> clientPlayers.get(p.getNick()))
                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList()),
                        thePlayer.getPersonalGoal(),
                        game.firstFinisher().get() == null ? null : game.getPlayers().indexOf(game.firstFinisher().get())),
                gameControllerFactory.get()));
        // Register all listeners to the game model
        game.getBoard().tiles().forEach(tileAndCoords -> registerObserverFor(thePlayer, tileAndCoords.tile(),
                tile -> gameUpdater.updateBoardTile(tileAndCoords.row(), tileAndCoords.col(), tile)));
        game.getPlayers().forEach(p -> p.getShelfie().tiles()
                .forEach(tileAndCoords -> registerObserverFor(thePlayer, tileAndCoords.tile(),
                        tile -> gameUpdater.updatePlayerShelfieTile(
                                p.getNick(),
                                tileAndCoords.row(),
                                tileAndCoords.col(),
                                tile))));
        registerObserverFor(thePlayer, game.currentTurn(), p -> gameUpdater.updateCurrentTurn(p.getNick()));
        registerObserverFor(thePlayer, game.firstFinisher(), p -> gameUpdater.updateFirstFinisher(p.getNick()));
        game.getCommonGoals().forEach(goal -> registerObserverFor(thePlayer,
                goal.achieved(),
                players -> gameUpdater.updateAchievedCommonGoal(goal.getType(), players.stream()
                        .map(ServerPlayer::getNick)
                        .collect(Collectors.toList()))));
    }

    private void disconnectPlayer(String nick, Throwable cause) {
        disconnectPlayer(nick, null, cause);
    }

    private void disconnectPlayer(String nick, @Nullable ServerPlayer player, Throwable cause) {
        heartbeats.remove(nick);

        try (var lobbyCloseable = LockProtected.useNullable(getLobbyFor(nick))) {
            var lobby = lobbyCloseable.obj();
            var lockedGame = lobby != null ? lobby.game().get() : null;
            try (var gameCloseable = LockProtected.useNullable(lockedGame)) {
                var game = gameCloseable.obj();
                if (game != null && player == null)
                    player = game.getPlayers().stream()
                            .filter(p -> p.getNick().equals(nick))
                            .findFirst()
                            .orElse(null);

                if (player != null) {
                    // TODO: set player as disconnected
                }

                observableTracker.unregisterObserversFor(nick);
            }
        }
    }

    private <T> void registerObserverFor(ServerPlayer player, Provider<T> toObserve, ThrowingConsumer<T> observer) {
        registerObserverFor(player.getNick(), player, toObserve, observer);
    }

    private <T> void registerObserverFor(String nick, Provider<T> toObserve, ThrowingConsumer<T> observer) {
        observableTracker.registerObserverFor(nick, toObserve, t -> {
            try {
                observer.accept(t);
            } catch (DisconnectedException e) {
                disconnectPlayer(nick, e);
            }
        });
    }

    private <T> void registerObserverFor(String nick,
                                         @Nullable ServerPlayer player,
                                         Provider<T> toObserve,
                                         ThrowingConsumer<T> observer) {
        observableTracker.registerObserverFor(nick, toObserve, t -> {
            try {
                observer.accept(t);
            } catch (DisconnectedException e) {
                disconnectPlayer(nick, player, e);
            }
        });
    }

    private interface ThrowingConsumer<T> {

        void accept(T t) throws DisconnectedException;
    }
}
