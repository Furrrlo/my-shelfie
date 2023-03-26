package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Game;

public record CreateGamePacket(Game game) implements S2CPacket {
}
