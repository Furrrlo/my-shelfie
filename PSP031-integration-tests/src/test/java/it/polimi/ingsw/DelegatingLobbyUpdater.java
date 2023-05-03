package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.util.List;

public class DelegatingLobbyUpdater implements LobbyUpdater {

    private final LobbyUpdater delegate;

    public DelegatingLobbyUpdater(LobbyUpdater delegate) {
        this.delegate = delegate;
    }

    @Override
    public void updateRequiredPlayers(int requiredPlayers) throws DisconnectedException {
        delegate.updateRequiredPlayers(requiredPlayers);
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
        delegate.updateJoinedPlayers(joinedPlayers);
    }

    @Override
    public void updatePlayerReady(String nick, boolean ready) throws DisconnectedException {
        delegate.updatePlayerReady(nick, ready);
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
        return delegate.updateGame(gameAndController);
    }
}
