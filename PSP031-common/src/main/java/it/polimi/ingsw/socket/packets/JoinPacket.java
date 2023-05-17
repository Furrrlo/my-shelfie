package it.polimi.ingsw.socket.packets;

public record JoinPacket(String nick) implements C2SPacket {
}
