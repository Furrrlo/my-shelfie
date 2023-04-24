package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwoAllDiffRowsCommonGoalCheckerTest {

    /** testing Type.TWO_ALL_DIFF_ROWS.checkCommonGoal() */
    @Test
    void checkCommonGoal_TWO_ALL_DIFF_ROWS_allNull() {
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
        assertFalse(new TwoAllDiffRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_TWO_ALL_DIFF_ROWS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.ORANGE    },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.BLUE     , Color.ORANGE, Color.BLUE  , Color.BLUE     , Color.BLUE      },
                new Color[] { Color.PINK     , Color.ORANGE, Color.GREEN , Color.YELLOW   , Color.LIGHTBLUE },
                new Color[] { Color.YELLOW   , Color.GREEN , Color.YELLOW, Color.YELLOW   , null            }
                //@formatter:on
        };
        assertTrue(new TwoAllDiffRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_ALL_DIFF_ROWS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.ORANGE    },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.BLUE     , Color.ORANGE, Color.BLUE  , Color.BLUE     , Color.BLUE      },
                new Color[] { Color.PINK     , null        , Color.GREEN , Color.YELLOW   , Color.LIGHTBLUE },
                new Color[] { Color.YELLOW   , Color.GREEN , Color.YELLOW, Color.YELLOW   , null            }
                //@formatter:on
        };
        assertFalse(new TwoAllDiffRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}