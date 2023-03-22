package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.SocketLobbyUpdaterFactory;
import it.polimi.ingsw.socket.packets.LobbyPacket;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SocketLobbyServerUpdaterFactory implements SocketLobbyUpdaterFactory {

    private final ObjectOutputStream oos;

    public SocketLobbyServerUpdaterFactory(ObjectOutputStream oos) {
        this.oos = oos;
    }

    @Override
    public LobbyUpdater create(Lobby lobby) throws DisconnectedException {
        try {
            oos.writeObject(new LobbyPacket(lobby));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new SocketLobbyServerUpdater(oos);
    }
}
