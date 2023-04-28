package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    .ifPresent(serverPlayer -> {
                        serverPlayer.connected().set(false);
                        changeCurrentTurn(game);
                    });
        }
    }

    public void onReconnectedPlayer(String nick) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            game.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .ifPresent(serverPlayer -> {
                        serverPlayer.connected().set(true);
                        // If the current player is disconnected, we now have a new connected player that can play
                        if (!game.currentTurn().get().connected().get())
                            changeCurrentTurn(game);
                    });
        }
    }

    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) throws IllegalArgumentException {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();

            if (!game.currentTurn().get().equals(player)) {
                //TODO: disconnect player
                return;
            }

            List<Tile> selectedTiles = new ArrayList<>();
            if (!game.getBoard().checkBoardCoord(selected)
                    || !player.getShelfie().checkColumnSpace(shelfCol, selected.size())) {
                throw new IllegalArgumentException("Invalid move");
            }

            //remove tiles from board
            for (BoardCoord coord : selected) {
                Property<@Nullable Tile> tileProp = game.getBoard().tile(coord.row(), coord.col());
                selectedTiles.add(Objects.requireNonNull(tileProp.get(), "Checked tile was invalid"));
                Property.setNullable(tileProp, null);
            }

            //add tiles to shelfie
            player.getShelfie().placeTiles(selectedTiles, shelfCol);

            //TODO: check if player has finished and shelfie is full

            if (game.getBoard().isEmpty() && game.getBag().size() > 0) {
                game.refillBoard();
            } else if (game.getBoard().isEmpty() && game.getBag().size() == 0) {
                //TODO: end game
                //game.endGame();
                //game.firstFinisher=player;
            }

            // Change current turn
            changeCurrentTurn(game);
        }
    }

    private void changeCurrentTurn(ServerGame game) {
        int nextPlayerIdx = game.getPlayers().indexOf(game.currentTurn().get());
        // Try for all the remaining players
        for (int i = 0; i < game.getPlayers().size() - 1; i++) {
            nextPlayerIdx++;
            if (nextPlayerIdx >= game.getPlayers().size())
                nextPlayerIdx = 0;

            var nextPlayer = game.getPlayers().get(nextPlayerIdx);
            // Only pick a player if there's a currently connected one
            if (nextPlayer.connected().get()) {
                game.currentTurn().set(nextPlayer);
                return;
            }
        }
    }
}
