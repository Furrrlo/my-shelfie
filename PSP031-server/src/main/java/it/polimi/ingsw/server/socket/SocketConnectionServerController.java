package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.packets.JoinGamePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public void run() {
        do {
            try {
                final Socket socket = socketServer.accept();
                threadPool.submit(() -> {
                    try {
                        final var ois = new ObjectInputStream(socket.getInputStream());
                        final var oos = new ObjectOutputStream(socket.getOutputStream());
                        JoinGamePacket p = (JoinGamePacket) ois.readObject();
                        controller.joinGame(p.nick(), new SocketLobbyServerUpdaterFactory(oos),
                                SocketServerGameController::new);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!Thread.interrupted());
    }
}
