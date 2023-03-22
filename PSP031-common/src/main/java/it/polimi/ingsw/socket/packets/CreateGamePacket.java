package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;

public record CreateGamePacket(GameAndController<Game> game) implements S2CPacket {
}
