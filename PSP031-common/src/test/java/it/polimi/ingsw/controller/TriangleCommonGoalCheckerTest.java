package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriangleCommonGoalCheckerTest {

    /** testing Type.TRIANGLE.checkCommonGoal() */
    @Test
    void checkCommonGoal_TRIANGLE_allNull() {
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
        assertFalse(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_0_4_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , null            },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , null           , null            },
                new Color[] { Color.WHITE    , Color.GREEN    , null           , null           , null            },
                new Color[] { Color.YELLOW   , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                //@formatter:on
        };
        assertTrue(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_1_4_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE       },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.WHITE       },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , Color.WHITE    , null              },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.WHITE    , null           , null              },
                new Color[] { Color.YELLOW   , Color.WHITE    , null           , null           , null              },
                new Color[] { Color.WHITE    , null           , null           , null           , null              },
                //@formatter:on
        };
        assertTrue(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_0_0_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { null           , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE      },
                new Color[] { null           , null           , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { null           , null           , null           , Color.PINK     , Color.YELLOW    },
                new Color[] { null           , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                //@formatter:on
        };
        assertTrue(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_1_0_true() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.GREEN   , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE      },
                new Color[] { null          , Color.YELLOW   , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { null          , null           , Color.GREEN    , Color.PINK     , Color.YELLOW    },
                new Color[] { null          , null           , null           , Color.GREEN    , Color.PINK      },
                new Color[] { null          , null           , null           , null           , Color.GREEN     },
                //@formatter:on
        };
        assertTrue(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_0_4_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE     },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.WHITE    , Color.PINK     , null            },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.PINK     , null           , null            },
                new Color[] { Color.WHITE    , Color.GREEN    , Color.PINK     , null           , null            },
                new Color[] { Color.YELLOW   , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                //@formatter:on
        };
        assertFalse(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TRIANGLE_from_1_0_false() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.WHITE    },
                new Color[] { Color.GREEN   , Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.BLUE      },
                new Color[] { Color.GREEN   , Color.YELLOW   , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { null          , null           , Color.GREEN    , Color.PINK     , Color.YELLOW    },
                new Color[] { null          , null           , null           , Color.GREEN    , Color.PINK      },
                new Color[] { null          , null           , null           , null           , Color.GREEN     },
                //@formatter:on
        };
        assertFalse(new TriangleCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}