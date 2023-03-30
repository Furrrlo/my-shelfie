package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerLobby;
import it.polimi.ingsw.server.model.ServerLobbyAndController;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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
    protected @Nullable ServerLobbyAndController<ServerLobby> getLobbyFor(String nick) {
        // TODO: like search a game
        return null;
    }

    @VisibleForTesting
    protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
        // TODO: like pick game or create one if needed
        var lockedLobby = new LockProtected<>(new ServerLobby(4));
        return new ServerLobbyAndController<>(lockedLobby, new LobbyServerController(lockedLobby));
    }

    public LobbyView joinGame(String nick,
                              HeartbeatHandler heartbeatHandler,
                              LobbyUpdaterFactory lobbyUpdaterFactory,
                              Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                              BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory) {
        heartbeats.put(nick, heartbeatHandler);

        do {
            final var serverLobbyAndController = getOrCreateLobby(nick);
            final var lockedServerLobby = serverLobbyAndController.lobby();

            try (var serverLobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = serverLobbyCloseable.obj();

                // This is basically double-checked locking, getOrCreateGameLobbySomehow() checks with no lock
                // so that it can discard options fast, then here we re-check while actually holding the lock
                // to guarantee concurrency
                final List<String> currentPlayers = serverLobby.joinedPlayers().get().stream()
                        .map(LobbyPlayer::getNick)
                        .toList();
                if (!currentPlayers.contains(nick) && currentPlayers.size() >= serverLobby.getRequiredPlayers())
                    continue;

                final Lobby lobby = new Lobby(serverLobby.getRequiredPlayers(), serverLobby.joinedPlayers().get());

                final LobbyUpdater lobbyUpdater;
                try {
                    lobbyUpdater = lobbyUpdaterFactory.create(new LobbyAndController<>(lobby,
                            lobbyControllerFactory.apply(serverLobbyAndController.controller())));
                } catch (DisconnectedException e) {
                    throw new IllegalStateException("Player disconnected during handshake process");
                }

                final var currGameAndController = serverLobby.game().get();
                final var currGameLocked = currGameAndController != null ? currGameAndController.game() : null;
                try (var currGameCloseable = LockProtected.useNullable(currGameLocked)) {
                    var currGame = currGameCloseable.obj();

                    // Doesn't need to be concurrent as it will only be called inside the lobby lock
                    final var playersRegisteredObservers = new HashMap<LobbyPlayer, Consumer<?>>();
                    for (LobbyPlayer player : serverLobby.joinedPlayers().get())
                        playersRegisteredObservers.put(
                                player,
                                registerObserverFor(nick, player.ready(),
                                        ready -> lobbyUpdater.updatePlayerReady(player.getNick(), ready)));

                    registerObserverFor(nick, serverLobby.joinedPlayers(), newLobbyPlayers -> {
                        lobbyUpdater.updateJoinedPlayers(newLobbyPlayers.stream()
                                .map(LobbyPlayer::getNick)
                                .collect(Collectors.toList()));
                        // Add observers to players which joined
                        for (LobbyPlayer p0 : newLobbyPlayers)
                            playersRegisteredObservers.computeIfAbsent(p0, p -> registerObserverFor(nick, p.ready(),
                                    ready -> lobbyUpdater.updatePlayerReady(p.getNick(), ready)));
                        // Remove observers from players which left
                        playersRegisteredObservers.entrySet().removeIf(e -> !newLobbyPlayers.contains(e.getKey()));
                    });
                    registerObserverFor(nick, serverLobby.game(), game -> {
                        if (game != null) {
                            try (var gameCloseable = game.game().use()) {
                                updateGameForPlayer(
                                        nick,
                                        gameCloseable.obj(),
                                        game.controller(),
                                        lobbyUpdater,
                                        gameControllerFactory);
                            }
                        }
                    });

                    // Add the player after registering the listeners, 
                    // so the joining player will also receive it
                    if (!currentPlayers.contains(nick)) {
                        serverLobby.joinedPlayers().update(l -> {
                            final var newList = new ArrayList<>(l);
                            newList.add(new LobbyPlayer(nick, false));
                            return newList;
                        });
                    }

                    if (currGameAndController != null)
                        updateGameForPlayer(
                                nick,
                                Objects.requireNonNull(currGame, "Controller is not null but game is null?"),
                                currGameAndController.controller(),
                                lobbyUpdater,
                                gameControllerFactory);
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
                                     GameServerController gameController,
                                     LobbyUpdater lobbyUpdater,
                                     BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
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
                gameControllerFactory.apply(thePlayer, gameController)));
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

    public void disconnectPlayer(String nick, Throwable cause) {
        doDisconnectPlayer(nick, null, cause);
    }

    public void disconnectPlayer(ServerPlayer player, Throwable cause) {
        doDisconnectPlayer(player.getNick(), player, cause);
    }

    private void doDisconnectPlayer(String nick, @Nullable ServerPlayer player, Throwable cause) {
        heartbeats.remove(nick);

        final var lobbyAndController = getLobbyFor(nick);
        final var lockedLobby = lobbyAndController != null ? lobbyAndController.lobby() : null;
        try (var lobbyCloseable = LockProtected.useNullable(lockedLobby)) {
            var lobby = lobbyCloseable.obj();
            var gameAndController = lobby != null ? lobby.game().get() : null;
            var lockedGame = gameAndController != null ? gameAndController.game() : null;
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

    private <T> Consumer<T> registerObserverFor(ServerPlayer player, Provider<T> toObserve, ThrowingConsumer<T> observer) {
        Consumer<T> throwingObserver;
        observableTracker.registerObserverFor(player.getNick(), toObserve, throwingObserver = t -> {
            try {
                observer.accept(t);
            } catch (DisconnectedException e) {
                disconnectPlayer(player, e);
            }
        });
        return throwingObserver;
    }

    private <T> Consumer<T> registerObserverFor(String nick, Provider<T> toObserve, ThrowingConsumer<T> observer) {
        Consumer<T> throwingObserver;
        observableTracker.registerObserverFor(nick, toObserve, throwingObserver = t -> {
            try {
                observer.accept(t);
            } catch (DisconnectedException e) {
                disconnectPlayer(nick, e);
            }
        });
        return throwingObserver;
    }

    private interface ThrowingConsumer<T> {

        void accept(T t) throws DisconnectedException;
    }
}
