package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SixCouplesCommonGoalCheckerTest {

    /** testing Type.SIX_COUPLES.checkCommonGoal() **/
    @Test
    void checkCommonGoal_SIX_COUPLES_normal() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.GREEN    , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.GREEN    , Color.WHITE    , Color.GREEN    , Color.YELLOW   , Color.PINK      },
                new Color[] { Color.WHITE    , Color.PINK     , Color.BLUE     , Color.YELLOW   , Color.BLUE      },
                new Color[] { Color.WHITE    , Color.PINK     , Color.BLUE     , Color.GREEN    , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.BLUE     , Color.PINK     , Color.LIGHTBLUE },
                new Color[] { Color.YELLOW   , Color.WHITE    , Color.PINK     , Color.WHITE    , Color.YELLOW   }
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_allNull() {
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
        assertFalse(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_borderCouples() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.WHITE    , Color.WHITE    , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.WHITE    , null           , Color.YELLOW    }
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_normal_true1() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.BLUE     , Color.PINK     , Color.PINK     , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , Color.WHITE    , Color.WHITE    , null           , null            },
                new Color[] { null           , Color.WHITE    , Color.WHITE    , null           , null            }
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_missingOne() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.WHITE    , Color.WHITE    , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , Color.YELLOW    },
                new Color[] { null           , Color.WHITE    , Color.WHITE    , null           , Color.YELLOW    }
                //@formatter:on
        };
        assertFalse(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { null           , Color.WHITE    , Color.WHITE    , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , Color.YELLOW   , null           , null            },
                new Color[] { null           , null           , Color.YELLOW   , null           , null            },
                new Color[] { null           , null           , null           , null           , Color.YELLOW    },
                new Color[] { null           , Color.WHITE    , Color.WHITE    , null           , Color.YELLOW    }
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void sixCouplesOnRandomGenerated() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.GREEN    , Color.GREEN    , Color.PINK     , Color.WHITE    , Color.PINK      },
                new Color[] { Color.PINK     , Color.GREEN    , Color.LIGHTBLUE, Color.BLUE     , Color.WHITE     },
                new Color[] { Color.BLUE     , Color.GREEN    , Color.GREEN    , Color.WHITE    , Color.LIGHTBLUE },
                new Color[] { Color.WHITE    , Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.YELLOW    },
                new Color[] { Color.WHITE    , Color.WHITE    , Color.GREEN    , Color.WHITE    , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.GREEN    , Color.YELLOW   , Color.BLUE     , Color.GREEN     },
                //@formatter:on
        };
        assertFalse(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));

        Color[][] matrix1 = {
                //@formatter:off
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.BLUE     , Color.GREEN    , Color.PINK      },
                new Color[] { Color.BLUE     , Color.WHITE    , Color.YELLOW   , Color.BLUE     , Color.LIGHTBLUE },
                new Color[] { Color.GREEN    , Color.PINK     , Color.LIGHTBLUE, Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.PINK     , Color.LIGHTBLUE, Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.GREEN     },
                new Color[] { Color.PINK     , Color.PINK     , Color.BLUE     , Color.BLUE     , Color.WHITE     },
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix1)));

        Color[][] matrix2 = {
                //@formatter:off
                new Color[] { Color.GREEN    , Color.GREEN    , Color.LIGHTBLUE, Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.PINK     , Color.WHITE    , Color.WHITE    , Color.LIGHTBLUE },
                new Color[] { Color.YELLOW   , Color.BLUE     , Color.PINK     , Color.GREEN    , Color.YELLOW    },
                new Color[] { Color.PINK     , Color.GREEN    , Color.LIGHTBLUE, Color.PINK     , Color.PINK      },
                new Color[] { Color.PINK     , Color.WHITE    , Color.BLUE     , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.PINK     , Color.GREEN    , Color.LIGHTBLUE, Color.GREEN    , Color.GREEN     },
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix2)));

    }
}