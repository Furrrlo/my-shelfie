package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.updater.LobbyClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.packets.CreateGamePacket;
import it.polimi.ingsw.socket.packets.S2CPacket;
import it.polimi.ingsw.socket.packets.UpdateJoinedPlayerPacket;
import it.polimi.ingsw.updater.GameUpdater;

import java.io.IOException;
import java.util.function.Supplier;

public class SocketLobbyClientUpdater extends LobbyClientUpdater implements Supplier<SocketGameClientUpdater> {

    private final ClientSocketManager socketManager;

    public SocketLobbyClientUpdater(Lobby lobby, ClientSocketManager socketManager) {
        super(lobby);
        this.socketManager = socketManager;
    }

    @Override
    public SocketGameClientUpdater get() {
        do {
            try (var ctx = socketManager.receive(S2CPacket.class)) {
                final S2CPacket p = ctx.getPacket();
                if (p instanceof UpdateJoinedPlayerPacket packet) {
                    updateJoinedPlayers(packet.players());
                } else if (p instanceof CreateGamePacket packet) {
                    return (SocketGameClientUpdater) updateGame(packet.game());
                }
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO:
            }
        } while (!Thread.interrupted());
        throw new RuntimeException();
    }

    @Override
    protected GameUpdater createGameUpdater(GameAndController<Game> gameAndController) {
        return new SocketGameClientUpdater(gameAndController.game(), socketManager);
    }
}
