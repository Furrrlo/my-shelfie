package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        PersonalGoal p2 = new PersonalGoal(0);
        assertEquals(p1,p2);
    }
}