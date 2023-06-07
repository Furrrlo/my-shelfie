package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.LobbyController;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI remotable service which will be used to implement {@link LobbyController}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link RmiGameController.Adapter} is then used in order to be able to have an actual {@link LobbyController}
 * interface implementation
 *
 * @see LobbyController
 * @see Adapter
 */
public interface RmiLobbyController extends Remote {

    /** RMI redeclaration of {@link LobbyController#setRequiredPlayers(int)}, check that for docs and details */
    void setRequiredPlayers(int requiredPlayers) throws RemoteException;

    /** RMI redeclaration of {@link LobbyController#ready(boolean)}, check that for docs and details */
    void ready(boolean ready) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyController, Serializable {

        private final RmiLobbyController controller;

        public Adapter(RmiLobbyController controller) {
            this.controller = controller;
        }

        @Override
        public void setRequiredPlayers(int requiredPlayers) throws DisconnectedException {
            adapt(() -> controller.setRequiredPlayers(requiredPlayers));
        }

        @Override
        public void ready(boolean ready) throws DisconnectedException {
            adapt(() -> controller.ready(ready));
        }
    }
}
