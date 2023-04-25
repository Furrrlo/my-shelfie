package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

class TuiGameSceneTest {

    static TuiPrintStream out = new TuiPrintStream(System.out, System.console() != null
            ? System.console().charset()
            : Charset.defaultCharset());

    @Test
    void printColoredShelfie() {
        Color[][] matrix = {
                //@formatter:off
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.LIGHTBLUE, Color.GREEN , Color.GREEN     },
                new Color[] { Color.PINK     , Color.GREEN , Color.PINK     , Color.WHITE , Color.YELLOW    },
                new Color[] { Color.GREEN    , Color.GREEN , Color.WHITE    , Color.GREEN , Color.GREEN     },
                new Color[] { Color.BLUE     , Color.GREEN , Color.YELLOW   , Color.BLUE  , Color.BLUE      },
                new Color[] { Color.LIGHTBLUE, Color.PINK  , Color.GREEN    , Color.YELLOW, Color.LIGHTBLUE },
                new Color[] { Color.LIGHTBLUE, Color.PINK  , Color.WHITE    , Color.BLUE  , Color.LIGHTBLUE }
                //@formatter:on
        };
        TuiGameScene.printShelfie(out, new Shelfie(matrix));
    }

    @Test
    void printPersonalGoal() {
        PersonalGoal p2 = new PersonalGoal(0);
        TuiGameScene.printPersonalGoal(out, p2);
    }

    @Test
    void printAchieved() {
        PersonalGoal p0 = new PersonalGoal(0);
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[]{Color.WHITE    , null           , Color.LIGHTBLUE, Color.WHITE    , Color.GREEN    },
                new Color[]{null           , null           , null           , Color.WHITE    , Color.GREEN    },
                new Color[]{null           , null           , null           , null           , null           },
                new Color[]{null           , null           , Color.PINK     , Color.WHITE    , null           },
                new Color[]{null           , null           , null           , null           , Color.GREEN    },
                new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
                //@formatter:on
        });
        TuiGameScene.printPersonalGoalOnShelfie(out, p0, shelfie);
    }
}