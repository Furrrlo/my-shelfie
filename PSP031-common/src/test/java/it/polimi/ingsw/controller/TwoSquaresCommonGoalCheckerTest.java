package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwoSquaresCommonGoalCheckerTest {

    /** testing Type.TWO_SQUARES.checkCommonGoal() **/
    @Test
    void checkCommonGoal_TWO_SQUARES_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.ORANGE   , Color.LIGHTBLUE, Color.YELLOW   , Color.LIGHTBLUE },
                new Color[] { Color.ORANGE   , Color.ORANGE   , Color.BLUE     , Color.BLUE     , Color.BLUE      },
                new Color[] { Color.BLUE     , Color.PINK     , Color.BLUE     , Color.BLUE     , Color.LIGHTBLUE },
                new Color[] { Color.GREEN    , Color.YELLOW   , Color.PINK     , Color.GREEN    , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.PINK     , Color.BLUE     , Color.PINK      },
                new Color[] { Color.GREEN    , Color.BLUE     , Color.YELLOW   , Color.LIGHTBLUE, Color.GREEN     }
                //@formatter:on
        };
        assertTrue(new TwoSquaresCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_SQUARES_allNull() {

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
        assertFalse(new TwoSquaresCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_SQUARES_threeSquares() {

        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.GREEN    , Color.GREEN    , null           , null           , null           },
                new Color[] { Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.BLUE     , null           },
                new Color[] { null           , null           , Color.BLUE     , Color.BLUE     , Color.PINK     },
                new Color[] { null           , null           , null           , null           , Color.PINK     },
                new Color[] { null           , null           , null           , Color.PINK     , Color.PINK     },
                new Color[] { null           , null           , null           , Color.PINK     , Color.PINK     }
                //@formatter:on
        };
        assertTrue(new TwoSquaresCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}