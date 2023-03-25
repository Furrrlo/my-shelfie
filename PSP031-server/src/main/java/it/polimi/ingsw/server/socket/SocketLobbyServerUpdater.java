package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.SocketLobbyUpdater;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.updater.GameUpdater;

import java.io.IOException;
import java.util.List;

public class SocketLobbyServerUpdater implements SocketLobbyUpdater {
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
            socketManager.send(new UpdateJoinedPlayerPacket(joinedPlayers), C2SAckPacket.class);
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: ???
        }
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        try {
            socketManager.send(new CreateGamePacket(gameAndController), C2SAckPacket.class);
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: ???
        }
        return new SocketServerGameUpdater(socketManager);
    }
}
