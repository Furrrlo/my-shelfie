package it.polimi.ingsw.socket.packets;

/** Interface used to identify packets used for {@link it.polimi.ingsw.controller.GameController}s communications */
public sealed interface GameActionPacket extends C2SPacket permits MakeMovePacket, SendMessagePacket {
}
