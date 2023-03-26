package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.rmi.RmiGameUpdater;

class RmiGameClientUpdater extends GameClientUpdater implements RmiGameUpdater {

    public RmiGameClientUpdater(Game game) {
        super(game);
    }
}
