package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.controller.LockProtected;

import java.io.Serializable;

public record ServerGameAndController<S extends ServerGameView>(LockProtected<S> game, GameServerController controller) implements Serializable {
}
