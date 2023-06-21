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
        assertEquals(p1, p2);
    }

    @Test
    void achieved() {
        PersonalGoal p0 = new PersonalGoal(0);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
                new Color[]{null           , null           , null           , null           , Color.GREEN    },
                new Color[]{null           , null           , Color.PINK     , Color.WHITE    , null           },
                new Color[]{null           , Color.YELLOW   , null           , null           , null           },
                new Color[]{null           , null           , null           , Color.WHITE    , Color.GREEN    },
                new Color[]{Color.WHITE    , null           , Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    },
                //@formatter:on
        });
        assertTrue(p0.achievedPersonalGoal(shelfie));
    }

    @Test
    void achieved1() {
        PersonalGoal p0 = new PersonalGoal(0);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.WHITE    , null           , Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    },
                new Color[]{null           , null           , null           , Color.WHITE    , Color.GREEN    },
                new Color[]{null           , null           , null           , null           , null           },
                new Color[]{null           , null           , Color.PINK     , Color.WHITE    , null           },
                new Color[]{null           , null           , null           , null           , Color.GREEN    },
                new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
                //@formatter:on
        });
        assertFalse(p0.achievedPersonalGoal(shelfie));
    }

    @Test
    void achieved2() {
        PersonalGoal p1 = new PersonalGoal(1);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.GREEN    , null           , null           , null           , null           },
                new Color[]{Color.GREEN    , Color.PINK     , null           , null           , null           },
                new Color[]{Color.GREEN    , null           , Color.YELLOW   , null           , null           },
                new Color[]{null           , null           , null           , null           , Color.WHITE    },
                new Color[]{null           , null           , null           , Color.LIGHTBLUE, null           },
                new Color[]{null           , null           , Color.YELLOW   , null           , Color.BLUE     },
                //@formatter:on
        });
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
    void tiles() {
        assertDoesNotThrow(() -> new PersonalGoal(0).tiles().forEach(t -> {
        }));
    }

    @Test
    void testGetIndex() {
        var p = new PersonalGoal(0);
        assertEquals(0, p.getIndex());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var personalGoal1 = new PersonalGoal(0);
        final var personalGoal2 = new PersonalGoal(0);

        assertEquals(personalGoal1, personalGoal1, "Same instance is not the same");
        assertNotEquals(personalGoal1, new Object(), "Different object should not be equals");
        assertEquals(personalGoal1, personalGoal2, "Instances with no differences should be equals");

        final var personalGoalDiffIndex = new PersonalGoal(1);
        assertNotEquals(personalGoal1, personalGoalDiffIndex, "Instances with different tiles should not be equals");
    }

    @Test
    void testHashCode() {
        final var personalGoal1 = new PersonalGoal(0);
        final var personalGoal2 = new PersonalGoal(0);

        assertEquals(personalGoal1.hashCode(), personalGoal1.hashCode(), "Same instance is not the same");
        assertEquals(personalGoal1.hashCode(), personalGoal2.hashCode(), "Instances with no differences should be equals");

        final var personalGoalDiffIndex = new PersonalGoal(1);
        assertNotEquals(personalGoal1.hashCode(), personalGoalDiffIndex.hashCode(),
                "Instances with different tiles should not be equals");
    }

    @Test
    void testToString() {
        final var personalGoal = new PersonalGoal(1);
        assertDoesNotThrow(personalGoal::toString);
    }
}