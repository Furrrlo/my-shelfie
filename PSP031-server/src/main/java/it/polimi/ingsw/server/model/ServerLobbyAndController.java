package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.server.controller.LockProtected;

import java.io.Serializable;

public record ServerLobbyAndController<L extends ServerLobbyView>(
        LockProtected<L> lobby,
        LobbyServerController controller) implements Serializable {
}
