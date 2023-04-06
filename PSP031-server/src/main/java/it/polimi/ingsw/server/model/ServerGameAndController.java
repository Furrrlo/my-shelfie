package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.GameServerController;

import java.io.Serializable;

public record ServerGameAndController<S extends ServerGameView>(
        S game,
        GameServerController controller) implements Serializable {
}
