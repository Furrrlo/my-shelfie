package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.ShelfieView;

/**
 * Read-only object which represent a player of an in-progress game, as seen by the server.
 * <p>
 * Compared to the client, the server can see:
 * - the shelfie of any player ({@link #getShelfie()})
 * - the personal goal of any player ({@link #getPersonalGoal()})
 * - the private score of any player at any point ({@link #privateScore()})
 *
 * @see it.polimi.ingsw.model.PlayerView
 */
public interface ServerPlayerView {

    /** Returns the player's nick */
    String getNick();

    /** Return the shelfie as matrix of tiles */
    ShelfieView getShelfie();

    /** Returns personal goal */
    PersonalGoal getPersonalGoal();

    /** Returns true if the player is connected */
    Provider<Boolean> connected();

    /** Returns the player current score, as seen by the other players */
    Provider<Integer> publicScore();

    /** Returns the player current score, as seen by the player itself */
    Provider<Integer> privateScore();
}
