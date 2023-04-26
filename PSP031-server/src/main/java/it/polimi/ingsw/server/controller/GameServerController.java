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
                    .ifPresent(serverPlayer -> serverPlayer.connected().set(false));
        }
    }

    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) throws IllegalArgumentException {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
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

        }
    }
}
