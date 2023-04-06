package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.util.List;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class GameServerController {

    private final LockProtected<ServerGame> game;

    public GameServerController(LockProtected<ServerGame> game) {
        this.game = game;
    }

    public void disconnectPlayer(String nick, Throwable cause) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            ServerPlayer serverPlayer = game.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElse(null);

            if (serverPlayer != null) {
                // TODO: set serverPlayer as not connected
                serverPlayer.connected().set(false);
            }
        }
    }

    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            // TODO:
        }
    }
}
