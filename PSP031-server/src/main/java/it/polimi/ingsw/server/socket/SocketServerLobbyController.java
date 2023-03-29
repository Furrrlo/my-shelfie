package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.socket.packets.ReadyPacket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;

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
                try (var ctx = socketManager.receive(ReadyPacket.class)) {
                    ready(ctx.getPacket().ready());
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void ready(boolean ready) {
        controller.ready(nick, ready);
    }
}
