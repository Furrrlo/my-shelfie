package it.polimi.ingsw.socket.packets;

public record UpdateSuspendedPacket(boolean suspended) implements GameUpdaterPacket {
}
