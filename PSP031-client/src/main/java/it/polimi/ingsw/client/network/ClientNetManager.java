package it.polimi.ingsw.client.network;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.model.LobbyView;

import java.io.Closeable;
import java.io.IOException;

public interface ClientNetManager extends Closeable {

    String getHost();

    int getPort();

    String getNick();

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

    @Override
    default void close() throws IOException {
        // TODO: override and implement this
    }

    interface Factory {

        ClientNetManager create(String host, int port, String nick) throws Exception;
    }
}
