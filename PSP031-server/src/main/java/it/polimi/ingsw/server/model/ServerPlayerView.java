package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.ShelfieView;

public interface ServerPlayerView {

    /** Returns the player's nick */
    String getNick();

    /** Return the shelfie as matrix of tiles */
    ShelfieView getShelfie();

    /** Returns personal goal */
    PersonalGoal getPersonalGoal();

    /** Returns true if the player is connected */
    Provider<Boolean> connected();

    /** Returns the player current score */
    Provider<Integer> score();
}
