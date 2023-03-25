package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Lobby;

import java.util.Objects;

/**
 * Ack packet with a {@link Lobby}
 * This will also be wrapped in a {@link SeqPacket}
 */
public final class LobbyPacket implements S2CAckPacket {
    private final Lobby lobby;
    private long seqAck;

    public LobbyPacket(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setSeqAck(long seqAck) {
        this.seqAck = seqAck;
    }

    public Lobby lobby() {
        return lobby;
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
        var that = (LobbyPacket) obj;
        return Objects.equals(this.lobby, that.lobby) &&
                this.seqAck == that.seqAck;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobby, seqAck);
    }

    @Override
    public String toString() {
        return "LobbyPacket[" +
                "lobby=" + lobby + ", " +
                "seqAck=" + seqAck + ']';
    }

}
