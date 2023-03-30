package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.socket.packets.UpdatePlayerReadyPacket;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.io.IOException;
import java.util.List;

public class SocketLobbyServerUpdater implements LobbyUpdater {
    //TODO: serve?
    //private final Lobby lobby;
    private final ServerSocketManager socketManager;

    public SocketLobbyServerUpdater(ServerSocketManager socketManager) {
        //this.lobby = lobby;
        this.socketManager = socketManager;
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        try {
            socketManager.send(new UpdateJoinedPlayerPacket(joinedPlayers));
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: ???
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
            throw new RuntimeException(e); //TODO: ???
        }
        return new SocketServerGameUpdater(socketManager);
    }
}
