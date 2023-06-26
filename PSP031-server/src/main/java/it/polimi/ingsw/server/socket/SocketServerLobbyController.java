package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.socket.packets.LobbyActionPacket;
import it.polimi.ingsw.socket.packets.ReadyPacket;
import it.polimi.ingsw.socket.packets.RequiredPlayersPacket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;

/**
 * Socket GameController implementation which listens for incoming packets from a socket
 * and calls the corresponding methods on the protocol agnostic {@link LobbyServerController}.
 * <p>
 * This is a per-player object.
 *
 * @see LobbyController
 * @see it.polimi.ingsw.controller
 */
public class SocketServerLobbyController implements LobbyController, Runnable {

    private final ServerSocketManager socketManager;
    private final LobbyServerController controller;
    private final String nick;

    public SocketServerLobbyController(ServerSocketManager socketManager,
                                       LobbyServerController controller,
                                       String nick) {
        this.socketManager = socketManager;
        this.controller = controller;
        this.nick = nick;
    }

    @Override
    public void run() {
        try {
            do {
                try (var ctx = socketManager.receive(LobbyActionPacket.class)) {
                    switch (ctx.getPacket()) {
                        case RequiredPlayersPacket p -> setRequiredPlayers(p.requiredPlayers());
                        case ReadyPacket p -> ready(p.ready());
                    }
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void setRequiredPlayers(int requiredPlayers) {
        controller.setRequiredPlayers(nick, requiredPlayers);
    }

    @Override
    public void ready(boolean ready) {
        controller.ready(nick, ready);
    }
}
