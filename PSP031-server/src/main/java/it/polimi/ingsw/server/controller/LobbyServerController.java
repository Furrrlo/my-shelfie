package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.*;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * Actual {@link it.polimi.ingsw.controller.LobbyController} server implementation which all network controllers
 * delegate to
 * <p>
 * This implements all the {@link it.polimi.ingsw.controller.LobbyController} interface methods, but with an overload
 * which is the nick of the player executing the method
 *
 * @see it.polimi.ingsw.controller.LobbyController
 */
public class LobbyServerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyServerController.class);

    private static final List<Tile> BAG = List.of(
            new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0),
            new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0),
            new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1),
            new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1),
            new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2),
            new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 0),
            new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0),
            new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0),
            new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1),
            new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1),
            new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2),
            new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 0),
            new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0),
            new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0),
            new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1),
            new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1),
            new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2),
            new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 0),
            new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0),
            new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0),
            new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1),
            new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1),
            new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2),
            new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 0),
            new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0),
            new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0),
            new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1),
            new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1),
            new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2),
            new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 0));

    private final LockProtected<ServerLobby> lockedLobby;

    public LobbyServerController(LockProtected<ServerLobby> lockedLobby) {
        this.lockedLobby = lockedLobby;
    }

    /**
     * Hook method called by the network controllers when a player disconnects
     *
     * @param nick nick of the player which disconnected, used as an identifier
     */
    public void onDisconnectPlayer(String nick) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            LobbyPlayer lobbyPlayer = lobby.joinedPlayers().get().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElse(null);

            var game = lobbyCloseable.obj().game().get();
            if (game != null) {
                game.controller().onDisconnectPlayer(nick);
            } else {
                // If the game hasn't started yet, just remove the lobbyPlayer
                lobby.joinedPlayers().update(lobbyPlayers -> {
                    List<LobbyPlayer> newP = new ArrayList<>(lobbyPlayers);
                    newP.remove(lobbyPlayer);
                    LOGGER.info("[Server] Removed {}", nick);
                    return Collections.unmodifiableList(newP);
                });
            }
        }
    }

    /**
     * Hook method called by the network controllers when a player reconnects after a disconnection
     *
     * @param nick nick of the player which reconnected, used as an identifier
     */
    public void onReconnectedPlayer(String nick) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            var game = lobby.game().get();
            if (game != null)
                game.controller().onReconnectedPlayer(nick);
        }
    }

    /**
     * Implementation of {@link it.polimi.ingsw.controller.LobbyController#setRequiredPlayers(int)}, see
     * there for detailed docs
     *
     * @param nick the player executing the method
     */
    public void setRequiredPlayers(String nick, int requiredPlayers) {
        try (var use = lockedLobby.use()) {
            ServerLobby lobby = use.obj();
            if (!lobby.isLobbyCreator(nick))
                throw new IllegalArgumentException("This player is not the creator of this lobby");

            if (requiredPlayers != 0
                    && (requiredPlayers < ServerLobbyView.MIN_PLAYERS || requiredPlayers > ServerLobbyView.MAX_PLAYERS))
                throw new IllegalArgumentException("Number of player not valid");

            lobby.requiredPlayers().set(requiredPlayers);

            checkGameStart(lobby);
        }
    }

    /**
     * Implementation of {@link it.polimi.ingsw.controller.LobbyController#ready(boolean)}}, see
     * there for detailed docs
     *
     * @param nick the player executing the method
     */
    public void ready(String nick, boolean ready) {
        try (var use = lockedLobby.use()) {
            var lobby = use.obj();
            if (lobby.hasGameStarted())
                return;
            LobbyPlayer lobbyPlayer = lobby.joinedPlayers().get().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Somehow missing the player " + nick));
            lobbyPlayer.ready().set(ready);

            checkGameStart(lobby);
        }
    }

    private void checkGameStart(ServerLobby lobby) {
        List<LobbyPlayer> lobbyPlayers = lobby.joinedPlayers().get();
        if (lobbyPlayers.stream().allMatch(p -> p.ready().get())
                && ((!lobby.hasRequiredPlayers() && lobbyPlayers.size() > 1)
                        || (lobby.hasRequiredPlayers()
                                && lobbyPlayers.size() >= lobby.requiredPlayers().get()))) {
            final ServerGame game = createGame(0, lobbyPlayers);
            lobby.game().set(new ServerGameAndController<>(game,
                    new GameServerController(new LockProtected<>(game, lockedLobby.getLock()))));
        }
    }

    @VisibleForTesting
    public static ServerGame createGame(int gameId, List<LobbyPlayer> lobbyPlayers) {
        return createGame(gameId, RandomGenerator.getDefault(), lobbyPlayers);
    }

    @VisibleForTesting
    public static ServerGame createGame(int gameId,
                                        RandomGenerator random,
                                        List<LobbyPlayer> lobbyPlayers) {
        LOGGER.info("game started");

        final var remainingCommonGoalTypes = new ArrayList<Type>();
        Collections.addAll(remainingCommonGoalTypes, Type.values());
        final List<ServerCommonGoal> commonGoals = List.of(
                new ServerCommonGoal(remainingCommonGoalTypes.remove(random.nextInt(remainingCommonGoalTypes.size()))),
                new ServerCommonGoal(remainingCommonGoalTypes.remove(random.nextInt(remainingCommonGoalTypes.size()))));

        final var bag = new ArrayList<>(BAG);
        Collections.shuffle(bag, Random.from(random));

        final var firstFinisher = SerializableProperty.<ServerPlayer> nullableProperty(null);

        final List<ServerPlayer> players;
        final var personalGoalIndexes = new ArrayList<Integer>();
        int temp;
        for (int i = 0; i < lobbyPlayers.size(); i++) {
            temp = random.nextInt(PersonalGoal.PERSONAL_GOALS.size());
            while (personalGoalIndexes.contains(temp))
                temp = random.nextInt(PersonalGoal.PERSONAL_GOALS.size());
            personalGoalIndexes.add(temp);
        }
        var game = new ServerGame(
                gameId,
                new Board(lobbyPlayers.size()),
                bag,
                players = lobbyPlayers.stream()
                        .map(n -> new ServerPlayer(
                                n.getNick(),
                                new PersonalGoal(personalGoalIndexes.remove(random.nextInt(personalGoalIndexes.size()))),
                                p -> new PublicScoreProvider(p, commonGoals, firstFinisher),
                                PrivateScoreProvider::new))
                        .collect(Collectors.toList()),
                random.nextInt(players.size()),
                commonGoals, firstFinisher);
        game.refillBoard();
        return game;
    }
}
