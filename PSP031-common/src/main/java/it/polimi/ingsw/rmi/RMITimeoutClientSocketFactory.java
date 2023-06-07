package it.polimi.ingsw.rmi;

import it.polimi.ingsw.NetworkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Objects;

/**
 * Client socket factory which respects the "sun.rmi.transport.tcp.readTimeout" system property
 * in order to properly timeout on socket connection
 */
public class RMITimeoutClientSocketFactory implements RMIClientSocketFactory, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RMITimeoutClientSocketFactory.class);

    protected static final long CONNECT_TIMEOUT_MILLIS = Long.getLong(
            "sun.rmi.transport.tcp.readTimeout",
            NetworkConstants.READ_TIMEOUT.toMillis());

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        LOGGER.trace("Creating new socket for RMI. Remote IP {}:{}", host, port);
        Socket s = doCreateNonConnectedSocket();
        s.setTcpNoDelay(true);
        s.connect(new InetSocketAddress(host, port), (int) CONNECT_TIMEOUT_MILLIS);
        return s;
    }

    protected Socket doCreateNonConnectedSocket() {
        return new Socket();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RMITimeoutClientSocketFactory that))
            return false;
        return getClass().equals(that.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }

    @Override
    public String toString() {
        return "RMITimeoutSocketFactory{" +
                "connectTimeoutMillis=" + CONNECT_TIMEOUT_MILLIS +
                "} " + super.toString();
    }
}
