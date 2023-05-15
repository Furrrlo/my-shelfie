package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.socket.packets.MakeMovePacket;
import it.polimi.ingsw.socket.packets.SendMessagePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.List;

public class SocketServerGameController implements GameController, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServerGameController.class);

    private final ServerSocketManager socketManager;
    private final ServerPlayer player;
    private final GameServerController controller;

    public SocketServerGameController(ServerSocketManager socketManager,
                                      ServerPlayer serverPlayer,
                                      GameServerController controller) {
        this.socketManager = socketManager;
        player = serverPlayer;
        this.controller = controller;
    }

    @Override
    public void run() {
        LOGGER.info("[Server] Started game controller");
        try {
            do {
                try (var ctx = socketManager.receive(MakeMovePacket.class)) {
                    final MakeMovePacket p = ctx.getPacket();
                    makeMove(p.selected(), p.shelfCol());
                }
                try (var ctx = socketManager.receive(SendMessagePacket.class)) {
                    final SendMessagePacket p = ctx.getPacket();
                    sendMessage(p.message(), p.nickReceivingPlayer());
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void makeMove(List<BoardCoord> selected, int shelfCol) {
        controller.makeMove(player, selected, shelfCol);
    }

    @Override
    public void sendMessage(String message, String nickReceivingPlayer) {
        controller.sendMessage(player.getNick(), message, nickReceivingPlayer);
    }
}
