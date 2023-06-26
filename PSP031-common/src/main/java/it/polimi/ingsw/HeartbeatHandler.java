package it.polimi.ingsw;

import java.time.Instant;

/**
 * Per-client object which is able to send a heartbeat ping to
 * a specific client
 */
public interface HeartbeatHandler {

    /**
     * Send a heartbeat ping to this client
     *
     * @param serverTime time at which the ping was sent
     */
    void sendHeartbeat(Instant serverTime);
}
