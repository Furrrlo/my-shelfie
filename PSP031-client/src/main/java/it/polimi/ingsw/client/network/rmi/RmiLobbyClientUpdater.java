package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.updater.LobbyClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.RmiGameUpdater;
import it.polimi.ingsw.rmi.RmiLobbyUpdater;
import it.polimi.ingsw.rmi.UnicastRemoteObjects;
import it.polimi.ingsw.updater.GameUpdater;

import java.rmi.RemoteException;

class RmiLobbyClientUpdater extends LobbyClientUpdater implements RmiLobbyUpdater {

    private final UnicastRemoteObjects.Exporter unicastRemoteObjects;
    private final LobbyAndController<Lobby> lobbyAndController;

    public RmiLobbyClientUpdater(UnicastRemoteObjects.Exporter unicastRemoteObjects,
                                 LobbyAndController<Lobby> lobbyAndController) {
        super(lobbyAndController.lobby());
        this.unicastRemoteObjects = unicastRemoteObjects;
        this.lobbyAndController = lobbyAndController;
    }

    LobbyAndController<Lobby> getLobbyAndController() {
        return lobbyAndController;
    }

    @Override
    protected GameUpdater createGameUpdater(GameAndController<Game> gameAndController) {
        try {
            return new RmiGameUpdater.Adapter(unicastRemoteObjects.export(
                    new RmiGameClientUpdater(gameAndController.game()), 0));
        } catch (RemoteException e) {
            throw new IllegalStateException("Unexpectedly failed to export RmiGameClientUpdater", e);
        }
    }
}
