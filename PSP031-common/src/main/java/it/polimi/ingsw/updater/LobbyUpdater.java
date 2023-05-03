package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;

import java.util.List;

public interface LobbyUpdater {

    void updateRequiredPlayers(int requiredPlayers) throws DisconnectedException;

    void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException;

    void updatePlayerReady(String nick, boolean ready) throws DisconnectedException;

    GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException;
}
