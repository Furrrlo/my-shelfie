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
        p1.printPersonalGoal();
        PersonalGoal p2 = new PersonalGoal(0);
        p2.printPersonalGoal();
        assertEquals(p1, p2);
    }

    @Test
    void achieved() {
        PersonalGoal p0 = new PersonalGoal(0);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.YELLOW   , null           , Color.LIGHTBLUE, Color.YELLOW   , Color.GREEN    },
                new Color[]{null           , null           , null           , Color.YELLOW   , Color.GREEN    },
                new Color[]{null           , Color.ORANGE   , null           , null           , null           },
                new Color[]{null           , null           , Color.PINK     , Color.YELLOW   , null           },
                new Color[]{null           , null           , null           , null           , Color.GREEN    },
                new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
                //@formatter:on
        });
        p0.printPersonalGoalOnShelfie(shelfie);
        assertTrue(p0.achievedPersonalGoal(shelfie));
    }

    @Test
    void achieved1() {
        PersonalGoal p0 = new PersonalGoal(0);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.YELLOW   , null           , Color.LIGHTBLUE, Color.YELLOW   , Color.GREEN    },
                new Color[]{null           , null           , null           , Color.YELLOW   , Color.GREEN    },
                new Color[]{null           , null           , null           , null           , null           },
                new Color[]{null           , null           , Color.PINK     , Color.YELLOW   , null           },
                new Color[]{null           , null           , null           , null           , Color.GREEN    },
                new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
                //@formatter:on
        });
        p0.printPersonalGoalOnShelfie(shelfie);
        assertFalse(p0.achievedPersonalGoal(shelfie));
    }

    @Test
    void achieved2() {
        PersonalGoal p1 = new PersonalGoal(1);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{null           , null           , Color.ORANGE   , null           , Color.BLUE     },
                new Color[]{null           , null           , null           , Color.LIGHTBLUE, null           },
                new Color[]{null           , null           , null           , null           , Color.YELLOW   },
                new Color[]{Color.GREEN    , null           , Color.ORANGE   , null           , null           },
                new Color[]{Color.GREEN    , Color.PINK     , null           , null           , null           },
                new Color[]{Color.GREEN    , null           , null           , null           , null           },
                //@formatter:on
        });
        p1.printPersonalGoal();
        shelfie.printColoredShelfie();
        assertTrue(p1.achievedPersonalGoal(shelfie));
    }

    @Test
    void achieved3() {
        PersonalGoal p2 = new PersonalGoal(2);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
                //@formatter:on
        });
        assertFalse(p2.achievedPersonalGoal(shelfie));
    }

    @Test
    void getPersonalGoal() {
        PersonalGoal p1 = new PersonalGoal(1);
        p1.getPersonalGoal();
    }
}