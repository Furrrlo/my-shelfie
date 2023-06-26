package it.polimi.ingsw.server;

import it.polimi.ingsw.NetworkConstants;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.rmi.RmiConnectionServerController;
import it.polimi.ingsw.server.socket.SocketConnectionServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/** Server main class */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final Duration PING_INTERVAL = NetworkConstants.PING_INTERVAL;
    private static final Duration READ_TIMEOUT = NetworkConstants.READ_TIMEOUT;
    private static final Duration RESPONSE_TIMEOUT = NetworkConstants.RESPONSE_TIMEOUT;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {
        // If the server has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        // @see https://bugs.openjdk.org/browse/JDK-8042232
        // To work around this, run JVM with the parameter -Djava.rmi.server.hostname=<server address> or uncomment the following line.
        // System.setProperty("java.rmi.server.hostname", "<server address>");

        // add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LOGGER.info("Starting server");

        final long pingIntervalMillis = TimeUnit.MILLISECONDS.convert(PING_INTERVAL);
        final long readTimeoutMillis = TimeUnit.MILLISECONDS.convert(READ_TIMEOUT);
        final long responseTimeoutMillis = TimeUnit.MILLISECONDS.convert(RESPONSE_TIMEOUT);

        System.setProperty("sun.rmi.transport.connectionTimeout", "7000");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", String.valueOf(readTimeoutMillis));
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(responseTimeoutMillis));
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", String.valueOf(responseTimeoutMillis));

        try (final var controller = new ServerController(pingIntervalMillis, TimeUnit.MILLISECONDS);
             final var ignored1 = RmiConnectionServerController.bind(controller);
             final var ignored2 = new SocketConnectionServerController(
                     controller,
                     1234,
                     readTimeoutMillis, TimeUnit.MILLISECONDS,
                     responseTimeoutMillis, TimeUnit.MILLISECONDS)) {

            LOGGER.info("Press enter to exit");
            System.in.read();
            LOGGER.info("Stopping...");
        }
    }
}
