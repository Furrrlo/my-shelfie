package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.socket.packets.HeartbeatPing;
import it.polimi.ingsw.socket.packets.HeartbeatPong;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Consumer;

class SocketHeartbeatHandler implements HeartbeatHandler {

    private final ServerSocketManager socketManager;
    private final Consumer<Throwable> close;

    public SocketHeartbeatHandler(ServerSocketManager socketManager, Consumer<Throwable> pingFailed) {
        this.socketManager = socketManager;
        this.close = pingFailed;
    }

    @Override
    @SuppressWarnings("EmptyTryBlock")
    public void sendHeartbeat(Instant serverTime) {
        try (var ignored = socketManager.send(new HeartbeatPing(serverTime), HeartbeatPong.class)) {
            // Received pong, we are happy
        } catch (IOException e) {
            close.accept(e);
        }
    }
}
