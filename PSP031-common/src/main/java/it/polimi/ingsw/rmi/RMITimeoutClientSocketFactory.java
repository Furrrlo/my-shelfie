package it.polimi.ingsw.rmi;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * This is needed to set a connection timeout, in order to detect client disconnections
 */
public class RMITimeoutClientSocketFactory implements RMIClientSocketFactory, Serializable {

    private transient final Supplier<Socket> socketFactory;
    protected final long connectTimeoutMillis;

    public RMITimeoutClientSocketFactory(Supplier<Socket> socketFactory, long connectTimeout, TimeUnit connectTimeoutUnit) {
        this.socketFactory = socketFactory;
        this.connectTimeoutMillis = connectTimeoutUnit.toMillis(connectTimeout);
    }

    public RMITimeoutClientSocketFactory(long connectTimeout, TimeUnit connectTimeoutUnit) {
        this(Socket::new, connectTimeout, connectTimeoutUnit);
    }

    @Serial
    private Object readResolve() throws ObjectStreamException {
        return new RMITimeoutClientSocketFactory(connectTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        System.out.println("Creating new socket for RMI. Remote IP " + host + ":" + port);
        Socket s = socketFactory.get();
        s.connect(new InetSocketAddress(host, port), (int) connectTimeoutMillis);
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RMITimeoutClientSocketFactory that))
            return false;
        return getClass().equals(that.getClass()) && connectTimeoutMillis == that.connectTimeoutMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), connectTimeoutMillis);
    }

    @Override
    public String toString() {
        return "RMITimeoutSocketFactory{" +
                "connectTimeoutMillis=" + connectTimeoutMillis +
                "} " + super.toString();
    }
}
