package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

/** Packet which indicates that the other side is closing */
record ClosePacket() implements S2CPacket, C2SPacket {
}
