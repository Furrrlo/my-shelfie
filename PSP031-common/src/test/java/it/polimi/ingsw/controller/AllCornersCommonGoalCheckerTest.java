package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllCornersCommonGoalCheckerTest {

    /** testing Type.ALL_CORNERS.checkCommonGoal() **/
    @Test
    void checkCommonGoal_ALL_CORNERS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.PINK     , Color.YELLOW   , Color.LIGHTBLUE, Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.BLUE     , Color.PINK     , Color.BLUE      },
                new Color[] { Color.BLUE     , Color.PINK     , Color.YELLOW   , Color.LIGHTBLUE, Color.BLUE      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.YELLOW   , Color.LIGHTBLUE, Color.ORANGE    },
                new Color[] { Color.YELLOW   , Color.BLUE     , Color.GREEN    , Color.PINK     , null            },
                new Color[] { Color.PINK     , Color.ORANGE   , null           , Color.LIGHTBLUE, Color.PINK      }
                //@formatter:on
        };
        assertTrue(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_ALL_CORNERS_allNull() {
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
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_ALL_CORNERS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.PINK     , Color.YELLOW   , Color.LIGHTBLUE, Color.PINK     , Color.GREEN     },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.BLUE     , Color.PINK     , Color.BLUE      },
                new Color[] { Color.BLUE     , Color.PINK     , Color.YELLOW   , Color.LIGHTBLUE, Color.BLUE      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.YELLOW   , Color.LIGHTBLUE, Color.ORANGE    },
                new Color[] { Color.YELLOW   , Color.BLUE     , Color.GREEN    , Color.PINK     , null            },
                new Color[] { Color.PINK     , Color.ORANGE   , null           , Color.LIGHTBLUE, Color.PINK      }
                //@formatter:on
        };
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}