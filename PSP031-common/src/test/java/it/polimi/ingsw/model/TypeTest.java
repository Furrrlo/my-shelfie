package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

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
        assertTrue(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
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
        assertTrue(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_SIX_COUPLES_normal_false() {
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
        assertTrue(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
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
        assertFalse(Type.SIX_COUPLES.checkCommonGoal(new Shelfie(matrix)));
    }

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
        assertTrue(Type.ALL_CORNERS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_ALL_CORNERS_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.ALL_CORNERS.checkCommonGoal(new Shelfie(matrix)));
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
        assertFalse(Type.ALL_CORNERS.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.FOUR_QUADRIPLETS.checkCommonGoal() **/
    @Test
    void checkCommonGoal_Four_QuadripletsNull() {

        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(matrix)));
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

        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(matrix)));
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

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(matrix)));
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

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.TWO_SQUARES.checkCommonGoal() **/
    @Test
    void checkCommonGoal_TWO_SQUARES_normalTrue() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.ORANGE, Color.LIGHTBLUE, Color.YELLOW, Color.LIGHTBLUE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.BLUE, Color.PINK, Color.BLUE, Color.BLUE, Color.LIGHTBLUE },
                new Color[] { Color.GREEN, Color.YELLOW, Color.PINK, Color.GREEN, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.GREEN, Color.PINK, Color.BLUE, Color.PINK },
                new Color[] { Color.GREEN, Color.BLUE, Color.YELLOW, Color.LIGHTBLUE, Color.GREEN }
        };
        assertTrue(Type.TWO_SQUARES.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_SQUARES_allNull() {

        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.TWO_SQUARES.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_SQUARES_threeSquares() {

        Color[][] matrix = {
                new Color[] { Color.GREEN, Color.GREEN, null, null, null },
                new Color[] { Color.GREEN, Color.GREEN, Color.BLUE, Color.BLUE, null },
                new Color[] { null, null, Color.BLUE, Color.BLUE, Color.PINK },
                new Color[] { null, null, null, null, Color.PINK },
                new Color[] { null, null, null, Color.PINK, Color.PINK },
                new Color[] { null, null, null, Color.PINK, Color.PINK }
        };
        assertTrue(Type.TWO_SQUARES.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.THREE_COLUMNS.checkCommonGoal() */
    @Test
    void checkCommonGoal_THREE_COLUMNS_normalTrue() {
        Color[][] matrix = {
                new Color[] { Color.BLUE, Color.LIGHTBLUE, Color.BLUE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.GREEN },
                new Color[] { Color.BLUE, Color.LIGHTBLUE, Color.ORANGE, Color.YELLOW, Color.PINK },
                new Color[] { Color.YELLOW, Color.ORANGE, Color.GREEN, Color.YELLOW, Color.YELLOW },
                new Color[] { Color.ORANGE, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.GREEN, Color.GREEN, Color.BLUE, Color.GREEN },
        };
        assertTrue(Type.THREE_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_THREE_COLUMNS_allNull() {

        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.THREE_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_THREE_COLUMNS_normalFalse() {
        Color[][] matrix = {
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.LIGHTBLUE, Color.GREEN, Color.GREEN },
                new Color[] { Color.PINK, Color.GREEN, Color.PINK, Color.YELLOW, Color.ORANGE },
                new Color[] { Color.GREEN, Color.GREEN, Color.YELLOW, Color.GREEN, Color.GREEN },
                new Color[] { Color.BLUE, Color.GREEN, Color.ORANGE, Color.BLUE, Color.BLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK, Color.GREEN, Color.ORANGE, Color.LIGHTBLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK, Color.YELLOW, Color.BLUE, Color.LIGHTBLUE }
        };
        assertFalse(Type.THREE_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.EIGHT_EQUAL_TILES.checkCommonGoal() */
    @Test
    void checkCommonGoal_EIGHT_EQUAL_TILES_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.ORANGE   , Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , Color.PINK     , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.LIGHTBLUE, Color.ORANGE   , Color.ORANGE   , Color.ORANGE    },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertTrue(Type.EIGHT_EQUAL_TILES.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_EGHT_EQUAL_TILES_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.EIGHT_EQUAL_TILES.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.DIAGONAL.checkCommonGoal() */
    @Test
    void checkCommonGoal_DIAGONAL_normalTrue() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.ORANGE   , Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , Color.ORANGE   , Color.ORANGE    },
                new Color[] { Color.ORANGE   , Color.LIGHTBLUE, Color.ORANGE   , Color.ORANGE   , Color.ORANGE    },
                new Color[] { Color.BLUE     , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
                //@formatter:on
        };
        assertTrue(Type.DIAGONAL.checkCommonGoal(new Shelfie(matrix)));
        new Shelfie(matrix).printColoredShelfie();
    }

    @Test
    void checkCommonGoal_DIAGONAL_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.DIAGONAL.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.FOUR_ROWS.checkCommonGoal() */
    @Test
    void checkCommonGoal_FOUR_ROWS_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.FOUR_ROWS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_easy() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE },
                new Color[] { null, null, null, null, null }
        };
        assertTrue(Type.FOUR_ROWS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_normal_true() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, null, Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.GREEN },
                new Color[] { null, null, null, null, null }
        };
        assertTrue(Type.FOUR_ROWS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_FOUR_ROWS_normal_false() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, null, Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.ORANGE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.ORANGE, null, Color.ORANGE, Color.ORANGE, Color.GREEN },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.FOUR_ROWS.checkCommonGoal(new Shelfie(matrix)));
    }

    /** testing Type.TWO_ALL_DIFF_COLUMNS.checkCommonGoal() */
    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_allNull() {
        Color[][] matrix = {
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null },
                new Color[] { null, null, null, null, null }
        };
        assertFalse(Type.TWO_ALL_DIFF_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_normal_true() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE, Color.GREEN, Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.BLUE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.PINK, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.GREEN },
                new Color[] { Color.YELLOW, Color.GREEN, Color.YELLOW, Color.YELLOW, null }
        };
        assertTrue(Type.TWO_ALL_DIFF_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

    @Test
    void checkCommonGoal_TWO_ALL_DIFF_COLUMNS_normal_false() {
        Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE, Color.GREEN, Color.ORANGE },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.BLUE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.PINK, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.GREEN },
                new Color[] { null, Color.GREEN, Color.GREEN, Color.YELLOW, null }
        };
        assertFalse(Type.TWO_ALL_DIFF_COLUMNS.checkCommonGoal(new Shelfie(matrix)));
    }

        /** testing Type.TWO_ALL_DIFF_ROWS.checkCommonGoal() */
        @Test
        void checkCommonGoal_TWO_ALL_DIFF_ROWS_allNull() {
            Color[][] matrix = {
                    new Color[] { null, null, null, null, null },
                    new Color[] { null, null, null, null, null },
                    new Color[] { null, null, null, null, null },
                    new Color[] { null, null, null, null, null },
                    new Color[] { null, null, null, null, null },
                    new Color[] { null, null, null, null, null }
            };
            assertFalse(Type.TWO_ALL_DIFF_ROWS.checkCommonGoal(new Shelfie(matrix)));
        }
    
        @Test
        void checkCommonGoal_TWO_TWO_ALL_DIFF_ROWS_normal_true() {
            Color[][] matrix = {
                    new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                    new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE, Color.PINK, Color.YELLOW },
                    new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                    new Color[] { Color.BLUE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                    new Color[] { Color.PINK, Color.ORANGE, Color.GREEN, Color.YELLOW, Color.LIGHTBLUE },
                    new Color[] { Color.YELLOW, Color.GREEN, Color.YELLOW, Color.YELLOW, null }
            };
            assertTrue(Type.TWO_ALL_DIFF_ROWS.checkCommonGoal(new Shelfie(matrix)));
        }
    
        @Test
        void checkCommonGoal_TWO_ALL_DIFF_ROWS_normal_false() {
            Color[][] matrix = {
                new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE, Color.PINK, Color.YELLOW },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.YELLOW },
                new Color[] { Color.BLUE, Color.ORANGE, Color.BLUE, Color.BLUE, Color.BLUE },
                new Color[] { Color.PINK, null, Color.GREEN, Color.YELLOW, Color.LIGHTBLUE },
                new Color[] { Color.YELLOW, Color.GREEN, Color.YELLOW, Color.YELLOW, null }
            };
            assertFalse(Type.TWO_ALL_DIFF_ROWS.checkCommonGoal(new Shelfie(matrix)));
        }

       /** testing Type.CROSS.checkCommonGoal() */
       @Test
       void checkCommonGoal_CROSS_allNull() {
           Color[][] matrix = {
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null }
           };
           assertFalse(Type.CROSS.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_CROSS_normal_true() {
           Color[][] matrix = {
                   new Color[] { Color.ORANGE, Color.BLUE, Color.ORANGE, Color.PINK, Color.ORANGE },
                   new Color[] { Color.GREEN, Color.ORANGE, Color.PINK, Color.PINK, Color.YELLOW },
                   new Color[] { Color.ORANGE, Color.YELLOW, Color.ORANGE, Color.LIGHTBLUE, Color.YELLOW },
                   new Color[] { Color.BLUE, Color.ORANGE, Color.BLUE, Color.ORANGE, Color.BLUE },
                   new Color[] { Color.PINK, Color.ORANGE, Color.GREEN, Color.YELLOW, Color.ORANGE },
                   new Color[] { Color.YELLOW, Color.GREEN, Color.YELLOW, Color.YELLOW, null }
           };
           assertTrue(Type.CROSS.checkCommonGoal(new Shelfie(matrix)));
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
           assertTrue(Type.CROSS.checkCommonGoal(new Shelfie(matrix)));
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
           assertFalse(Type.CROSS.checkCommonGoal(new Shelfie(matrix)));
       }

       /** testing Type.TRIANGLE.checkCommonGoal() */
       @Test
       void checkCommonGoal_TRIANGLE_allNull() {
           Color[][] matrix = {
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null },
                   new Color[] { null, null, null, null, null }
           };
           assertFalse(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_0_4_true() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , null      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.PINK     , null     , null            },
                new Color[] { Color.YELLOW   , Color.GREEN    , null     , null     , null    },
                new Color[] { Color.ORANGE   , null, null     , null   , null      },
                new Color[] { null     , null, null, null           , null            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertTrue(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_1_4_true() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.YELLOW      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.PINK     , Color.YELLOW     , null            },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.YELLOW     , null     , null    },
                new Color[] { Color.ORANGE   , Color.YELLOW, null     , null   , null      },
                new Color[] { Color.YELLOW     , null, null, null           , null            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertTrue(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_0_0_true() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { null   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { null   , null   , Color.PINK     , Color.PINK     , Color.ORANGE            },
                new Color[] { null   , null    , null     , Color.PINK     , Color.ORANGE    },
                new Color[] { null   , null, null     , null   , Color.PINK      },
                new Color[] { null     , null, null, null           , null            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertTrue(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_1_0_true() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE  , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.GREEN   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { null          , Color.ORANGE  , Color.PINK     , Color.PINK     , Color.ORANGE            },
                new Color[] { null          , null          , Color.GREEN    , Color.PINK     , Color.ORANGE    },
                new Color[] { null          , null           , null           , Color.GREEN    , Color.PINK      },
                new Color[] { null          , null           , null           , null           , Color.GREEN            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertTrue(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_0_4_false() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE   , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , null      },
                new Color[] { Color.YELLOW   , Color.ORANGE   , Color.PINK     , null     , null            },
                new Color[] { Color.YELLOW   , Color.GREEN    , Color.PINK     , null     , null    },
                new Color[] { Color.ORANGE   , null, null     , null   , null      },
                new Color[] { null     , null, null, null           , null            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertFalse(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

       @Test
       void checkCommonGoal_TRIANGLE_from_1_0_false() {
           Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.ORANGE  , Color.GREEN    , Color.GREEN    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.GREEN   , Color.ORANGE   , Color.YELLOW   , Color.PINK     , Color.BLUE      },
                new Color[] { Color.GREEN          , Color.ORANGE  , Color.PINK     , Color.PINK     , Color.ORANGE            },
                new Color[] { null          , null          , Color.GREEN    , Color.PINK     , Color.ORANGE    },
                new Color[] { null          , null           , null           , Color.GREEN    , Color.PINK      },
                new Color[] { null          , null           , null           , null           , Color.GREEN            },
                //@formatter:on
           };
           new Shelfie(matrix).printColoredShelfie();
           assertFalse(Type.TRIANGLE.checkCommonGoal(new Shelfie(matrix)));
       }

    @Test
    void printColoredShelfie() {
        Color[][] matrix = {
                new Color[] { Color.LIGHTBLUE, Color.YELLOW, Color.LIGHTBLUE, Color.GREEN, Color.GREEN },
                new Color[] { Color.PINK, Color.GREEN, Color.PINK, Color.YELLOW, Color.ORANGE },
                new Color[] { Color.GREEN, Color.GREEN, Color.YELLOW, Color.GREEN, Color.GREEN },
                new Color[] { Color.BLUE, Color.GREEN, Color.ORANGE, Color.BLUE, Color.BLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK, Color.GREEN, Color.ORANGE, Color.LIGHTBLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK, Color.YELLOW, Color.BLUE, Color.LIGHTBLUE }
        };
        new Shelfie(matrix).printColoredShelfie();
    }
}
