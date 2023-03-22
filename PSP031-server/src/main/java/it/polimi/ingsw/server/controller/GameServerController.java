package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.Game;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class GameServerController {

    private final Game game;

    public GameServerController(Game game) {
        this.game = game;
    }
}
