package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    /** testing Type.SIX_COUPLES.checkCommonGoal() **/
    @Test
    void checkCommonGoal_SIX_COUPLES_normal(){
        Color[][] matrix = {
                new Color[] { Color.BLUE     , Color.GREEN    , Color.ORANGE   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.GREEN    , Color.YELLOW   , Color.GREEN    , Color.ORANGE   , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.PINK     , Color.BLUE     , Color.ORANGE   , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.PINK     , Color.BLUE     , Color.GREEN    , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.BLUE     , Color.PINK     , Color.LIGHTBLUE },
                new Color[] { Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.YELLOW   , Color.ORANGE    }
        };
        assertTrue(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }
    @Test
    void checkCommonGoal_SIX_COUPLES_allNull(){
        Color[][] Nullmatrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(Nullmatrix)));
    }
    @Test
    void checkCommonGoal_SIX_COUPLES_borderCouples(){
        Color[][] matrix = {
                new Color[] { Color.BLUE     , Color.YELLOW   , Color.YELLOW   , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.ORANGE    },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , null           , Color.ORANGE    }
                };
        assertTrue(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }
    @Test
    void checkCommonGoal_SIX_COUPLES_missingOne(){
        Color[][] matrix = {
                new Color[] { Color.BLUE     , Color.YELLOW   , Color.YELLOW   , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , Color.ORANGE    },
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , Color.ORANGE    }
        };
        assertFalse(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.FOUR_QUADRIPLETS.checkCommonGoal() **/
    @Test
    void checkCommonGoal_Four_QuadripletsNull() {

        Color[][] tilesNull = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(tilesNull)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsSingleQUadriplet() {

        Color[][] singleQuadriplet = {
                new Color[] { Color.GREEN, Color.GREEN, null, null, null },
                new Color[] { null, Color.GREEN, Color.GREEN, Color.GREEN, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };

        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(singleQuadriplet)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsEasy() {

        Color[][] fourQuadripletEasy = {
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE },
                new Color[] { Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE }
        };

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(fourQuadripletEasy)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsNormal() {

        Color[][] fourQuadripletNormal = {
                new Color[] { Color.GREEN, Color.GREEN, Color.GREEN, Color.LIGHTBLUE, Color.BLUE },
                new Color[] { Color.GREEN, Color.ORANGE, Color.LIGHTBLUE, Color.ORANGE, Color.BLUE },
                new Color[] { Color.PINK, Color.ORANGE, Color.YELLOW, Color.ORANGE, Color.BLUE },
                new Color[] { Color.PINK, Color.PINK, null, null, Color.BLUE },
                new Color[] { Color.PINK, Color.YELLOW, null, Color.ORANGE, null },
                new Color[] { null, null, null, null, null }
        };

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(fourQuadripletNormal)));
    }


}
