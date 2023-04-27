package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiagonalCommonGoalCheckerTest {

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.YELLOW   , Color.PINK     , Color.PINK     },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           },
                //@formatter:on
        };
        assertTrue(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_secondaryTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.YELLOW   , Color.PINK     , Color.PINK     },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW   },
                //@formatter:on
        };
        assertTrue(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_reverseTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.WHITE    , Color.PINK     , Color.WHITE    , Color.WHITE    , Color.BLUE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.PINK     },
                new Color[] { Color.WHITE    , Color.WHITE    , Color.PINK     , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           },
                //@formatter:on
        };
        assertTrue(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_reverseSecondaryTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.WHITE    , Color.PINK     , Color.WHITE    , Color.WHITE    , Color.BLUE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.PINK     },
                new Color[] { Color.WHITE    , Color.WHITE    , Color.PINK     , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW   },
                //@formatter:on
        };
        assertTrue(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_normalFalse() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.WHITE    , Color.PINK     , Color.WHITE    , Color.PINK     , Color.BLUE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.YELLOW   , Color.PINK     , Color.PINK     },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW   },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           },
                //@formatter:on
        };
        assertFalse(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_DIAGONAL_allNull() {
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
        assertFalse(new DiagonalCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}