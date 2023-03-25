package it.polimi.ingsw.socket.packets;

public interface AckPacket extends Packet {
    long seqAck();

    void setSeqAck(long seqAck);
}
