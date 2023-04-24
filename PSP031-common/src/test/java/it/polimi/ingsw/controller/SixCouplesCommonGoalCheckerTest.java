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
                new Color[] { Color.BLUE     , Color.GREEN    , Color.ORANGE   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.GREEN    , Color.YELLOW   , Color.GREEN    , Color.ORANGE   , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.PINK     , Color.BLUE     , Color.ORANGE   , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.PINK     , Color.BLUE     , Color.GREEN    , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.BLUE     , Color.PINK     , Color.LIGHTBLUE },
                new Color[] { Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.YELLOW   , Color.ORANGE    }
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
                new Color[] { Color.BLUE     , Color.YELLOW   , Color.YELLOW   , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.ORANGE    },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , null           , Color.ORANGE    }
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
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            },
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            }
                //@formatter:on
        };
        assertTrue(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_missingOne() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.BLUE     , Color.YELLOW   , Color.YELLOW   , null           , Color.PINK      },
                new Color[] { Color.BLUE     , null           , null           , null           , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , Color.ORANGE    },
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , Color.ORANGE    }
                //@formatter:on
        };
        assertFalse(new SixCouplesCommonGoalChecker().checkCommonGoal(new Shelfie(matrix)));
    }
}