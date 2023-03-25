package it.polimi.ingsw.socket.packets;

public record UpdateCurrentTurnPacket(String nick) implements S2CPacket {
}
