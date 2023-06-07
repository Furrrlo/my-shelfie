package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;

import java.util.List;

/** Object in charge of sending updates related to the {@link it.polimi.ingsw.model.Lobby} model class */
public interface LobbyUpdater {

    /**
     * Set the number of required players the lobby creator set
     *
     * @param requiredPlayers num of required players
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateRequiredPlayers(int requiredPlayers) throws DisconnectedException;

    /**
     * Set the list of players who have joined
     *
     * @param joinedPlayers list of joined players
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException;

    /**
     * Set whether the specified player is ready
     *
     * @param nick nick of the player whose ready state is changed
     * @param ready true if the player is ready
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updatePlayerReady(String nick, boolean ready) throws DisconnectedException;

    /**
     * Set the current game instance when the game starts/end
     *
     * @param gameAndController game modifiable model and controller to be used by the client
     * @return an instance of the updater which will be used to update the game
     * @throws DisconnectedException if the client disconnects during the update
     */
    GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException;
}
