package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.server.model.ServerLobby;

public class LobbyServerController {

    private final LockProtected<ServerLobby> lockedLobby;

    public LobbyServerController(LockProtected<ServerLobby> lockedLobby) {
        this.lockedLobby = lockedLobby;
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
