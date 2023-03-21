package it.polimi.ingsw.server.model;


public interface PersonalGoalView {
    /**
     * @param r defines row of personalGoal
     * @param c defines column of personalGoal
     * @return tile of personal Goal
     */
    Tile get(int r, int c);
}
