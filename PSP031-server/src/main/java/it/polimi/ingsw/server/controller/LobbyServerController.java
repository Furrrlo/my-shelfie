package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.*;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

public class LobbyServerController {

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

    public void onDisconnectPlayer(String nick, Throwable cause) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            LobbyPlayer lobbyPlayer = lobby.joinedPlayers().get().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElse(null);

            var game = lobbyCloseable.obj().game().get();
            if (game != null) {
                game.controller().onDisconnectPlayer(nick, cause);
            } else {
                // If the game hasn't started yet, just remove the lobbyPlayer
                lobby.joinedPlayers().update(lobbyPlayers -> {
                    List<LobbyPlayer> newP = new ArrayList<>(lobbyPlayers);
                    newP.remove(lobbyPlayer);
                    System.out.println("[Server] Removed " + nick);
                    return Collections.unmodifiableList(newP);
                });
            }
        }
    }

    public void ready(String nick, boolean ready) {
        try (var use = lockedLobby.use()) {
            List<LobbyPlayer> lobbyPlayers = use.obj().joinedPlayers().get();
            LobbyPlayer lobbyPlayer = lobbyPlayers.stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Somehow missing the player " + nick));
            lobbyPlayer.ready().set(ready);
            if (lobbyPlayers.size() > 1 && lobbyPlayers.stream().allMatch(p -> p.ready().get())) {
                final ServerGame game = createGame(0, new Random(), lobbyPlayers);
                use.obj().game().set(new ServerGameAndController<>(game,
                        new GameServerController(new LockProtected<>(game, lockedLobby.getLock()))));
            }
        }
    }

    @VisibleForTesting
    public static ServerGame createGame(int gameId,
                                        RandomGenerator random,
                                        List<LobbyPlayer> lobbyPlayers) {
        System.out.println("game started");
        final var firstFinisher = SerializableProperty.<ServerPlayer> nullableProperty(null);
        // TODO: extract 2 common goals randomly
        final List<ServerCommonGoal> commonGoals = List.of(
                new ServerCommonGoal(Type.CROSS),
                new ServerCommonGoal(Type.ALL_CORNERS));
        final List<ServerPlayer> players;
        return new ServerGame(
                gameId,
                new Board(lobbyPlayers.size()),
                BAG, // This is defensively copied anyway
                players = lobbyPlayers.stream()
                        .map(n -> new ServerPlayer(
                                n.getNick(),
                                // TODO: extract personal goal randomly
                                new PersonalGoal(1),
                                p -> new ScoreProvider(p, commonGoals, firstFinisher)))
                        .collect(Collectors.toList()),
                players.size() - 1, // TODO: choose who starts randomly
                commonGoals,
                firstFinisher);
    }
}
