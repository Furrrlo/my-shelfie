package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.RmiGameUpdater;
import it.polimi.ingsw.rmi.RmiLobbyUpdater;
import it.polimi.ingsw.rmi.UnicastRemoteObjects;

import java.rmi.RemoteException;
import java.util.List;

class RmiLobbyClientUpdater implements RmiLobbyUpdater {

    private final Lobby lobby;

    public RmiLobbyClientUpdater(Lobby lobby) {
        this.lobby = lobby;
    }

    Lobby getGameCreationState() {
        return lobby;
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) {
        getGameCreationState().joinedPlayers().set(joinedPlayers);
    }

    @Override
    public RmiGameUpdater updateGame(GameAndController<Game> gameAndController) {
        try {
            final RmiGameUpdater res = UnicastRemoteObjects.export(
                    new RmiGameClientUpdater(gameAndController.game()), 0);
            getGameCreationState().game().set(gameAndController);
            return res;
        } catch (RemoteException e) {
            throw new IllegalStateException("Unexpectedly failed to export RmiGameClientUpdater", e);
        }
    }
}
