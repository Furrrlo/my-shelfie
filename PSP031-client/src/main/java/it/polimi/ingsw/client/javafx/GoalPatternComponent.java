package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalView;
import it.polimi.ingsw.model.PersonalGoalView;

import javafx.scene.layout.Pane;

public class GoalPatternComponent extends Pane {
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
            new Color[] { Color.PINK     , Color.PINK     , Color.LIGHTBLUE, Color.LIGHTBLUE, Color.PINK      },
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
            new Color[] { null           , null           , null           , null           , null            },
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
    public ColorTileComponent t0x0;
    public ColorTileComponent t0x1;
    public ColorTileComponent t0x2;
    public ColorTileComponent t0x3;
    public ColorTileComponent t0x4;
    public ColorTileComponent t1x0;
    public ColorTileComponent t1x1;
    public ColorTileComponent t1x2;
    public ColorTileComponent t1x3;
    public ColorTileComponent t1x4;
    public ColorTileComponent t2x0;
    public ColorTileComponent t2x2;
    public ColorTileComponent t2x1;
    public ColorTileComponent t2x3;
    public ColorTileComponent t2x4;
    public ColorTileComponent t3x0;
    public ColorTileComponent t3x1;
    public ColorTileComponent t3x2;
    public ColorTileComponent t3x3;
    public ColorTileComponent t3x4;
    public ColorTileComponent t4x0;
    public ColorTileComponent t4x1;
    public ColorTileComponent t4x2;
    public ColorTileComponent t4x3;
    public ColorTileComponent t4x4;
    public ColorTileComponent t5x0;
    public ColorTileComponent t5x1;
    public ColorTileComponent t5x2;
    public ColorTileComponent t5x3;
    public ColorTileComponent t5x4;

    public GoalPatternComponent(FxResourcesLoader resources, PersonalGoalView personalGoal) {

        getChildren().add(t0x0 = new ColorTileComponent(resources, personalGoal.get(0, 0)));
        getChildren().add(t0x1 = new ColorTileComponent(resources, personalGoal.get(0, 1)));
        getChildren().add(t0x2 = new ColorTileComponent(resources, personalGoal.get(0, 2)));
        getChildren().add(t0x3 = new ColorTileComponent(resources, personalGoal.get(0, 3)));
        getChildren().add(t0x4 = new ColorTileComponent(resources, personalGoal.get(0, 4)));
        getChildren().add(t1x0 = new ColorTileComponent(resources, personalGoal.get(1, 0)));
        getChildren().add(t1x1 = new ColorTileComponent(resources, personalGoal.get(1, 1)));
        getChildren().add(t1x2 = new ColorTileComponent(resources, personalGoal.get(1, 2)));
        getChildren().add(t1x3 = new ColorTileComponent(resources, personalGoal.get(1, 3)));
        getChildren().add(t1x4 = new ColorTileComponent(resources, personalGoal.get(1, 4)));
        getChildren().add(t2x0 = new ColorTileComponent(resources, personalGoal.get(2, 0)));
        getChildren().add(t2x2 = new ColorTileComponent(resources, personalGoal.get(2, 2)));
        getChildren().add(t2x1 = new ColorTileComponent(resources, personalGoal.get(2, 1)));
        getChildren().add(t2x3 = new ColorTileComponent(resources, personalGoal.get(2, 3)));
        getChildren().add(t2x4 = new ColorTileComponent(resources, personalGoal.get(2, 4)));
        getChildren().add(t3x0 = new ColorTileComponent(resources, personalGoal.get(3, 0)));
        getChildren().add(t3x1 = new ColorTileComponent(resources, personalGoal.get(3, 1)));
        getChildren().add(t3x2 = new ColorTileComponent(resources, personalGoal.get(3, 2)));
        getChildren().add(t3x3 = new ColorTileComponent(resources, personalGoal.get(3, 3)));
        getChildren().add(t3x4 = new ColorTileComponent(resources, personalGoal.get(3, 4)));
        getChildren().add(t4x0 = new ColorTileComponent(resources, personalGoal.get(4, 0)));
        getChildren().add(t4x1 = new ColorTileComponent(resources, personalGoal.get(4, 1)));
        getChildren().add(t4x2 = new ColorTileComponent(resources, personalGoal.get(4, 2)));
        getChildren().add(t4x3 = new ColorTileComponent(resources, personalGoal.get(4, 3)));
        getChildren().add(t4x4 = new ColorTileComponent(resources, personalGoal.get(4, 4)));
        getChildren().add(t5x0 = new ColorTileComponent(resources, personalGoal.get(5, 0)));
        getChildren().add(t5x1 = new ColorTileComponent(resources, personalGoal.get(5, 1)));
        getChildren().add(t5x2 = new ColorTileComponent(resources, personalGoal.get(5, 2)));
        getChildren().add(t5x3 = new ColorTileComponent(resources, personalGoal.get(5, 3)));
        getChildren().add(t5x4 = new ColorTileComponent(resources, personalGoal.get(5, 4)));
    }

