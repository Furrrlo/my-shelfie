package it.polimi.ingsw;

import java.time.Instant;

public interface HeartbeatHandler {

    Instant sendHeartbeat(Instant serverTime) throws DisconnectedException;
}
