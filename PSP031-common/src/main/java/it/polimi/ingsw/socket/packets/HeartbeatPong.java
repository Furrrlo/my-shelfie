package it.polimi.ingsw.socket.packets;

import java.time.Instant;
import java.util.Objects;

public final class HeartbeatPong implements C2SAckPacket {

    private final Instant serverTime;
    private long seqAck;

    public HeartbeatPong(Instant serverTime) {
        this.serverTime = serverTime;
    }

    public void setSeqAck(long seqAck) {
        this.seqAck = seqAck;
    }

    @Override
    public long seqAck() {
        return seqAck;
    }

    public Instant serverTime() {
        return serverTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (HeartbeatPong) obj;
        return Objects.equals(this.serverTime, that.serverTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverTime);
    }

    @Override
    public String toString() {
        return "HeartbeatPong[" +
                "serverTime=" + serverTime + ']';
    }
}
