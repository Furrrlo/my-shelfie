package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.BoardCoord;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI remotable service which will be used to implement {@link GameController}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link Adapter} is then used in order to be able to have an actual {@link GameController}
 * interface implementation
 *
 * @see GameController
 * @see Adapter
 */
public interface RmiGameController extends Remote {

    /** RMI redeclaration of {@link GameController#makeMove(List, int)}, check that for docs and details */
    void makeMove(List<BoardCoord> selected, int shelfCol) throws RemoteException;

    /** RMI redeclaration of {@link GameController#sendMessage(String, String)}, check that for docs and details */
    void sendMessage(String message, String nickReceivingPlayer) throws RemoteException;

    class Adapter extends RmiAdapter implements GameController, Serializable {

        private final RmiGameController controller;

        public Adapter(RmiGameController controller) {
            this.controller = controller;
        }

        @Override
        public void makeMove(List<BoardCoord> selected, int shelfCol) throws DisconnectedException {
            adapt(() -> controller.makeMove(selected, shelfCol));
        }

        @Override
        public void sendMessage(String message, String nickReceivingPlayer) throws DisconnectedException {
            adapt(() -> controller.sendMessage(message, nickReceivingPlayer));
        }
    }
}
