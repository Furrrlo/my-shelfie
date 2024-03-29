package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.socket.packets.HeartbeatPing;
import it.polimi.ingsw.socket.packets.HeartbeatPong;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * Socket based heartbeat handler which is able to deliver heartbeat pings
 * to a specific client.
 * <p>
 * This is a per-socket object.
 *
 * @see HeartbeatHandler
 */
class SocketHeartbeatHandler implements HeartbeatHandler, Closeable {

    private final ServerSocketManager socketManager;
    private final Consumer<Throwable> close;
    private volatile boolean isClosed;

    public SocketHeartbeatHandler(ServerSocketManager socketManager, Consumer<Throwable> pingFailed) {
        this.socketManager = socketManager;
        this.close = pingFailed;
    }

    @Override
    @SuppressWarnings("EmptyTryBlock")
    public void sendHeartbeat(Instant serverTime) {
        if (isClosed)
            return;

        try (var ignored = socketManager.send(new HeartbeatPing(serverTime), HeartbeatPong.class)) {
            // Received pong, we are happy
        } catch (IOException e) {
            close.accept(e);
        }
    }

    @Override
    public void close() {
        isClosed = true;
    }
}
