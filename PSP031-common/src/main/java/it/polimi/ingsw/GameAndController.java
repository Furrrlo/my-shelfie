package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;

import java.io.Serializable;

public record GameAndController<G extends GameView>(G game, GameController controller) implements Serializable {
}
