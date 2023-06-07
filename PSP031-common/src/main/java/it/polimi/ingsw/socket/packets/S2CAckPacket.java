package it.polimi.ingsw.socket.packets;

/** Server-to-client packet which acknowledges another one previously sent in the opposite direction */
public interface S2CAckPacket extends S2CPacket, AckPacket {
}
