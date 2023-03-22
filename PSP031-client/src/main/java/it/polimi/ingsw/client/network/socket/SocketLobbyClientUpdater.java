package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.SocketLobbyUpdater;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.S2CPacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.updater.GameUpdater;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class SocketLobbyClientUpdater implements SocketLobbyUpdater, Runnable {

    private final Lobby lobby;
    private final ObjectInputStream ois;

    public SocketLobbyClientUpdater(Lobby lobby, ObjectInputStream ois) {
        this.lobby = lobby;
        this.ois = ois;
    }

    @Override
    public void run() {
        do {
            try {
                final S2CPacket p = (S2CPacket) ois.readObject();
                if (p instanceof UpdateJoinedPlayerPacket)
                    updateJoinedPlayers(((UpdateJoinedPlayerPacket) p).players());
                else if (p instanceof CreateGamePacket) {
                    updateGame(((CreateGamePacket) p).game());
                    break;
                }
            } catch (IOException | ClassNotFoundException | DisconnectedException e) {
                throw new RuntimeException(e); //TODO:
            }
        } while (!Thread.interrupted());
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        lobby.joinedPlayers().set(joinedPlayers);
    }

    @Override
    @SuppressWarnings("NullAway") //TODO: remove this line
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        lobby.game().set(gameAndController);
        new Thread(new SocketGameClientUpdater(gameAndController.game(), ois)).start();
        return null; //TODO: ?
    }

}
