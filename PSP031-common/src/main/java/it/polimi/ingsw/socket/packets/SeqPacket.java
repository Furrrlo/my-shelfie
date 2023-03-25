package it.polimi.ingsw.socket.packets;

import java.io.Serializable;

public record SeqPacket(Packet packet, long seqN) implements Serializable {
    @Override
    public String toString() {
        return "SeqPacket{" +
                "packet=" + packet +
                ", seqN=" + seqN +
                '}';
    }
}
