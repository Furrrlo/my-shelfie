package it.polimi.ingsw.client.updater;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.util.List;

public abstract class LobbyClientUpdater implements LobbyUpdater {

    protected final Lobby lobby;

    public LobbyClientUpdater(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) {
        lobby.joinedPlayers().set(joinedPlayers);
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) {
        final GameUpdater res = createGameUpdater(gameAndController);
        lobby.game().set(gameAndController);
        return res;
    }

    protected abstract GameUpdater createGameUpdater(GameAndController<Game> gameAndController);
}
