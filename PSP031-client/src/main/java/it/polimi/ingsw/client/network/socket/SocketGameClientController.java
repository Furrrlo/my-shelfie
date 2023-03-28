package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.socket.packets.MakeMovePacket;

import java.io.IOException;
import java.util.List;

public class SocketGameClientController implements GameController {
    private final ClientSocketManager socketManager;

    public SocketGameClientController(ClientSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void makeMove(List<BoardCoord> selected, int shelfCol) throws DisconnectedException {
        try {
            socketManager.send(new MakeMovePacket(selected, shelfCol));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
