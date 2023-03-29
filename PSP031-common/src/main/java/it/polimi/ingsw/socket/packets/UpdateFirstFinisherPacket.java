package it.polimi.ingsw.socket.packets;

public record UpdateFirstFinisherPacket(String nick) implements GameUpdaterPacket {
}
