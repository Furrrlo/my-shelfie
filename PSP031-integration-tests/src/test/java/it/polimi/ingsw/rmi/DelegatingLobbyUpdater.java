package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;
import it.polimi.ingsw.model.Game;

import java.util.List;

public class DelegatingLobbyUpdater implements LobbyUpdater {

    private final LobbyUpdater delegate;

    public DelegatingLobbyUpdater(LobbyUpdater delegate) {
        this.delegate = delegate;
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        delegate.updateJoinedPlayers(joinedPlayers);
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        return delegate.updateGame(gameAndController);
    }
}
