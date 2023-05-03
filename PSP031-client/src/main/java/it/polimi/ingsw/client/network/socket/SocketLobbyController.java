package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.socket.packets.ReadyPacket;
import it.polimi.ingsw.socket.packets.RequiredPlayersPacket;

import java.io.IOException;

public class SocketLobbyController implements LobbyController {

    private final ClientSocketManager socketManager;

    public SocketLobbyController(ClientSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void setRequiredPlayers(int requiredPlayers) throws DisconnectedException {
        try {
            socketManager.send(new RequiredPlayersPacket(requiredPlayers));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
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
