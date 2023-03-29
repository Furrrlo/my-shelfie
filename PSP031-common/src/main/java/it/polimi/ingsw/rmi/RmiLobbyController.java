package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.LobbyController;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiLobbyController extends Remote {

    void ready(boolean ready) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyController, Serializable {

        private final RmiLobbyController controller;

        public Adapter(RmiLobbyController controller) {
            this.controller = controller;
        }

        @Override
        public void ready(boolean ready) throws DisconnectedException {
            adapt(() -> controller.ready(ready));
        }
    }
}
