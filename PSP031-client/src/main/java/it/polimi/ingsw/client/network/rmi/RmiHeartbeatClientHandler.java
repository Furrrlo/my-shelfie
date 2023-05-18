package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.rmi.RmiHeartbeatHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.rmi.RemoteException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class RmiHeartbeatClientHandler implements RmiHeartbeatHandler, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiHeartbeatClientHandler.class);

    private final Clock clock;
    private final Runnable onDisconnect;
    private final ScheduledExecutorService heartbeatExecutor;
    private final AtomicBoolean closed = new AtomicBoolean();

    private final Property<Duration> ping = new SerializableProperty<>(Duration.ZERO);
    private volatile Instant lastPing;

    public RmiHeartbeatClientHandler(Runnable onDisconnect) {
        this(Clock.systemUTC(), onDisconnect);
    }

    public RmiHeartbeatClientHandler(Clock clock, Runnable onDisconnect) {
        this.clock = clock;
        this.onDisconnect = onDisconnect;
        this.lastPing = Instant.EPOCH;
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform()
                .name("ClientRMI-heartbeat-scheduler-thread")
                .factory());
    }

    @Override
    public Instant sendHeartbeat(Instant serverTime) throws RemoteException {
        lastPing = Instant.now(clock);
        ping.set(Duration.between(serverTime, lastPing));
        return lastPing;
    }

    public void start() {
        this.heartbeatExecutor.scheduleAtFixedRate(this::checkLastPing, 10, 2, TimeUnit.SECONDS);
    }

    private void checkLastPing() {
        if (!closed.get() && lastPing.plus(10, ChronoUnit.SECONDS).isBefore(Instant.now(clock))) {
            LOGGER.error("RMI: connection lost");

            //TODO: find a better way to get the lobby (?)
            try {
                onDisconnect.run();
            } catch (NullPointerException e) {
                LOGGER.warn("Failed to create the lobby ", e);
            }
            close();
        }
    }

    public Property<Duration> ping() {
        return ping;
    }

    @Override
    public void close() {
        if (closed.getAndSet(true))
            return;

        heartbeatExecutor.shutdown();
    }
}
