package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreeColumnsCommonGoalCheckerTest {

    /** testing Type.THREE_COLUMNS.checkCommonGoal() */
    @Test
    void checkCommonGoal_THREE_COLUMNS_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.BLUE     , Color.ORANGE   , Color.ORANGE   },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.ORANGE   , Color.ORANGE   , Color.GREEN    },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.ORANGE   , Color.YELLOW   , Color.PINK     },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.GREEN    , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.ORANGE   , Color.YELLOW   , Color.ORANGE   , Color.ORANGE   , Color.ORANGE   },
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.GREEN    },
                //@formatter:on
        };
        assertTrue(new ThreeColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_THREE_COLUMNS_allNull() {

        Color[][] matrix = {
                //@formatter:off
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
                //@formatter:on
        };
        assertFalse(new ThreeColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_THREE_COLUMNS_normalFalse() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.LIGHTBLUE, Color.GREEN    , Color.GREEN     },
                new Color[] { Color.PINK     , Color.GREEN    , Color.PINK     , Color.YELLOW   , Color.ORANGE    },
                new Color[] { Color.GREEN    , Color.GREEN    , Color.YELLOW   , Color.GREEN    , Color.GREEN     },
                new Color[] { Color.BLUE     , Color.GREEN    , Color.ORANGE   , Color.BLUE     , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.PINK     , Color.GREEN    , Color.ORANGE   , Color.LIGHTBLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK     , Color.YELLOW   , Color.BLUE     , Color.LIGHTBLUE }
                //@formatter:on
        };
        assertFalse(new ThreeColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}