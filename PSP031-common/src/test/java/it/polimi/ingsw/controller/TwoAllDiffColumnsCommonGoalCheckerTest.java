package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwoAllDiffColumnsCommonGoalCheckerTest {

    /** testing Type.TWO_ALL_DIFF_COLUMNS.checkCommonGoal() */
    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_allNull() {
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
        assertFalse(new TwoAllDiffColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW, Color.BLUE  , Color.YELLOW, Color.PINK     , Color.YELLOW},
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW, Color.GREEN    , Color.YELLOW},
                new Color[] { Color.LIGHTBLUE, Color.WHITE, Color.GREEN , Color.LIGHTBLUE, Color.WHITE},
                new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE  , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.PINK     , Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.GREEN  },
                new Color[] { Color.WHITE, Color.GREEN , Color.WHITE, Color.WHITE, null         }
                //@formatter:on
        };
        assertTrue(new TwoAllDiffColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW, Color.BLUE  , Color.YELLOW, Color.PINK     , Color.YELLOW},
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW, Color.GREEN    , Color.YELLOW},
                new Color[] { Color.LIGHTBLUE, Color.WHITE, Color.GREEN , Color.LIGHTBLUE, Color.WHITE},
                new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE  , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.PINK     , Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.GREEN  },
                new Color[] { null           , Color.GREEN , Color.GREEN , Color.WHITE, null         }
                //@formatter:on
        };
        assertFalse(new TwoAllDiffColumnsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}