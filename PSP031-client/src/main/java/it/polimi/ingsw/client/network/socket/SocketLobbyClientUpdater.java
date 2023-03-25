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
import java.util.List;

public class SocketLobbyClientUpdater implements SocketLobbyUpdater, Runnable {

    private final Lobby lobby;
    private final ClientSocketManager socketManager;

    public SocketLobbyClientUpdater(Lobby lobby, ClientSocketManager socketManager) {
        this.lobby = lobby;
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        do {
            try (var ctx = socketManager.receive(S2CPacket.class)) {
                final S2CPacket p = ctx.getPacket();
                if (p instanceof UpdateJoinedPlayerPacket)
                    updateJoinedPlayers(((UpdateJoinedPlayerPacket) p).players());
                else if (p instanceof CreateGamePacket) {
                    updateGame(((CreateGamePacket) p).game());
                    break;
                }
            } catch (IOException | DisconnectedException e) {
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
        new Thread(new SocketGameClientUpdater(gameAndController.game(), socketManager)).start();
        return null; //TODO: ?
    }

}
