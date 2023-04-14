package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;

import java.io.IOException;

public class Main {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        final var controller = new ServerController();
        RmiConnectionServerController.bind(controller);

        //Socket timeout is set in SocketConnectionServerController#acceptConnectionsLoop() with setSoTimeout
        new SocketConnectionServerController(controller, 1234);
        //new SocketConnectionServerController(serverController, 1234, 1, TimeUnit.SECONDS);
    }
}
