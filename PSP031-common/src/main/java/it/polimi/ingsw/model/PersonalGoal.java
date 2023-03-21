package it.polimi.ingsw.model;

import java.util.Arrays;

public class PersonalGoal implements PersonalGoalView {

    private final Tile[][] personalGoal;

    public PersonalGoal(Tile[][] personalGoal) {
        if(personalGoal.length != ROWS)
            throw new IllegalArgumentException("Provided shelf combination has the wrong row size");
        for(int row = 0; row < personalGoal.length; row++) {
            if (personalGoal[row].length != COLUMNS)
                throw new IllegalArgumentException("Provided shelf combination has the wrong column size at row " + row);
        }

        this.personalGoal = personalGoal;
    }

    @Override
    public Tile get(int r, int c) {
        return personalGoal[r][c];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonalGoal that)) return false;
        return Arrays.deepEquals(personalGoal, that.personalGoal);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(personalGoal);
    }

    @Override
    public String toString() {
        return "PersonalGoal{" +
                "personalGoal=" + Arrays.toString(personalGoal) +
                '}';
    }
}