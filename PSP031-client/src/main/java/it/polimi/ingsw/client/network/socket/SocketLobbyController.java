package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.socket.packets.ReadyPacket;

import java.io.IOException;

public class SocketLobbyController implements LobbyController {

    private final ClientSocketManager socketManager;

    public SocketLobbyController(ClientSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void ready(boolean ready) throws DisconnectedException {
        try {
            socketManager.send(new ReadyPacket(ready));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