    public GoalPatternComponent(FxResourcesLoader resources, CommonGoalView commonGoal) {
        Color[][] shelfie = switch (commonGoal.getType()) {
            case SIX_COUPLES -> SIX_COUPLES;
            case ALL_CORNERS -> ALL_CORNERS;
            case FOUR_QUADRIPLETS -> FOUR_QUADRIPLETS;
            case TWO_SQUARES -> TWO_SQUARES;
            case THREE_COLUMNS -> THREE_COLUMNS;
            case EIGHT_EQUAL_TILES -> EIGHT_EQUAL_TILES;
            case DIAGONAL -> DIAGONAL;
            case FOUR_ROWS -> FOUR_ROWS;
            case TWO_ALL_DIFF_COLUMNS -> TWO_ALL_DIFF_COLUMNS;
            case TWO_ALL_DIFF_ROWS -> TWO_ALL_DIFF_ROWS;
            case CROSS -> CROSS;
            case TRIANGLE -> TRIANGLE;
        };
        getChildren().add(t0x0 = new ColorTileComponent(resources, shelfie[0][0]));
        getChildren().add(t0x1 = new ColorTileComponent(resources, shelfie[0][1]));
        getChildren().add(t0x2 = new ColorTileComponent(resources, shelfie[0][2]));
        getChildren().add(t0x3 = new ColorTileComponent(resources, shelfie[0][3]));
        getChildren().add(t0x4 = new ColorTileComponent(resources, shelfie[0][4]));
        getChildren().add(t1x0 = new ColorTileComponent(resources, shelfie[1][0]));
        getChildren().add(t1x1 = new ColorTileComponent(resources, shelfie[1][1]));
        getChildren().add(t1x2 = new ColorTileComponent(resources, shelfie[1][2]));
        getChildren().add(t1x3 = new ColorTileComponent(resources, shelfie[1][3]));
        getChildren().add(t1x4 = new ColorTileComponent(resources, shelfie[1][4]));
        getChildren().add(t2x0 = new ColorTileComponent(resources, shelfie[2][0]));
        getChildren().add(t2x2 = new ColorTileComponent(resources, shelfie[2][2]));
        getChildren().add(t2x1 = new ColorTileComponent(resources, shelfie[2][1]));
        getChildren().add(t2x3 = new ColorTileComponent(resources, shelfie[2][3]));
        getChildren().add(t2x4 = new ColorTileComponent(resources, shelfie[2][4]));
        getChildren().add(t3x0 = new ColorTileComponent(resources, shelfie[3][0]));
        getChildren().add(t3x1 = new ColorTileComponent(resources, shelfie[3][1]));
        getChildren().add(t3x2 = new ColorTileComponent(resources, shelfie[3][2]));
        getChildren().add(t3x3 = new ColorTileComponent(resources, shelfie[3][3]));
        getChildren().add(t3x4 = new ColorTileComponent(resources, shelfie[3][4]));
        getChildren().add(t4x0 = new ColorTileComponent(resources, shelfie[4][0]));
        getChildren().add(t4x1 = new ColorTileComponent(resources, shelfie[4][1]));
        getChildren().add(t4x2 = new ColorTileComponent(resources, shelfie[4][2]));
        getChildren().add(t4x3 = new ColorTileComponent(resources, shelfie[4][3]));
        getChildren().add(t4x4 = new ColorTileComponent(resources, shelfie[4][4]));
        getChildren().add(t5x0 = new ColorTileComponent(resources, shelfie[5][0]));
        getChildren().add(t5x1 = new ColorTileComponent(resources, shelfie[5][1]));
        getChildren().add(t5x2 = new ColorTileComponent(resources, shelfie[5][2]));
        getChildren().add(t5x3 = new ColorTileComponent(resources, shelfie[5][3]));
        getChildren().add(t5x4 = new ColorTileComponent(resources, shelfie[5][4]));
    }

    protected void layoutChildren() {
        final double _widthScale = getWidth() / 1218d;
        final double _heightScale = getHeight() / 1235d;
        final double scale = Math.min(_widthScale, _heightScale);

        final double widthScale = scale;
        final double heightScale = scale;
        t0x0.resizeRelocate(146.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x1.resizeRelocate(342.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x2.resizeRelocate(537.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x3.resizeRelocate(732.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x4.resizeRelocate(930.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x0.resizeRelocate(148.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x1.resizeRelocate(342.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x2.resizeRelocate(537.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x3.resizeRelocate(730.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x4.resizeRelocate(928.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x0.resizeRelocate(150.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x1.resizeRelocate(342.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x2.resizeRelocate(537.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x3.resizeRelocate(730.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x4.resizeRelocate(924.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x0.resizeRelocate(154.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x1.resizeRelocate(344.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x2.resizeRelocate(537.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x3.resizeRelocate(730.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x4.resizeRelocate(922.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x0.resizeRelocate(156.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x1.resizeRelocate(346.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x2.resizeRelocate(537.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x3.resizeRelocate(730.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x4.resizeRelocate(915.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x0.resizeRelocate(156.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x1.resizeRelocate(346.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x2.resizeRelocate(537.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x3.resizeRelocate(730.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x4.resizeRelocate(915.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
    }
}
