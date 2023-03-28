package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.socket.packets.MakeMovePacket;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class SocketServerGameController implements GameController {
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

    public void run() {

        try {
            do {
                try (var ctx = socketManager.receive(MakeMovePacket.class)) {
                    final MakeMovePacket p = ctx.getPacket();
                    makeMove(p.selected(), p.shelfCol());
                }
            } while (true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void makeMove(List<BoardCoord> selected, int shelfCol) {
        controller.makeMove(player, selected, shelfCol);
    }
}
