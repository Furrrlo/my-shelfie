package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.rmi.RmiHeartbeatHandler;

import java.rmi.RemoteException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class RmiHeartbeatClientHandler implements RmiHeartbeatHandler {

    private final Property<Duration> ping = new SerializableProperty<>(Duration.ZERO);
    private final Clock clock;

    public RmiHeartbeatClientHandler() {
        this(Clock.systemUTC());
    }

    public RmiHeartbeatClientHandler(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Instant sendHeartbeat(Instant serverTime) throws RemoteException {
        final var now = Instant.now(clock);
        ping.set(Duration.between(serverTime, now));
        return now;
    }

    public Property<Duration> ping() {
        return ping;
    }
}
