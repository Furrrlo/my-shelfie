package it.polimi.ingsw.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * This is needed to set a connection timeout, in order to detect client disconnections
 */
public class RMITimeoutSocketFactory extends RMISocketFactory {
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        System.out.println("Creating new socket for RMI. Remote IP " + host + ":" + port);
        Socket s = new Socket() {
            @Override
            public synchronized void close() throws IOException {
                System.out.println("closing socket");
                super.close();
            }
        };
        s.connect(new InetSocketAddress(host, port), 500);
        return s;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        System.out.println("Creating new ServerSocket for RMI...");
        return getDefaultSocketFactory().createServerSocket(port);
    }
}
