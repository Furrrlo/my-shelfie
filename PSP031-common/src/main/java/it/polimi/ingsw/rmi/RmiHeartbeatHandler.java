package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.HeartbeatHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;

public interface RmiHeartbeatHandler extends Remote {

    Instant sendHeartbeat(Instant serverTime) throws RemoteException;

    class Adapter extends RmiAdapter implements HeartbeatHandler {

        private final RmiHeartbeatHandler handler;

        public Adapter(RmiHeartbeatHandler handler) {
            this.handler = handler;
        }

        @Override
        public Instant sendHeartbeat(Instant serverTime) throws DisconnectedException {
            return adapt(() -> handler.sendHeartbeat(serverTime));
        }
    }
}
