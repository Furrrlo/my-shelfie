package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.socket.packets.HeartbeatPing;
import it.polimi.ingsw.socket.packets.HeartbeatPong;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Socket implementation of the heartbeat which is in charge of answering the server's ping request */
class SocketClientHeartbeatHandler implements Runnable {

    private final ClientSocketManager socketManager;
    private final Clock clock;
    private final Property<Duration> ping = new SerializableProperty<>(Duration.ZERO);

    public SocketClientHeartbeatHandler(ClientSocketManager socketManager) {
        this(socketManager, Clock.systemUTC());
    }

    public SocketClientHeartbeatHandler(ClientSocketManager socketManager, Clock clock) {
        this.socketManager = socketManager;
        this.clock = clock;
    }

    @Override
    public void run() {
        try {
            do {
                var pingCtx = socketManager.receive(HeartbeatPing.class);
                var now = Instant.now(clock);
                ping.set(Duration.between(pingCtx.getPacket().serverTime(), now));
                pingCtx.reply(new HeartbeatPong(now));
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // We got interrupted, normal flow
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public Property<Duration> ping() {
        return ping;
    }
}
