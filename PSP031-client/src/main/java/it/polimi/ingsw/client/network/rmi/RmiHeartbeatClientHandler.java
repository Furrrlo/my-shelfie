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
    private final long readTimeoutMillis;
    private final ScheduledExecutorService heartbeatExecutor;
    private final AtomicBoolean closed = new AtomicBoolean();

    private final Property<Duration> ping = new SerializableProperty<>(Duration.ZERO);
    private volatile Instant lastPing;

    public RmiHeartbeatClientHandler(long readTimeout, TimeUnit readTimeoutUnit, Runnable onDisconnect) {
        this(Clock.systemUTC(), readTimeout, readTimeoutUnit, onDisconnect);
    }

    public RmiHeartbeatClientHandler(Clock clock,
                                     long readTimeout,
                                     TimeUnit readTimeoutUnit,
                                     Runnable onDisconnect) {
        this.clock = clock;
        this.readTimeoutMillis = readTimeoutUnit.toMillis(readTimeout);
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
        this.heartbeatExecutor.scheduleAtFixedRate(
                this::checkLastPing,
                // The first needs to happen after we received the first ping, so wait a while
                readTimeoutMillis,
                // Check quite often just to make sure, it's not an expensive check anyway
                readTimeoutMillis / 5,
                TimeUnit.MILLISECONDS);
    }

    private void checkLastPing() {
        if (!closed.get() && lastPing.plus(readTimeoutMillis, ChronoUnit.MILLIS).isBefore(Instant.now(clock))) {
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
