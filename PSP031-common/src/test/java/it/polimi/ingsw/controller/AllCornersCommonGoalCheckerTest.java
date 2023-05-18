package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllCornersCommonGoalCheckerTest {

    /** testing Type.ALL_CORNERS.checkCommonGoal() **/
    @Test
    void getDescription() {
        final String description = """
                Four tiles of the same type in the four corners
                of the bookshelf.
                """;
        assertEquals(description, Type.ALL_CORNERS.getDescription());
    }

    @Test
    void checkCommonGoal_ALL_CORNERS_normal_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.PINK     , Color.WHITE    , Color.LIGHTBLUE, Color.PINK     , Color.PINK      },
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.BLUE     , Color.PINK     , Color.BLUE      },
                new Color[] { Color.BLUE     , Color.PINK     , Color.WHITE    , Color.LIGHTBLUE, Color.BLUE      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.WHITE    , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.WHITE    , Color.BLUE     , Color.GREEN    , Color.PINK     , null            },
                new Color[] { Color.PINK     , Color.YELLOW   , null           , Color.LIGHTBLUE, Color.PINK      }
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
    void checkCommonGoal_ALL_CORNERS1() {
        Color[][] matrix1 = {
                //@formatter:off
                new Color[] { Color.GREEN, null, null, null, null },
                new Color[] { null       , null, null, null, null },
                new Color[] { null       , null, null, null, null },
                new Color[] { null       , null, null, null, null },
                new Color[] { null       , null, null, null, null },
                new Color[] { null       , null, null, null, null }
                //@formatter:on
        };
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix1)));
        Color[][] matrix2 = {
                //@formatter:off
                new Color[] { Color.GREEN, null, null, null, Color.GREEN },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        }
                //@formatter:on
        };
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix2)));
        Color[][] matrix3 = {
                //@formatter:off
                new Color[] { Color.GREEN, null, null, null, Color.GREEN },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { Color.GREEN, null, null, null, null        }
                //@formatter:on
        };
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix3)));
        Color[][] matrix4 = {
                //@formatter:off
                new Color[] { Color.GREEN, null, null, null, Color.GREEN },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { null       , null, null, null, null        },
                new Color[] { Color.GREEN, null, null, null, Color.GREEN }
                //@formatter:on
        };
        assertTrue(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix4)));
    }

    @Test
    void checkCommonGoal_ALL_CORNERS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.PINK     , Color.WHITE    , Color.LIGHTBLUE, Color.PINK     , Color.GREEN     },
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.BLUE     , Color.PINK     , Color.BLUE      },
                new Color[] { Color.BLUE     , Color.PINK     , Color.WHITE    , Color.LIGHTBLUE, Color.BLUE      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.WHITE    , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.WHITE    , Color.BLUE     , Color.GREEN    , Color.PINK     , null            },
                new Color[] { Color.PINK     , Color.YELLOW   , null           , Color.LIGHTBLUE, Color.PINK      }
                //@formatter:on
        };
        assertFalse(new AllCornersCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}