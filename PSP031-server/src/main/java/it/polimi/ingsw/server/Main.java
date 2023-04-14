package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;

import java.io.IOException;

public class Main {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server");
        //System.setProperty("java.rmi.server.useLocalHostname", "true");

        //TODO: without this clients try to connect to the wrong network (192.168.56.1, virtualbox net) (?????)
        // IP address of this server if is not the same of the clients
        System.setProperty("java.rmi.server.hostname", "192.168.178.10");
        System.setProperty("sun.rmi.transport.connectionTimeout", "7000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "7000");
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "500");

        final var controller = new ServerController();
        RmiConnectionServerController.bind(controller);

        //Socket timeout is set in SocketConnectionServerController#acceptConnectionsLoop() with setSoTimeout
        new SocketConnectionServerController(controller, 1234);
        //new SocketConnectionServerController(serverController, 1234, 1, TimeUnit.SECONDS);
    }
}
