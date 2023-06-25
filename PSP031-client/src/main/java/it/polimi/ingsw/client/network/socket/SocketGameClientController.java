package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.BoardCoord;
import it.polimi.ingsw.socket.packets.MakeMovePacket;
import it.polimi.ingsw.socket.packets.SendMessagePacket;

import java.io.IOException;
import java.util.List;

/**
 * Socket GameController implementation which forwards requests to the server using packets
 *
 * @see it.polimi.ingsw.controller
 */
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

    @Override
    public void sendMessage(String message, String nickReceivingPlayer) throws DisconnectedException {
        try {
            socketManager.send(new SendMessagePacket(message, nickReceivingPlayer));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
