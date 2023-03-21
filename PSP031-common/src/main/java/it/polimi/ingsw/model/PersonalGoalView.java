package it.polimi.ingsw.model;

public interface PersonalGoalView {

    int ROWS = Shelfie.ROWS;
    int COLUMNS = Shelfie.COLUMNS;

    /**
     * @param r defines row of personalGoal
     * @param c defines column of personalGoal
     * @return tile of personal Goal
     */
    Tile get(int r, int c);
}
