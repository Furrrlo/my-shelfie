package it.polimi.ingsw.socket.packets;

public sealed interface GameActionPacket extends C2SPacket permits MakeMovePacket, SendMessagePacket {
}
