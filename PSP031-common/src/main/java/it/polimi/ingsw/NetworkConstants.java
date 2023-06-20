package it.polimi.ingsw;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/** Network protocol constants to use in both client and server */
public interface NetworkConstants {
    /** Interval between successive heartbeat pings */
    Duration PING_INTERVAL = Duration.of(5, ChronoUnit.SECONDS);
    /** Timeout after which, if nothing was read, the connection should be considered dead */
    Duration READ_TIMEOUT = PING_INTERVAL.multipliedBy(2);
    /** Timeout after which, if a response to a packet was not received, the connection should be considered dead */
    Duration RESPONSE_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
}
