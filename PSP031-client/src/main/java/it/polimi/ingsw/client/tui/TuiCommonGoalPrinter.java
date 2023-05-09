package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ShelfieView;
import it.polimi.ingsw.model.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TuiCommonGoalPrinter implements TuiPrinter2 {
    private final Type type;
    private static final Color[][] SIX_COUPLES = new Color[][] {
            //@formatter:off
            new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , Color.BLUE      },
            new Color[] { Color.WHITE    , null           , null           , null           , Color.BLUE      },
            new Color[] { null           , null           , Color.YELLOW   , null           , null            },
            new Color[] { null           , null           , Color.YELLOW   , null           , null            },
            new Color[] { Color.GREEN    , null           , null           , null           , null            },
            new Color[] { Color.GREEN    , null           , null           , Color.PINK     , Color.PINK      },
            //@formatter:on
    };
    private static final Color[][] ALL_CORNERS = new Color[][] {
            //@formatter:off
            new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
            //@formatter:on
    };
    private static final Color[][] FOUR_QUADRIPLETS = new Color[][] {
            //@formatter:off
            new Color[] { Color.BLUE     , Color.BLUE     , null           , Color.PINK     , null            },
            new Color[] { Color.BLUE     , Color.BLUE     , null           , Color.PINK     , null            },
            new Color[] { null           , null           , null           , Color.PINK     , null            },
            new Color[] { null           , null           , null           , Color.PINK     , null            },
            new Color[] { null           , Color.YELLOW   , Color.GREEN    , Color.GREEN    , null            },
            new Color[] { Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.GREEN    , Color.GREEN     },
            //@formatter:on
    };
    private static final Color[][] TWO_SQUARES = new Color[][] {
            //@formatter:off
            new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            },
            new Color[] { null           , Color.YELLOW   , Color.YELLOW   , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
    };
    private static final Color[][] THREE_COLUMNS = new Color[][] {
            //@formatter:off
            new Color[] { Color.BLUE     , null           , Color.BLUE     , Color.LIGHTBLUE, null            },
            new Color[] { Color.YELLOW   , null           , Color.WHITE    , Color.PINK     , null            },
            new Color[] { Color.YELLOW   , null           , Color.BLUE     , Color.GREEN    , null            },
            new Color[] { Color.GREEN    , null           , Color.WHITE    , Color.BLUE     , null            },
            new Color[] { Color.YELLOW   , null           , Color.YELLOW   , Color.LIGHTBLUE, null            },
            new Color[] { Color.GREEN    , null           , Color.YELLOW   , Color.LIGHTBLUE, null            },
            //@formatter:on
    };
    private static final Color[][] EIGHT_EQUAL_TILES = new Color[][] {
            //@formatter:off
            new Color[] { Color.LIGHTBLUE, null           , null           , null           , Color.LIGHTBLUE },
            new Color[] { Color.LIGHTBLUE, null           , null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, null           , Color.LIGHTBLUE, null           , null            },
            new Color[] { null           , null           , null           , null           , Color.LIGHTBLUE },
            new Color[] { null           , Color.LIGHTBLUE, Color.LIGHTBLUE, null           , null            },
            //@formatter:on
    };
    private static final Color[][] DIAGONAL = new Color[][] {
            //@formatter:off
            new Color[] { Color.PINK     , null           , null           , null           , null            },
            new Color[] { null           , Color.PINK     , null           , null           , null            },
            new Color[] { null           , null           , Color.PINK     , null           , null            },
            new Color[] { null           , null           , null           , Color.PINK     , null            },
            new Color[] { null           , null           , null           , null           , Color.PINK     },
            new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
    };
    private static final Color[][] FOUR_ROWS = new Color[][] {
            //@formatter:off
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE     },
            new Color[] { Color.BLUE     , Color.BLUE     , Color.BLUE     , Color.BLUE     , Color.BLUE      },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.YELLOW   , Color.GREEN     },
            new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
    };
    private static final Color[][] TWO_ALL_DIFF_COLUMNS = new Color[][] {
            //@formatter:off
            new Color[] { Color.YELLOW   , null           , Color.PINK     , null           , Color.PINK      },
            new Color[] { Color.GREEN    , null           , Color.GREEN    , null           , Color.GREEN     },
            new Color[] { Color.LIGHTBLUE, null           , Color.LIGHTBLUE, null           , Color.LIGHTBLUE },
            new Color[] { Color.BLUE     , null           , Color.BLUE     , null           , Color.BLUE      },
            new Color[] { Color.PINK     , null           , Color.YELLOW   , null           , Color.YELLOW    },
            new Color[] { Color.WHITE    , null           , Color.WHITE    , null           , Color.WHITE     },
            //@formatter:on
    };
    private static final Color[][] TWO_ALL_DIFF_ROWS = new Color[][] {
            //@formatter:off
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.PINK     , Color.WHITE    , Color.PINK     , Color.WHITE    , Color.PINK     },
            new Color[] { Color.BLUE     , Color.BLUE     , Color.GREEN    , Color.GREEN    , Color.YELLOW    },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE     },
            new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
    };
    private static final Color[][] CROSS = new Color[][] {
            //@formatter:off
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { null           , Color.PINK     , null           , Color.PINK     , null            },
            new Color[] { null           , null           , Color.PINK     , null           , null            },
            new Color[] { null           , Color.PINK     , null           , Color.PINK     , null            },
            new Color[] { null           , null           , null           , null           , null            },
            new Color[] { null           , null           , null           , null           , null            },
            //@formatter:on
    };
    private static final Color[][] TRIANGLE = new Color[][] {
            //@formatter:off
            new Color[] { null           , null           , null           , null           , null             },
            new Color[] { null           , null           , null           , null           , Color.YELLOW     },
            new Color[] { null           , null           , null           , Color.GREEN    , Color.WHITE      },
            new Color[] { null           , null           , Color.GREEN    , Color.YELLOW   , Color.WHITE      },
            new Color[] { null           , Color.YELLOW   , Color.WHITE    , Color.YELLOW   , Color.WHITE      },
            new Color[] { Color.PINK     , Color.GREEN    , Color.PINK     , Color.GREEN    , Color.YELLOW     },
            //@formatter:on
    };

    public TuiCommonGoalPrinter(Type type) {
        this.type = type;
    }

    private static final Map<Type, TuiCommonGoalPrinter> COMMON_GOALS_PRINTERS = Arrays.stream(Type.values())
            .collect(Collectors.toUnmodifiableMap(Function.identity(), TuiCommonGoalPrinter::new));

    private static Color[][] CommonGoalType(Type type) {
        return switch (type) {
            case TWO_SQUARES -> TWO_SQUARES;
            case TWO_ALL_DIFF_COLUMNS -> TWO_ALL_DIFF_COLUMNS;
            case FOUR_QUADRIPLETS -> FOUR_QUADRIPLETS;
            case SIX_COUPLES -> SIX_COUPLES;
            case THREE_COLUMNS -> THREE_COLUMNS;
            case TWO_ALL_DIFF_ROWS -> TWO_ALL_DIFF_ROWS;
            case FOUR_ROWS -> FOUR_ROWS;
            case ALL_CORNERS -> ALL_CORNERS;
            case EIGHT_EQUAL_TILES -> EIGHT_EQUAL_TILES;
            case CROSS -> CROSS;
            case DIAGONAL -> DIAGONAL;
            case TRIANGLE -> TRIANGLE;
        };
    }

    @Override
    public void print(TuiPrintStream out) {
        TuiShelfiePrinter.printShelfieMatrixByColor(out, (row, col) -> CommonGoalType(type)[row][col], (row, col) -> true);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(ShelfieView.ROWS + 1, ShelfieView.COLUMNS * 3);
    }
}
