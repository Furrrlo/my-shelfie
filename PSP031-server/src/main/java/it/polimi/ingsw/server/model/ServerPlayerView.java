package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

public interface ServerPlayerView {

    /**
     * @return player's nick
     */
    String getNick();

    /**
     * @return shelfie as matrix of tiles
     */
    ShelfieView getShelfie();

    /**
     * @return personal goal
     */
    PersonalGoal getPersonalGoal();
}
