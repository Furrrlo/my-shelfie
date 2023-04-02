package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class GameServerController {

    private final LockProtected<ServerGame> game;

    public GameServerController(LockProtected<ServerGame> game) {
        this.game = game;
    }

    public void runOnLocks(Runnable runnable) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            runnable.run();
        }
    }

    public <T> T supplyOnLocks(Supplier<T> callable) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            return callable.get();
        }
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
