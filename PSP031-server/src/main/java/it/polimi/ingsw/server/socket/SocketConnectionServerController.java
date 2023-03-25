package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.packets.JoinGamePacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketConnectionServerController implements Runnable {
    private final ExecutorService threadPool;
    private final ServerSocket socketServer;
    private final ServerController controller;

    public SocketConnectionServerController(ServerController controller, int port) throws IOException {
        this.controller = controller;
        this.threadPool = Executors.newFixedThreadPool(10);
        this.socketServer = new ServerSocket(port);
    }

    @Override
    public void run() {
        do {
            try {
                final Socket socket = socketServer.accept();
                System.out.println("[Server] New client connected: " + socket.getRemoteSocketAddress());
                threadPool.submit(() -> {
                    try {
                        final ServerSocketManager socketManager = new ServerSocketManagerImpl(socket);
                        var rec = socketManager.receive(JoinGamePacket.class);
                        JoinGamePacket p = rec.getPacket();
                        System.out.println("[Server] " + p.nick() + " is joining...");
                        socketManager.setNick(p.nick());
                        controller.joinGame(p.nick(), new SocketLobbyServerUpdaterFactory(socketManager, rec),
                                SocketServerGameController::new);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!Thread.interrupted());
    }
}
