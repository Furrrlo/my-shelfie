package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EightEqualTilesCommonGoalCheckerTest {

    /** testing Type.EIGHT_EQUAL_TILES.checkCommonGoal() */
    @Test
    void checkCommonGoal_EIGHT_EQUAL_TILES_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.ORANGE   , Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , Color.PINK     , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.LIGHTBLUE, Color.ORANGE   , Color.ORANGE   , Color.ORANGE    },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertTrue(new EightEqualTilesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_EGHT_EQUAL_TILES_allNull() {
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
        assertFalse(new EightEqualTilesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}