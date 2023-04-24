package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrossCommonGoalCheckerTest {

    /** testing Type.CROSS.checkCommonGoal() */
    @Test
    void checkCommonGoal_CROSS_allNull() {
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
        assertFalse(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.BLUE     , Color.ORANGE   , Color.PINK     , Color.ORANGE    },
                new Color[] { Color.GREEN    , Color.ORANGE   , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.ORANGE   , Color.YELLOW   , Color.ORANGE   , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.BLUE     , Color.ORANGE   , Color.BLUE     , Color.ORANGE   , Color.BLUE      },
                new Color[] { Color.PINK     , Color.ORANGE   , Color.GREEN    , Color.YELLOW   , Color.ORANGE    },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.YELLOW   , Color.YELLOW   , null            }
                //@formatter:on
        };
        assertTrue(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_normal1_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.PINK     , Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , Color.PINK     , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.LIGHTBLUE, Color.PINK     , Color.ORANGE   , Color.PINK      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertTrue(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.PINK     , Color.PINK     , null            },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , Color.PINK     , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.LIGHTBLUE, Color.PINK     , Color.ORANGE   , Color.PINK      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertFalse(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}