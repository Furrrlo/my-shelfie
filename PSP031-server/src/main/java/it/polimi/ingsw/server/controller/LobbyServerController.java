package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerCommonGoal;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerLobby;
import it.polimi.ingsw.server.model.ServerPlayer;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyServerController {

    private static final List<Tile> BAG_TEMPLATE = List.of(); // TODO: bag

    private final LockProtected<ServerLobby> lockedLobby;

    public LobbyServerController(LockProtected<ServerLobby> lockedLobby) {
        this.lockedLobby = lockedLobby;
    }

    public void disconnectPlayer(String nick, Throwable cause) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            LobbyPlayer lobbyPlayer = lobby.joinedPlayers().get().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElse(null);
            // TODO: what do we do with the player?

            var game = lobbyCloseable.obj().game().get();
            if (game != null) {
                game.controller().disconnectPlayer(nick, cause);
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
            LobbyPlayer lobbyPlayer = use.obj().joinedPlayers().get().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Somehow missing the player " + nick));
            lobbyPlayer.ready().set(ready);
        }
    }

    @VisibleForTesting
    public static ServerGame createGame(int gameId, List<LobbyPlayer> lobbyPlayers) {
        final var firstFinisher = SerializableProperty.<ServerPlayer> nullableProperty(null);
        // TODO: extract 2 common goals randomly
        final List<ServerCommonGoal> commonGoals = List.of(
                new ServerCommonGoal(Type.CROSS),
                new ServerCommonGoal(Type.ALL_CORNERS));
        final List<ServerPlayer> players;
        return new ServerGame(
                gameId,
                new Board(lobbyPlayers.size()),
                BAG_TEMPLATE, // This is defensively copied anyway
                players = lobbyPlayers.stream()
                        .map(n -> new ServerPlayer(
                                n.getNick(),
                                // TODO: extract personal goal randomly
                                new PersonalGoal(new Tile[6][5]),
                                p -> new ScoreProvider(p, commonGoals, firstFinisher)))
                        .collect(Collectors.toList()),
                players.size() - 1, // TODO: choose who starts randomly
                commonGoals,
                firstFinisher);
    }
}
