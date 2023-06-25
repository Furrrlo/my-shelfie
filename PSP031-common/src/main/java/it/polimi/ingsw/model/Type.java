package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.*;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public enum Type implements Serializable {
    SIX_COUPLES(new SixCouplesCommonGoalChecker(), """
            Six groups each containing at least 2 tiles of
            the same type(not necessarily in the depicted
            shape). The tiles of one group cam be
            different from those of another group.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , Color.BLUE      },
                new Color[] { Color.WHITE    , null           , null           , null           , Color.BLUE      },
                new Color[] { null           , null           , Color.YELLOW   , null           , null            },
                new Color[] { null           , null           , Color.YELLOW   , null           , null            },
                new Color[] { Color.GREEN    , null           , null           , null           , null            },
                new Color[] { Color.GREEN    , null           , null           , Color.PINK     , Color.PINK      },
            //@formatter:on
            }),
    ALL_CORNERS(new AllCornersCommonGoalChecker(), """
            Four tiles of the same type in the four corners
            of the bookshelf.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
            //@formatter:on
            }),
    FOUR_QUADRIPLETS(new FourQuadripletsCommonGoalChecker(), """
            Four groups each containing at least 4 tiles
            of the same type(not necessarily in the
            depicted shape). The tiles of one group can be
            different from those of another group.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.BLUE     , Color.BLUE     , null           , Color.PINK     , null            },
                new Color[] { Color.BLUE     , Color.BLUE     , null           , Color.PINK     , null            },
                new Color[] { null           , null           , null           , Color.PINK     , null            },
                new Color[] { null           , null           , null           , Color.PINK     , null            },
                new Color[] { null           , Color.YELLOW   , Color.GREEN    , Color.GREEN    , null            },
                new Color[] { Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.GREEN    , Color.GREEN     },
            //@formatter:on
            }),
    TWO_SQUARES(new TwoSquaresCommonGoalChecker(), """
            Two groups each containing 4 tiles of the same
            type in a 2x2 square. The tiles of one square
            can be different from those of the other square.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            },
                new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
            }),
    THREE_COLUMNS(new ThreeColumnsCommonGoalChecker(), """
            Three columns each formed by 6 tiles of maximum
            three different types. One column can show the
            same or a different combination of another column.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.BLUE     , null           , Color.BLUE     , Color.LIGHTBLUE, null            },
                new Color[] { Color.YELLOW   , null           , Color.WHITE    , Color.PINK     , null            },
                new Color[] { Color.YELLOW   , null           , Color.BLUE     , Color.GREEN    , null            },
                new Color[] { Color.GREEN    , null           , Color.WHITE    , Color.PINK     , null            },
                new Color[] { Color.YELLOW   , null           , Color.YELLOW   , Color.LIGHTBLUE, null            },
                new Color[] { Color.GREEN    , null           , Color.YELLOW   , Color.LIGHTBLUE, null            },
            //@formatter:on
            }),
    EIGHT_EQUAL_TILES(new EightEqualTilesCommonGoalChecker(), """
            Eight tiles of the same type. There's no
            restriction about the position of these
            tiles.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
                new Color[] { Color.LIGHTBLUE, null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, null           , Color.LIGHTBLUE, null           , null            },
                new Color[] { null           , null           , null           , null           , Color.LIGHTBLUE },
                new Color[] { null           , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
            //@formatter:on
            }),
    //@formatter:on
    DIAGONAL(new DiagonalCommonGoalChecker(), """
            Five tiles of the same type forming a diagonal.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.PINK     , null           , null           , null           , null            },
                new Color[] { null           , Color.PINK     , null           , null           , null            },
                new Color[] { null           , null           , Color.PINK     , null           , null            },
                new Color[] { null           , null           , null           , Color.PINK     , null            },
                new Color[] { null           , null           , null           , null           , Color.PINK     },
                new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
            }),
    FOUR_ROWS(new FourRowsCommonGoalChecker(), """
            Four lines each formed by 5 tiles of
            maximum three different types. One line
            can show the same or a different
            combination of another line.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE     },
                new Color[] { Color.BLUE     , Color.BLUE     , Color.BLUE     , Color.BLUE     , Color.BLUE      },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.GREEN     },
                new Color[] { Color.PINK     , Color.PINK     , Color.LIGHTBLUE, Color.LIGHTBLUE, Color.PINK      },
            //@formatter:on
            }),
    TWO_ALL_DIFF_COLUMNS(new TwoAllDiffColumnsCommonGoalChecker(), """
            Two columns each formed by 6 different types
            of tiles.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { Color.YELLOW   , null           , Color.PINK     , null           , Color.PINK      },
                new Color[] { Color.GREEN    , null           , Color.GREEN    , null           , Color.GREEN     },
                new Color[] { Color.LIGHTBLUE, null           , Color.LIGHTBLUE, null           , Color.LIGHTBLUE },
                new Color[] { Color.BLUE     , null           , Color.BLUE     , null           , Color.BLUE      },
                new Color[] { Color.PINK     , null           , Color.YELLOW   , null           , Color.YELLOW    },
                new Color[] { Color.WHITE    , null           , Color.WHITE    , null           , Color.WHITE     },
            //@formatter:on
            }),
    TWO_ALL_DIFF_ROWS(new TwoAllDiffRowsCommonGoalChecker(), """
            Two lines each formed by 5 different types
            of tiles. One line can show the same or a
            different combination of the other line.
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.BLUE     , Color.PINK     , Color.GREEN    , Color.WHITE    , Color.YELLOW    },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    , Color.BLUE     , Color.PINK      },
                new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
            }),
    CROSS(new CrossCommonGoalChecker(), """
            Five tiles of the same type forming an X
            """,
            new Color[][] {
            //@formatter:off
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , Color.PINK     , null           , Color.PINK     , null            },
                new Color[] { null           , null           , Color.PINK     , null           , null            },
                new Color[] { null           , Color.PINK     , null           , Color.PINK     , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
            }),
    TRIANGLE(new TriangleCommonGoalChecker(), """
            Five columns of increasing or decreasing
            height. Starting from first column on the
            left or on the right, each next column must
            be made of exactly one more tile. Tiles
            can be of any type.""",
            new Color[][] {
            //@formatter:off
                new Color[] { null           , null           , null           , null           , null             },
                new Color[] { null           , null           , null           , null           , Color.YELLOW     },
                new Color[] { null           , null           , null           , Color.GREEN    , Color.WHITE      },
                new Color[] { null           , null           , Color.GREEN    , Color.YELLOW   , Color.WHITE      },
                new Color[] { null           , Color.YELLOW   , Color.WHITE    , Color.YELLOW   , Color.WHITE      },
                new Color[] { Color.PINK     , Color.GREEN    , Color.PINK     , Color.GREEN    , Color.YELLOW     },
            //@formatter:on
            });

    private final CommonGoalChecker checker;
    private final String description;
    private final ImmutableColorMatrix example;

    Type(CommonGoalChecker checker, String description, @Nullable Color[][] example) {
        this.checker = checker;
        this.description = description;
        this.example = new ImmutableColorMatrix(example);
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns true if the common goal of given type is achieved
     **/
    public boolean checkCommonGoal(Shelfie shelfie) {
        return checker.checkCommonGoal(shelfie);
    }

    public ImmutableColorMatrix getExample() {
        return example;
    }

    public static class ImmutableColorMatrix {

        private final @Nullable Color[][] matrix;

        public ImmutableColorMatrix(@Nullable Color[][] matrix) {
            this.matrix = matrix;
        }

        @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
        public @Nullable Color get(int row, int col) {
            return matrix[row][col];
        }
    }
}