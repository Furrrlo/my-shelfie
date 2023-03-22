package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyView;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import it.polimi.ingsw.socket.packets.LobbyPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketClientNetManager implements ClientNetManager {
    private final Socket socket;
    private final SocketAddress serverAddress;

    public SocketClientNetManager(SocketAddress address) {
        socket = new Socket();
        serverAddress = address;
    }

    @Override
    public LobbyView joinGame(String nick) throws IOException, ClassNotFoundException {
        socket.connect(serverAddress);
        final var oos = new ObjectOutputStream(socket.getOutputStream());
        final var ois = new ObjectInputStream(socket.getInputStream());
        oos.writeObject(new JoinGamePacket(nick));
        Lobby lobby = ((LobbyPacket) ois.readObject()).lobby();
        new Thread(new SocketLobbyClientUpdater(lobby, ois)).start();
        return lobby;
    }
}
