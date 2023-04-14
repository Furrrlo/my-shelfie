package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;

import java.io.IOException;

public class Main {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server");

        //If the server has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        //@see https://bugs.openjdk.org/browse/JDK-8042232
        //To work around this, run JVM with the parameter -Djava.rmi.server.hostname=<server address> or uncomment the following line.
        //System.setProperty("java.rmi.server.hostname", "<server address>");
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
