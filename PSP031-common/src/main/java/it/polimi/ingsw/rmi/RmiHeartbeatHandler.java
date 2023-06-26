package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.HeartbeatHandler;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * RMI remotable service which will be used to implement {@link HeartbeatHandler}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link Adapter} is then used in order to be able to have an actual {@link HeartbeatHandler}
 * interface implementation
 *
 * @see HeartbeatHandler
 * @see Adapter
 */
public interface RmiHeartbeatHandler extends Remote {

    /** RMI redeclaration of {@link HeartbeatHandler#sendHeartbeat(Instant)}, check that for docs and details */
    Instant sendHeartbeat(Instant serverTime) throws RemoteException;

    class Adapter extends RmiAdapter implements HeartbeatHandler, Closeable {

        private final RmiHeartbeatHandler handler;
        private final Consumer<Throwable> pingFailed;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        public Adapter(RmiHeartbeatHandler handler, Consumer<Throwable> pingFailed) {
            this.handler = handler;
            this.pingFailed = pingFailed;
        }

        @Override
        public void sendHeartbeat(Instant serverTime) {
            if (!closed.get()) {
                try {
                    adapt(() -> handler.sendHeartbeat(serverTime));
                } catch (DisconnectedException e) {
                    if (!closed.get())
                        pingFailed.accept(e);
                }
            }
        }

        @Override
        public void close() {
            closed.set(true);
        }
    }
}
