package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.BoardCoord;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.socket.SocketManager;
import it.polimi.ingsw.socket.packets.*;
import it.polimi.ingsw.utils.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Socket GameController implementation which listens for incoming packets from a socket
 * and calls the corresponding methods on the protocol agnostic {@link GameServerController}.
 * <p>
 * This is a per-player object.
 *
 * @see GameController
 * @see it.polimi.ingsw.controller
 */
public class SocketServerGameController implements GameController, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServerGameController.class);

    private final ExecutorService threadPool;
    private final ServerSocketManager socketManager;
    private final ServerPlayer player;
    private final GameServerController controller;

    public SocketServerGameController(ExecutorService threadPool,
                                      ServerSocketManager socketManager,
                                      ServerPlayer serverPlayer,
                                      GameServerController controller) {
        this.socketManager = socketManager;
        this.threadPool = threadPool;
        this.player = serverPlayer;
        this.controller = controller;
    }

    @Override
    public void run() {
        LOGGER.info("[Server] Started game controller");
        try {
            do {
                try (var ctx = socketManager.receive(GameActionPacket.class)) {
                    switch (ctx.getPacket()) {
                        case MakeMovePacket p -> doMakeMove(ctx, p.selected(), p.shelfCol());
                        case SendMessagePacket p -> sendMessage(p.message(), p.nickReceivingPlayer());
                    }
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void doMakeMove(SocketManager.PacketReplyContext<C2SAckPacket, S2CAckPacket, GameActionPacket> ctx,
                            List<BoardCoord> selected,
                            int shelfCol)
            throws IOException {
        // If we are making the last move of the game (the move that will cause enGame to be set to true),
        // that will cause the onGameOver hook to run and interrupt this thread, causing issues with subsequent
        // updates that will happen in this same thread (updating final score and endGame for other players and
        // disconnecting them).
        // We do it in a complete different thread so this won't be an issue, and we wait for it uninterruptibly
        // so that we won't stop waiting for it
        try {
            ThreadPools.getUninterruptibly(threadPool.submit(() -> {
                makeMove(selected, shelfCol);
                // Also send the ack from here, so it's done uninterruptibly
                try {
                    ctx.ack();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }));
        } catch (ExecutionException e) {
            // Unwrap IOExceptions
            if (e.getCause() instanceof UncheckedIOException uio && uio.getCause() instanceof IOException io) {
                io.addSuppressed(new Exception("Called from here", e));
                throw io;
            }

            if (e.getCause() instanceof RuntimeException re) {
                re.addSuppressed(new Exception("Called from here", e));
                throw re;
            }
            if (e.getCause() instanceof Error err) {
                err.addSuppressed(new Exception("Called from here", e));
                throw err;
            }

            throw new RuntimeException("Failed to invoke makeMove uninterruptibly", e.getCause());
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
