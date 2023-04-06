package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.HeartbeatHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.function.Consumer;

public interface RmiHeartbeatHandler extends Remote {

    Instant sendHeartbeat(Instant serverTime) throws RemoteException;

    class Adapter extends RmiAdapter implements HeartbeatHandler {

        private final RmiHeartbeatHandler handler;
        private final Consumer<Throwable> pingFailed;

        public Adapter(RmiHeartbeatHandler handler, Consumer<Throwable> pingFailed) {
            this.handler = handler;
            this.pingFailed = pingFailed;
        }

        @Override
        public void sendHeartbeat(Instant serverTime) {
            try {
                adapt(() -> handler.sendHeartbeat(serverTime));
            } catch (DisconnectedException e) {
                pingFailed.accept(e);
            }
        }
    }
}
