package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.S2CPacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class SocketLobbyClientUpdater implements LobbyUpdater, Supplier<SocketGameClientUpdater> {

    private final Lobby lobby;
    private final ClientSocketManager socketManager;

    public SocketLobbyClientUpdater(Lobby lobby, ClientSocketManager socketManager) {
        this.lobby = lobby;
        this.socketManager = socketManager;
    }

    @Override
    public SocketGameClientUpdater get() {
        do {
            try (var ctx = socketManager.receive(S2CPacket.class)) {
                final S2CPacket p = ctx.getPacket();
                if (p instanceof UpdateJoinedPlayerPacket)
                    updateJoinedPlayers(((UpdateJoinedPlayerPacket) p).players());
                else if (p instanceof CreateGamePacket) {
                    return updateGame(((CreateGamePacket) p).game());
                }
            } catch (IOException | DisconnectedException e) {
                throw new RuntimeException(e); //TODO:
            }
        } while (!Thread.interrupted());
        throw new RuntimeException();
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        lobby.joinedPlayers().set(joinedPlayers);
    }

    @Override
    public SocketGameClientUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        lobby.game().set(gameAndController);
        return new SocketGameClientUpdater(gameAndController.game(), socketManager);
    }

}
