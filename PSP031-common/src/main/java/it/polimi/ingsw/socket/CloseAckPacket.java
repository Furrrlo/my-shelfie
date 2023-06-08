package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;

/** Acknowledge that {@link ClosePacket} was received and this side is also starting to close */
record CloseAckPacket() implements S2CAckPacket, C2SAckPacket {
}
