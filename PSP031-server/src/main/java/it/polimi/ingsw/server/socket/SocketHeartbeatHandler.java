package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.HeartbeatHandler;
import it.polimi.ingsw.socket.packets.HeartbeatPing;
import it.polimi.ingsw.socket.packets.HeartbeatPong;

import java.io.IOException;
import java.time.Instant;

class SocketHeartbeatHandler implements HeartbeatHandler {

    private final ServerSocketManager socketManager;

    public SocketHeartbeatHandler(ServerSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public Instant sendHeartbeat(Instant serverTime) throws DisconnectedException {
        try (var pong = socketManager.send(new HeartbeatPing(serverTime), HeartbeatPong.class)) {
            return pong.getPacket().serverTime();
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
