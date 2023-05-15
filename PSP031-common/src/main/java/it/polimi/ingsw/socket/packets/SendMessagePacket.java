package it.polimi.ingsw.socket.packets;

public record SendMessagePacket(String message, String nickReceivingPlayer) implements C2SPacket, GameActionPacket {
}
