package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.server.model.ServerLobby;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LobbyServerController {

    private final LockProtected<ServerLobby> lockedLobby;

    public LobbyServerController(LockProtected<ServerLobby> lockedLobby) {
        this.lockedLobby = lockedLobby;
    }

    public void runOnLocks(Runnable runnable) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            var gameAndController = lobby.game().get();
            if (gameAndController == null)
                runnable.run();
            else
                gameAndController.controller().runOnLocks(runnable);
        }
    }

    public <T> T supplyOnLocks(Supplier<T> callable) {
        try (var lobbyCloseable = lockedLobby.use()) {
            var lobby = lobbyCloseable.obj();
            var gameAndController = lobby.game().get();
            if (gameAndController == null)
                return callable.get();

            return gameAndController.controller().supplyOnLocks(callable);
        }
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
            if (game != null)
                game.controller().disconnectPlayer(nick, cause);
            else
                // If the game hasn't started yet, just remove the lobbyPlayer
                lobby.joinedPlayers().update(lobbyPlayers -> {
                    List<LobbyPlayer> newP = new ArrayList<>(lobbyPlayers);
                    newP.remove(lobbyPlayer);
                    System.out.println("[Server] Removed " + nick);
                    return newP;
                });
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
}
