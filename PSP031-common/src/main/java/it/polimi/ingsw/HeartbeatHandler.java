package it.polimi.ingsw;

import java.time.Instant;

public interface HeartbeatHandler {

    void sendHeartbeat(Instant serverTime);
}
