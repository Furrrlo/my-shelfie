package it.polimi.ingsw.socket.packets;

import java.util.Objects;

/**
 * Empty ack packet with only the sequence number of the packet to acknowledge
 * This will also be wrapped in a {@link SeqPacket}, but it's seqN is not used.
 *
 * @see it.polimi.ingsw.socket.SocketManager.PacketReplyContext#ack()
 */
public final class SimpleAckPacket implements S2CAckPacket, C2SAckPacket {
    private long seqAck;

    public SimpleAckPacket(long seqAck) {
        this.seqAck = seqAck;
    }

    @Override
    public void setSeqAck(long seqAck) {
        this.seqAck = seqAck;
    }

    @Override
    public long seqAck() {
        return seqAck;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (SimpleAckPacket) obj;
        return this.seqAck == that.seqAck;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqAck);
    }

    @Override
    public String toString() {
        return "SimpleAckPacket[" +
                "seqAck=" + seqAck + ']';
    }

}
