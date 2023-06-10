package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.NickNotValidException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerController implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private final Clock clock;
    private final ScheduledExecutorService heartbeatExecutor;
    private final ExecutorService heartbeatThreadPool;

    private final ConcurrentMap<String, HeartbeatHandler> heartbeats = new ConcurrentHashMap<>();

    private final Lock lobbiesLock = new ReentrantLock();
    private final Set<ServerLobbyAndController<ServerLobby>> lobbies = ConcurrentHashMap.newKeySet();

    public ServerController(long pingInterval, TimeUnit pingIntervalUnit) {
        this(Clock.systemUTC(), pingInterval, pingIntervalUnit);
    }

    public ServerController(Clock clock,
                            long pingInterval,
                            TimeUnit pingIntervalUnit) {
        this.clock = clock;
        this.heartbeatThreadPool = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                .name("ServerController-heartbeat-thread-", 0)
                .factory());
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform()
                .name("ServerController-heartbeat-scheduler-thread")
                .factory());
        this.heartbeatExecutor.scheduleAtFixedRate(
                this::detectDisconnectedPlayers,
                0, pingInterval, pingIntervalUnit);
    }

    @Override
    public void close() {
        heartbeatExecutor.shutdown();
        heartbeatThreadPool.shutdown();
    }

    @SuppressWarnings("FutureReturnValueIgnored") // We don't need to keep track of it as we shut down everything together
    private void detectDisconnectedPlayers() {
        heartbeats.forEach(
                (nick, heartbeatHandler) -> heartbeatThreadPool
                        .submit(() -> heartbeatHandler.sendHeartbeat(Instant.now(clock))));
    }

    private @Nullable ServerLobbyAndController<ServerLobby> getLobbyFor(String nick) {
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
                        var lockedLobby = new LockProtected<>(new ServerLobby());
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
        var lock = getOnlyLobbyLock();
        if (lock == null) {
            runnable.run();
            return;
        }

        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    @VisibleForTesting
    public @Nullable Lock getOnlyLobbyLock() {
        if (lobbies.size() != 1)
            throw new AssertionError("This method is supposed to be used for testing when there's only 1 lobby");

        var lobbyAndController = lobbies.stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("This method is supposed to be used for testing " +
                        "when there's only 1 lobby"));
        return lobbyAndController == null
                ? null
                : lobbyAndController.lobby().getLock();
    }

    public <T> T supplyOnLocks(String nick, Supplier<T> callable) {
        final var lobbyAndController = getLobbyFor(nick);
        if (lobbyAndController == null)
            return callable.get();

        try (var ignored = lobbyAndController.lobby().use()) {
            return callable.get();
        }
    }

    /**
     * Badge class which can be used as a parameter by methods to signal that the method, despite public,
     * should only be called by the ServerController class, in particular only when the ServerController
     * is already holding the required lobby/game locks for a certain player.
     * <p>
     * See <a href="https://awesomekling.github.io/Serenity-C++-patterns-The-Badge/">the SerenityOS badge pattern</a>
     */
    public static class LockBadge {
        /** Private constructor so that only the ServerController can instantiate the badges */
        private LockBadge() {
        }
    }

    public void connectPlayer(String nick, HeartbeatHandler heartbeatHandler)
            throws NickNotValidException {
        if (nick.isEmpty())
            throw new NickNotValidException("Nick can't be empty");
        if (heartbeats.putIfAbsent(nick, heartbeatHandler) != null)
            throw new NickNotValidException("This nick is already in use");
    }

    public LobbyView joinGame(String nick,
                              PlayerObservableTracker observableTracker,
                              Runnable onGameOver,
                              LobbyUpdaterFactory lobbyUpdaterFactory,
                              LobbyControllerFactory lobbyControllerFactory,
                              BiFunction<ServerPlayer, GameServerController, GameController> gameControllerFactory)
            throws DisconnectedException {
        do {
            final var serverLobbyAndController = getOrCreateLobby(nick);
            final var lockedServerLobby = serverLobbyAndController.lobby();

            try (var serverLobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = serverLobbyCloseable.obj();
                var lockBadge = new LockBadge();

                // This is basically double-checked locking, getOrCreateGameLobbySomehow() checks with no lock
                // so that it can discard options fast, then here we re-check while actually holding the lock
                // to guarantee concurrency
                final List<String> joinedPlayersNicks = serverLobby.joinedPlayers().get().stream()
                        .map(LobbyPlayer::getNick)
                        .toList();

                //I have the lock, so if this lobby is not open yet, I become the creator.
                if (!joinedPlayersNicks.contains(nick) && !(serverLobby.canOnePlayerJoin() || !serverLobby.isOpen()))
                    continue; // If we fail the test, let's just retry and search a new one

                final LobbyAndController<Lobby> lobbyAndController;
                final LobbyUpdater lobbyUpdater;
                try {
                    lobbyUpdater = lobbyUpdaterFactory.create(lobbyAndController = new LobbyAndController<>(
                            new Lobby(serverLobby.requiredPlayers().get(), serverLobby.joinedPlayers().get(), nick),
                            lobbyControllerFactory.create(serverLobbyAndController.controller())));
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
                                observableTracker.registerObserver(lockBadge, player.ready(),
                                        ready -> lobbyUpdater.updatePlayerReady(player.getNick(), ready)));

                    observableTracker.registerObserver(lockBadge, serverLobby.requiredPlayers(),
                            lobbyUpdater::updateRequiredPlayers);

                    observableTracker.registerObserver(lockBadge, serverLobby.joinedPlayers(), newLobbyPlayers -> {
                        lobbyUpdater.updateJoinedPlayers(newLobbyPlayers.stream()
                                .map(LobbyPlayer::getNick)
                                .collect(Collectors.toList()));
                        // Add observers to players which joined
                        for (LobbyPlayer p0 : newLobbyPlayers)
                            playersRegisteredObservers.computeIfAbsent(p0, p -> observableTracker.registerObserver(
                                    lockBadge, p.ready(),
                                    ready -> lobbyUpdater.updatePlayerReady(p.getNick(), ready)));
                        // Remove observers from players which left
                        // TODO: unregister observers
                        playersRegisteredObservers.entrySet().removeIf(e -> !newLobbyPlayers.contains(e.getKey()));
                    });
                    observableTracker.registerObserver(lockBadge, serverLobby.game(), game0 -> {
                        try (var serverLobbyCloseable0 = lockedServerLobby.use()) {
                            var game = serverLobbyCloseable0.obj().game().get();
                            if (game != null) {
                                updateGameForPlayer(
                                        nick,
                                        serverLobbyAndController,
                                        lockBadge,
                                        game.game(),
                                        game.controller(),
                                        observableTracker,
                                        onGameOver,
                                        lobbyUpdater,
                                        gameControllerFactory);
                            }
                        }
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
                        LOGGER.info("[Server] {} is re-joining previous game...", nick);
                        serverLobbyAndController.controller().onReconnectedPlayer(nick);
                    }

                    if (currGameAndController != null)
                        updateGameForPlayer(
                                nick,
                                serverLobbyAndController,
                                lockBadge,
                                Objects.requireNonNull(currGame, "Controller is not null but game is null?"),
                                currGameAndController.controller(),
                                observableTracker,
                                onGameOver,
                                lobbyUpdater,
                                gameControllerFactory);
                } catch (DisconnectedException ex) {
                    throw new DisconnectedException("Player disconnected during handshake process", ex);
                }

                return lobbyAndController.lobby();
            }
        } while (true);
    }

    private void updateGameForPlayer(String nick,
                                     ServerLobbyAndController<ServerLobby> serverLobbyAndController,
                                     LockBadge lockBadge,
                                     ServerGame game,
                                     GameServerController gameController,
                                     PlayerObservableTracker observableTracker,
                                     Runnable onGameOver,
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
                                p.getNick().equals(nick)
                                        ? p.privateScore().get()
                                        : p.publicScore().get())));
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
                        game.firstFinisher().get() == null ? null : game.getPlayers().indexOf(game.firstFinisher().get()),
                        game.endGame().get(),
                        game.suspended().get()),
                gameControllerFactory.apply(thePlayer, gameController)));
        // Register all listeners to the game model
        game.getBoard().tiles().forEach(tileAndCoords -> observableTracker.registerObserver(
                lockBadge, tileAndCoords.tile(),
                tile -> gameUpdater.updateBoardTile(tileAndCoords.row(), tileAndCoords.col(), tile)));
        game.getPlayers().forEach(p -> {
            observableTracker.registerObserver(lockBadge, p.connected(),
                    connected -> gameUpdater.updatePlayerConnected(p.getNick(), connected));

            if (p.getNick().equals(nick)) {
                observableTracker.registerObserver(lockBadge, p.privateScore(),
                        score -> gameUpdater.updatePlayerScore(p.getNick(), score));
            } else {
                // If the game is not over, send the public score
                observableTracker.registerObserver(lockBadge, p.publicScore(), score -> {
                    if (!game.endGame().get())
                        gameUpdater.updatePlayerScore(p.getNick(), score);
                });
                // If the game is over, send the private score
                observableTracker.registerObserver(lockBadge, p.privateScore(), score -> {
                    if (game.endGame().get())
                        gameUpdater.updatePlayerScore(p.getNick(), score);
                });
            }

            p.getShelfie().tiles().forEach(tileAndCoords -> observableTracker.registerObserver(lockBadge, tileAndCoords.tile(),
                    tile -> gameUpdater.updatePlayerShelfieTile(
                            p.getNick(),
                            tileAndCoords.row(),
                            tileAndCoords.col(),
                            tile)));
        });
        observableTracker.registerObserver(lockBadge, game.suspended(), gameUpdater::updateSuspended);

        // updating message only if message nickReceivingPlayer == nick, or if it's for everyone
        observableTracker.registerObserver(lockBadge, game.message(), m -> {
            if (m != null && (m.nickReceivingPlayer().equals(nick)
                    || m.isForEveryone()
                    || nick.equals(m.nickSendingPlayer())))
                gameUpdater.updateMessage(m);
        });

        observableTracker.registerObserver(lockBadge, game.currentTurn(), p -> gameUpdater.updateCurrentTurn(p.getNick()));
        observableTracker.registerObserver(lockBadge, game.firstFinisher(),
                p -> gameUpdater.updateFirstFinisher(p == null ? null : p.getNick()));
        game.getCommonGoals().forEach(goal -> observableTracker.registerObserver(
                lockBadge, goal.achieved(),
                players -> gameUpdater.updateAchievedCommonGoal(goal.getType(), players.stream()
                        .map(ServerPlayer::getNick)
                        .collect(Collectors.toList()))));
        observableTracker.registerObserver(lockBadge, game.endGame(), gameOver -> {
            // Update all scores to the private ones
            if (gameOver) {
                for (ServerPlayer p : game.getPlayers())
                    gameUpdater.updatePlayerScore(p.getNick(), p.privateScore().get());
            }

            gameUpdater.updateEndGame(gameOver);

            if (gameOver) {
                onGameOver.run();
                lobbies.remove(serverLobbyAndController);
            }
        });
    }

    public void onDisconnectPlayer(String nick) {
        boolean wasConnected = heartbeats.remove(nick) != null;
        // Only call the rest if the player was actually connected
        if (!wasConnected)
            return;

        final var lobbyAndController = getLobbyFor(nick);
        if (lobbyAndController != null)
            lobbyAndController.controller().onDisconnectPlayer(nick);
    }
}
