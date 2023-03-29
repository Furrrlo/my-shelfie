package it.polimi.ingsw.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

public class RMIPortCapturingServerSocketFactory implements RMIServerSocketFactory {

    private volatile int firstCapturedPort = -1;

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        var serverSocket = new ServerSocket(port);
        if (firstCapturedPort == -1)
            firstCapturedPort = serverSocket.getLocalPort();
        return serverSocket;
    }

    public int getFirstCapturedPort() {
        return firstCapturedPort;
    }
}
