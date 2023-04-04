package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.ShelfieView;

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

    Provider<Boolean> connected();

    Provider<Integer> score();
}
