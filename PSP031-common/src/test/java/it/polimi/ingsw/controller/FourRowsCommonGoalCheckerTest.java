package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FourRowsCommonGoalCheckerTest {

    /** testing Type.FOUR_ROWS.checkCommonGoal() */
    @Test
    void checkCommonGoal_FOUR_ROWS_allNull() {
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
        assertFalse(new FourRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_easy() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { null        , null        , null        , null        , null         }
                //@formatter:on
        };
        assertTrue(new FourRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.ORANGE },
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, null           , Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN , Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.ORANGE   , Color.ORANGE, Color.BLUE  , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.ORANGE   , Color.ORANGE, Color.ORANGE, Color.ORANGE   , Color.GREEN  },
                new Color[] { null           , null        , null        , null           , null         }
                //@formatter:on
        };
        assertTrue(new FourRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, Color.PINK     , Color.ORANGE },
                new Color[] { Color.ORANGE   , Color.BLUE  , Color.ORANGE, null           , Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN , Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.ORANGE   , Color.ORANGE, Color.BLUE  , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.ORANGE   , null        , Color.ORANGE, Color.ORANGE   , Color.GREEN  },
                new Color[] { null           , null        , null        , null           , null         }
                //@formatter:on
        };
        assertFalse(new FourRowsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}