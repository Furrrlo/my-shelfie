package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FourQuadripletsCommonGoalCheckerTest {

    /** testing new FourQuadripletsCommonGoalChecker().checkCommonGoal() **/
    @Test
    void checkCommonGoal_Four_QuadripletsNull() {

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
        assertFalse(new FourQuadripletsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_Four_Quadriplets_SingleQuadriplet() {

        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.GREEN, Color.GREEN, null       , null       , null },
                new Color[] { null       , Color.GREEN, Color.GREEN, Color.GREEN, null },
                new Color[] { null       , null       , null       , null       , null },
                new Color[] { null       , null       , null       , null       , null },
                new Color[] { null       , null       , null       , null       , null },
                new Color[] { null       , null       , null       , null       , null }
                //@formatter:on
        };

        assertFalse(new FourQuadripletsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsEasy() {

        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE }
                //@formatter:on
        };

        assertTrue(new FourQuadripletsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsNormal() {

        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.GREEN, Color.GREEN , Color.GREEN    , Color.LIGHTBLUE, Color.BLUE },
                new Color[] { Color.GREEN, Color.ORANGE, Color.LIGHTBLUE, Color.ORANGE   , Color.BLUE },
                new Color[] { Color.PINK , Color.ORANGE, Color.YELLOW   , Color.ORANGE   , Color.BLUE },
                new Color[] { Color.PINK , Color.PINK  , null           , null           , Color.BLUE },
                new Color[] { Color.PINK , Color.YELLOW, null           , Color.ORANGE   , null       },
                new Color[] { null       , Color.GREEN , Color.GREEN    , Color.GREEN    , Color.GREEN}
                //@formatter:on
        };

        assertTrue(new FourQuadripletsCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}