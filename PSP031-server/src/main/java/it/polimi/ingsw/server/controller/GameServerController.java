package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.util.List;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class GameServerController {

    private final ServerGame game;

    public GameServerController(ServerGame game) {
        this.game = game;
    }

    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) {
        //TODO
    }
}
