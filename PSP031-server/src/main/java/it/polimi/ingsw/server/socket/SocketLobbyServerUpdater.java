package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.socket.packets.UpdatePlayerReadyPacket;
import it.polimi.ingsw.socket.packets.UpdateRequiredPlayersPacket;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.io.IOException;
import java.util.List;

/**
 * Socket LobbyUpdater implementation which forwards requests to the client using packets
 *
 * @see it.polimi.ingsw.controller
 */
public class SocketLobbyServerUpdater implements LobbyUpdater {
    private final ServerSocketManager socketManager;

    public SocketLobbyServerUpdater(ServerSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void updateRequiredPlayers(int requiredPlayers) throws DisconnectedException {
        try {
            socketManager.send(new UpdateRequiredPlayersPacket(requiredPlayers));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        try {
            socketManager.send(new UpdateJoinedPlayerPacket(joinedPlayers));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updatePlayerReady(String nick, boolean ready) throws DisconnectedException {
        try {
            socketManager.send(new UpdatePlayerReadyPacket(nick, ready));
        } catch (IOException ex) {
            throw new DisconnectedException(ex);
        }
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        try {
            socketManager.send(new CreateGamePacket(gameAndController.game()));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
        return new SocketServerGameUpdater(socketManager);
    }
}
