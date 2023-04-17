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

import java.io.Closeable;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerController implements Closeable {

    private final Clock clock;
    private final ScheduledFuture<?> heartbeatTask;
    private final ExecutorService heartbeatThreadPool;

    private final ConcurrentMap<String, HeartbeatHandler> heartbeats = new ConcurrentHashMap<>();

    private final Lock lobbiesLock = new ReentrantLock();
    private final Set<ServerLobbyAndController<ServerLobby>> lobbies = ConcurrentHashMap.newKeySet();

    public ServerController(long pingInterval, TimeUnit pingIntervalUnit) {
        this(Clock.systemUTC(), pingInterval, pingIntervalUnit);
    }

    @SuppressWarnings("resource")
    public ServerController(Clock clock,
                            long pingInterval,
                            TimeUnit pingIntervalUnit) {
        this.clock = clock;
        this.heartbeatThreadPool = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                .name("ServerController-heartbeat-thread-", 0)
                .factory());
        this.heartbeatTask = Executors.newSingleThreadScheduledExecutor(r -> {
            var t = new Thread(r);
            t.setName("ServerController-heartbeat-scheduler-thread");
            return t;
        }).scheduleAtFixedRate(this::detectDisconnectedPlayers, 0, pingInterval, pingIntervalUnit);
    }

    @Override
    public void close() {
        heartbeatTask.cancel(true);
        heartbeatThreadPool.shutdown();
    }

    @SuppressWarnings("FutureReturnValueIgnored") // We don't need to keep track of it as we shut down everything together
    private void detectDisconnectedPlayers() {
        heartbeats.forEach(
                (nick, heartbeatHandler) -> heartbeatThreadPool
                        .submit(() -> heartbeatHandler.sendHeartbeat(Instant.now(clock))));
    }

    @VisibleForTesting
    protected @Nullable ServerLobbyAndController<ServerLobby> getLobbyFor(String nick) {
        // TODO: like search a game
        for (var lobby : lobbies) {
            for (LobbyPlayer p : lobby.lobby().getUnsafe().joinedPlayers().get())
                if (p.getNick().equals(nick))
                    return lobby;
        }
        return null;
    }

    /**
     * @implNote to be as fast as possible, this method does not hold any of the lobby-specific locks
     *           while checking if the given player can join it.
     *           It is up to the caller to re-check inside a lock that the ServerLobby#canOnePlayerJoin()
     *           condition still holds true.
     */
    @VisibleForTesting
    protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
        // See if we can find a lobby where the player was already in
        // Since we only keep players in a lobby after they left when a game has started,
        // we can avoid holding any lock, as the #joinedPlayers() list becomes immutable de-facto
        var lobby = getLobbyFor(nick);
        if (lobby != null)
            return lobby;
        // Fast case: try seeing if there's a lobby already being created while not holding the lobbiesLock
        // Note:
        // 1. We are not holding any of the lobbies lock, so after returning this, the caller
        //    needs to make sure that the canOnePlayerJoin() is still true while on the lobby lock
        // 2. Since we don't hold the lobbies lock yet, we are not allowed to create and add a new lobby
        //    to fill in case we find none available.
        lobby = lobbies.stream()
                .filter(l -> l.lobby().getUnsafe().canOnePlayerJoin())
                .findFirst()
                .orElse(null);
        if (lobby != null)
            return lobby;
        // Double-checked locking: while inside the lock let's recheck if we can find any valid lobby.
        // If we can't, we are allowed to create a new one, as we are in the lock and nobody else can do
        // so concurrently to us
        lobbiesLock.lock();
        try {
            return lobbies.stream()
                    .filter(l -> l.lobby().getUnsafe().canOnePlayerJoin())
                    .findFirst()
                    .orElseGet(() -> {
                        var lockedLobby = new LockProtected<>(new ServerLobby(4));
                        ServerLobbyAndController<ServerLobby> newLobby = new ServerLobbyAndController<>(lockedLobby,
                                new LobbyServerController(lockedLobby));
                        lobbies.add(newLobby);
                        return newLobby;
                    });
        } finally {
            lobbiesLock.unlock();
        }
    }

    public void runOnLocks(String nick, Runnable runnable) {
        final var lobbyAndController = getLobbyFor(nick);
        if (lobbyAndController == null) {
            runnable.run();
            return;
        }

        try (var ignored = lobbyAndController.lobby().use()) {
            runnable.run();
        }
    }

    @VisibleForTesting
    public void runOnOnlyLobbyLocks(Runnable runnable) {
        if (lobbies.size() != 1)
            throw new AssertionError("This method is supposed to be used for testing when there's only 1 lobby");

        var lobbyAndController = lobbies.stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("This method is supposed to be used for testing " +
                        "when there's only 1 lobby"));
        if (lobbyAndController == null) {
            runnable.run();
            return;
        }

        try (var ignored = lobbyAndController.lobby().use()) {
            runnable.run();
        }
    }

    public <T> T supplyOnLocks(String nick, Supplier<T> callable) {
        final var lobbyAndController = getLobbyFor(nick);
        if (lobbyAndController == null)
            return callable.get();

        try (var ignored = lobbyAndController.lobby().use()) {
            return callable.get();
        }
    }

    public LobbyView joinGame(String nick,
                              HeartbeatHandler heartbeatHandler,
                              PlayerObservableTracker observableTracker,
                              LobbyUpdaterFactory lobbyUpdaterFactory,
                              Function<LobbyServerController, LobbyController> lobbyControllerFactory,
                              BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
            throws DisconnectedException {
        heartbeats.put(nick, heartbeatHandler);

        do {
            final var serverLobbyAndController = getOrCreateLobby(nick);
            final var lockedServerLobby = serverLobbyAndController.lobby();

            try (var serverLobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = serverLobbyCloseable.obj();

                // This is basically double-checked locking, getOrCreateGameLobbySomehow() checks with no lock
                // so that it can discard options fast, then here we re-check while actually holding the lock
                // to guarantee concurrency
                final List<String> joinedPlayersNicks = serverLobby.joinedPlayers().get().stream()
                        .map(LobbyPlayer::getNick)
                        .toList();
                if (!joinedPlayersNicks.contains(nick) && !serverLobby.canOnePlayerJoin())
                    continue; // If we fail the test, let's just retry and search a new one

                final Lobby lobby = new Lobby(serverLobby.getRequiredPlayers(), serverLobby.joinedPlayers().get());

                final LobbyUpdater lobbyUpdater;
                try {
                    lobbyUpdater = lobbyUpdaterFactory.create(new LobbyAndController<>(lobby,
                            lobbyControllerFactory.apply(serverLobbyAndController.controller())));
                } catch (DisconnectedException e) {
                    throw new IllegalStateException("Player disconnected during handshake process");
                }

                final var currGameAndController = serverLobby.game().get();
                final var currGame = currGameAndController != null ? currGameAndController.game() : null;
                try {
                    // Doesn't need to be concurrent as it will only be called inside the lobby lock
                    final var playersRegisteredObservers = new HashMap<LobbyPlayer, Consumer<?>>();
                    for (LobbyPlayer player : serverLobby.joinedPlayers().get())
                        playersRegisteredObservers.put(
                                player,
                                observableTracker.registerObserver(player.ready(),
                                        ready -> lobbyUpdater.updatePlayerReady(player.getNick(), ready)));

                    observableTracker.registerObserver(serverLobby.joinedPlayers(), newLobbyPlayers -> {
                        lobbyUpdater.updateJoinedPlayers(newLobbyPlayers.stream()
                                .map(LobbyPlayer::getNick)
                                .collect(Collectors.toList()));
                        // Add observers to players which joined
                        for (LobbyPlayer p0 : newLobbyPlayers)
                            playersRegisteredObservers.computeIfAbsent(p0, p -> observableTracker.registerObserver(p.ready(),
                                    ready -> lobbyUpdater.updatePlayerReady(p.getNick(), ready)));
                        // Remove observers from players which left
                        // TODO: unregister observers
                        playersRegisteredObservers.entrySet().removeIf(e -> !newLobbyPlayers.contains(e.getKey()));
                    });
                    observableTracker.registerObserver(serverLobby.game(), game -> {
                        if (game != null)
                            updateGameForPlayer(
                                    nick,
                                    game.game(),
                                    game.controller(),
                                    observableTracker,
                                    lobbyUpdater,
                                    gameControllerFactory);
                    });

                    // Add the player after registering the listeners, 
                    // so the joining player will also receive it
                    if (!joinedPlayersNicks.contains(nick)) {
                        serverLobby.joinedPlayers().update(l -> {
                            final var newList = new ArrayList<>(l);
                            newList.add(new LobbyPlayer(nick, false));
                            return Collections.unmodifiableList(newList);
                        });
                    } else {
                        System.out.println("[Server] " + nick + " is re-joining previous game...");
                    }

                    if (currGameAndController != null)
                        updateGameForPlayer(
                                nick,
                                Objects.requireNonNull(currGame, "Controller is not null but game is null?"),
                                currGameAndController.controller(),
                                observableTracker,
                                lobbyUpdater,
                                gameControllerFactory);
                } catch (DisconnectedException ex) {
                    throw new DisconnectedException("Player disconnected during handshake process", ex);
                }

                return lobby;
            }
        } while (true);
    }

    private void updateGameForPlayer(String nick,
                                     ServerGame game,
                                     GameServerController gameController,
                                     PlayerObservableTracker observableTracker,
                                     LobbyUpdater lobbyUpdater,
                                     BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
            throws DisconnectedException {

        final Map<String, Game.PlayerFactory> clientPlayers = game.getPlayers().stream()
                .collect(Collectors.toMap(
                        ServerPlayer::getNick,
                        p -> (isStartingPlayer, isCurrentTurnFactory, isFirstFinisherFactory) -> new Player(
                                p.getNick(),
                                p.getShelfie(),
                                isStartingPlayer,
                                p.connected().get(),
                                isCurrentTurnFactory,
                                isFirstFinisherFactory,
                                p.score().get())));
        final ServerPlayer thePlayer = game.getPlayers().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("" +
                        "Missing player " + nick + " which is supposed to be ingame " +
                        "(found players: " + game.getPlayers() + ")"));
        thePlayer.connected().set(true);

        final GameUpdater gameUpdater = lobbyUpdater.updateGame(new GameAndController<>(
                new Game(
                        game.getGameID(),
                        game.getBoard(),
                        // Use another stream 'cause we need to keep order
                        game.getPlayers().stream()
                                .map(p -> clientPlayers.get(p.getNick()))
                                .toList(),
                        game.getPlayers().indexOf(thePlayer),
                        game.getPlayers().indexOf(game.getStartingPlayer()),
                        game.getPlayers().indexOf(game.currentTurn().get()),
                        players -> game.getCommonGoals().stream().map(goal -> new CommonGoal(
                                goal.getType(),
                                goal.achieved().get().stream()
                                        .map(achievedPlayer -> players.stream()
                                                .filter(p -> p.getNick().equals(achievedPlayer.getNick()))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("" +
                                                        "Missing player " + achievedPlayer + " which has supposedly " +
                                                        "achieved a common goal" +
                                                        "(found players: " + players + ")")))
                                        .toList()))
                                .toList(),
                        thePlayer.getPersonalGoal(),
                        game.firstFinisher().get() == null ? null : game.getPlayers().indexOf(game.firstFinisher().get())),
                gameControllerFactory.apply(thePlayer, gameController)));
        // Register all listeners to the game model
        game.getBoard().tiles().forEach(tileAndCoords -> observableTracker.registerObserver(tileAndCoords.tile(),
                tile -> gameUpdater.updateBoardTile(tileAndCoords.row(), tileAndCoords.col(), tile)));
        game.getPlayers().forEach(p -> p.getShelfie().tiles()
                .forEach(tileAndCoords -> observableTracker.registerObserver(tileAndCoords.tile(),
                        tile -> gameUpdater.updatePlayerShelfieTile(
                                p.getNick(),
                                tileAndCoords.row(),
                                tileAndCoords.col(),
                                tile))));
        observableTracker.registerObserver(game.currentTurn(), p -> gameUpdater.updateCurrentTurn(p.getNick()));
        observableTracker.registerObserver(game.firstFinisher(),
                p -> gameUpdater.updateFirstFinisher(p == null ? null : p.getNick()));
        game.getCommonGoals().forEach(goal -> observableTracker.registerObserver(
                goal.achieved(),
                players -> gameUpdater.updateAchievedCommonGoal(goal.getType(), players.stream()
                        .map(ServerPlayer::getNick)
                        .collect(Collectors.toList()))));
    }

    public void onDisconnectPlayer(String nick, Throwable cause) {
        heartbeats.remove(nick);

        final var lobbyAndController = getLobbyFor(nick);
        if (lobbyAndController != null)
            lobbyAndController.controller().onDisconnectPlayer(nick, cause);
    }
}
