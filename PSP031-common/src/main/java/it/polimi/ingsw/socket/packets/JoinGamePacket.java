package it.polimi.ingsw.socket.packets;

public record JoinGamePacket(String nick) implements C2SPacket {
}
