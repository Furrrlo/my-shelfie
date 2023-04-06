package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PersonalGoalTest {
    @Test
    void equals0() {
        PersonalGoal p1 = new PersonalGoal(1);
        PersonalGoal p2 = new PersonalGoal(2);
        assertNotEquals(p1, p2);
    }

    @Test
    void equals1() {
        PersonalGoal p1 = new PersonalGoal(0);
        p1.printPersonalGoal();
        PersonalGoal p2 = new PersonalGoal(0);
        p2.printPersonalGoal();
        assertEquals(p1, p2);
    }

}