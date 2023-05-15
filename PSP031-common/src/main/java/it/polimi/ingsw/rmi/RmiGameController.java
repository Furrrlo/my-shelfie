package it.polimi.ingsw.rmi;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RmiGameController extends Remote {

    void makeMove(List<BoardCoord> selected, int shelfCol) throws RemoteException;

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
