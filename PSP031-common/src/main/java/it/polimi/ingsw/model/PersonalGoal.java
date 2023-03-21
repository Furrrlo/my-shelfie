package it.polimi.ingsw.model;

/**
 * 
 */
public class PersonalGoal implements PersonalGoalView {

    private Tile[][] personalGoal;
    /**
     * Default constructor
     */
    public PersonalGoal( Tile[][] personalGoal){
        this.personalGoal = personalGoal;
    }


    @Override
    public Tile get(int r, int c) {
        return personalGoal[r][c];
    }

}