package it.polimi.ingsw.client.network;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.model.LobbyView;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * Client network entrypoint which allows to kick-start communications with the server.
 * <p>
 * Implementing classes provide a way to connect to the server with the given nickname
 * and are in charge of maintaining the communication, updating the model as instructed
 * by the server and cleaning up and notifying via the model once a connection is lost.
 * <p>
 * Entry-points are:
 * - {@link it.polimi.ingsw.client.network.rmi.RmiClientNetManager#connect(String, int, String)}
 * which uses a protocol implemented on top of RMI
 * - {@link it.polimi.ingsw.client.network.socket.SocketClientNetManager#connect(InetSocketAddress, String)}
 * which uses a protocol implemented on top of raw sockets
 */
public interface ClientNetManager extends Closeable {

    /** Returns the server hostname this net manager is communicating with */
    int getPort();

    /** Returns the server port this net manager is communicating with */
    String getHost();

    /** Returns the nickname used to authenticate to the server */
    String getNick();

    /**
     * Ask the server to join a game
     * <p>
     * The server is in charge of choosing the most appropriate lobby to put this player in.
     * 
     * @return the lobby in which the server decided to put the player in
     * @throws Exception implementation-specific connection exceptions
     */
    LobbyAndController<? extends LobbyView> joinGame() throws Exception;

    /**
     * Recreates the same kind of ClientNetManager as {@code this} and attempts to reconnect
     * to the same server with the same nick
     *
     * @return newly reconnected ClientNetManager
     * @throws NickNotValidException if the nick is already in use by another player
     * @throws Exception implementation-specific connection exceptions
     */
    ClientNetManager recreateAndReconnect() throws Exception;

    /** Factory class used to create new instances of {@link ClientNetManager} */
    interface Factory {

        /** Connects to the given server and creates a new {@link ClientNetManager} */
        ClientNetManager create(String host, int port, String nick) throws Exception;
    }
}
