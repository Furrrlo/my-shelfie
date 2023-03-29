package it.polimi.ingsw;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.LobbyView;

import java.io.Serializable;

public record LobbyAndController<L extends LobbyView>(L lobby, LobbyController controller) implements Serializable {
}
