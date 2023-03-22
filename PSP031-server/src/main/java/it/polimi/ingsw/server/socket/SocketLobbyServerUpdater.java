package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.SocketLobbyUpdater;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.updater.GameUpdater;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class SocketLobbyServerUpdater implements SocketLobbyUpdater {
    //TODO: serve?
    //private final Lobby lobby;
    private final ObjectOutputStream oos;

    public SocketLobbyServerUpdater(/* Lobby lobby, */ ObjectOutputStream oos) {
        //this.lobby = lobby;
        this.oos = oos;
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        try {
            oos.writeObject(new UpdateJoinedPlayerPacket(joinedPlayers));
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: ???
        }
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        try {
            oos.writeObject(new CreateGamePacket(gameAndController));
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: ???
        }
        return new SocketServerGameUpdater(oos);
    }
}
