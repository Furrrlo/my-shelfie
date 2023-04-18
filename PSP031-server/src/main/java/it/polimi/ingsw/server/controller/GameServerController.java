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

    public void onDisconnectPlayer(String nick, Throwable cause) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            game.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .ifPresent(serverPlayer -> serverPlayer.connected().set(false));
        }
    }

    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) throws IllegalArgumentException {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();

        }
    }
}
