package it.polimi.ingsw.rmi;

import it.polimi.ingsw.controller.GameController;

import java.io.Serializable;
import java.rmi.Remote;

public interface RmiGameController extends Remote {

    class Adapter implements GameController, Serializable {

        private final RmiGameController controller;

        public Adapter(RmiGameController controller) {
            this.controller = controller;
        }
    }
}
