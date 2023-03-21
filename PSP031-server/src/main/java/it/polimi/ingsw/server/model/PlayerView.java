package it.polimi.ingsw.server.model;


public interface PlayerView {
    /**
     * @return player's nick
     */
    String getNick();

    /**
     * @return shelfie as matrix of tiles
     */
    ShelfieView getShelfie();

    /**
     * @return personal Goal as matrix of tiles
     */
    PersonalGoalView getPersonalGoal();
}
