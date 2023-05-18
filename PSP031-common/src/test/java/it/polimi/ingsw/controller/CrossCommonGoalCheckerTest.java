package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrossCommonGoalCheckerTest {

    /** testing Type.CROSS.checkCommonGoal() */
    @Test
    void getDescription() {
        final String description = """
                Five tiles of the same type forming an X
                """;
        assertEquals(description, Type.CROSS.getDescription());
    }

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
                new Color[] { Color.YELLOW   , Color.BLUE     , Color.YELLOW   , Color.PINK     , Color.YELLOW   },
                new Color[] { Color.GREEN    , Color.YELLOW   , Color.PINK     , Color.PINK     , Color.WHITE    },
                new Color[] { Color.YELLOW   , Color.WHITE    , Color.YELLOW   , Color.LIGHTBLUE, Color.WHITE    },
                new Color[] { Color.BLUE     , Color.YELLOW   , Color.BLUE     , Color.YELLOW   , Color.BLUE     },
                new Color[] { Color.PINK     , Color.YELLOW   , Color.GREEN    , Color.WHITE    , Color.YELLOW   },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.WHITE    , Color.WHITE    , null           }
                //@formatter:on
        };
        assertTrue(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_normal1_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE      },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , Color.PINK     , Color.PINK      },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.PINK     , Color.YELLOW   , Color.PINK      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertTrue(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_normal_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE      },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , Color.PINK     , null            },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.PINK     , Color.YELLOW   , Color.PINK      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertFalse(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_CROSS_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , Color.PINK     , Color.BLUE      },
                new Color[] { Color.WHITE    , Color.WHITE    , Color.PINK     , Color.PINK     , null            },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.WHITE    , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.LIGHTBLUE, Color.PINK     , Color.YELLOW   , Color.PINK      },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertFalse(new CrossCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}