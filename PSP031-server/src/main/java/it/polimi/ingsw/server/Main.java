package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Duration PING_INTERVAL = Duration.of(5, ChronoUnit.SECONDS);
    private static final Duration READ_TIMEOUT = PING_INTERVAL.multipliedBy(2);
    private static final Duration RESPONSE_TIMEOUT = Duration.of(2, ChronoUnit.SECONDS);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {
        // If the server has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        // @see https://bugs.openjdk.org/browse/JDK-8042232
        // To work around this, run JVM with the parameter -Djava.rmi.server.hostname=<server address> or uncomment the following line.
        // System.setProperty("java.rmi.server.hostname", "<server address>");

        System.out.println("Starting server");

        final long pingIntervalMillis = TimeUnit.MILLISECONDS.convert(PING_INTERVAL);
        final long readTimeoutMillis = TimeUnit.MILLISECONDS.convert(READ_TIMEOUT);
        final long responseTimeoutMillis = TimeUnit.MILLISECONDS.convert(RESPONSE_TIMEOUT);

        System.setProperty("sun.rmi.transport.connectionTimeout", "7000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", String.valueOf(readTimeoutMillis));
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(responseTimeoutMillis));

        try (final var controller = new ServerController(pingIntervalMillis, TimeUnit.MILLISECONDS);
             final var ignored1 = RmiConnectionServerController.bind(controller);
             final var ignored2 = new SocketConnectionServerController(
                     controller,
                     1234,
                     readTimeoutMillis, TimeUnit.MILLISECONDS,
                     responseTimeoutMillis, TimeUnit.MILLISECONDS)) {

            System.out.println("Press enter to exit");
            System.in.read();
            System.out.println("Stopping...");
        }
    }
}
