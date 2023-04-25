package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.util.ArrayList;
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
            List<Property<Tile>> selectedTiles = new ArrayList<>();
            if (!game.getBoard().checkBoardCoord(selected)
                    || !player.getShelfie().checkColumnSpace(shelfCol, selected.size())) {
                throw new IllegalArgumentException("Invalid move");
            }

            //remove tiles from board
            for (BoardCoord coord : selected) {
                selectedTiles.add(game.getBoard().tile(coord.row(), coord.col()));
                game.getBoard().removeTile(coord.row(), coord.col());
            }

            //add tiles to shelfie
            player.getShelfie().placeTiles(selectedTiles, shelfCol);

            //TODO: check if player has finished and shelfie is full

            if (game.getBoard().isEmpty() && game.getBag().size()>0) {
                game.refillBoard();
            }
            else if (game.getBoard().isEmpty() && game.getBag().size()==0) {
                //TODO: end game
                //game.endGame();
                //game.firstFinisher=player;
            }

        }
    }
}
