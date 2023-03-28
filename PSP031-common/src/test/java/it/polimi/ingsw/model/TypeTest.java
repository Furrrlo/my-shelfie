package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    @Test
    void checkCommonGoal() {
        /*Tile tiles1[][] = {
                new Tile[]{ new Tile(Color.GREEN), new Tile(Color.GREEN), new Tile(Color.BLUE), new Tile(Color.YELLOW), new Tile(Color.PINK)},
                new Tile[]{ new Tile(Color.GREEN), new Tile(Color.GREEN), new Tile(Color.BLUE), new Tile(Color.YELLOW), new Tile(Color.PINK)},
                new Tile[]{ new Tile(Color.YELLOW), new Tile(Color.YELLOW), new Tile(), new Tile(), new Tile()},
                new Tile[]{ new Tile(Color.YELLOW), new Tile(Color.YELLOW), new Tile(), new Tile(), new Tile()},
                new Tile[]{ new Tile(Color.GREEN), new Tile(Color.GREEN), new Tile(), new Tile(), new Tile()},
                new Tile[]{ new Tile(Color.GREEN), new Tile(Color.GREEN), new Tile(), new Tile(), new Tile()}
        };*/
        Color[][] tilesNull = {
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null}
        };
        Color[][] singleQuadriplet = {
                new Color[]{ Color.GREEN, Color.GREEN, null, null, null},
                new Color[]{ null, Color.GREEN, Color.GREEN, Color.GREEN, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null}
        };
        Color[][] fourQuadriplet = {
                new Color[]{ Color.GREEN, Color.GREEN, null, null, null},
                new Color[]{ Color.PINK, Color.GREEN, Color.GREEN, null, null},
                new Color[]{ Color.PINK, Color.PINK, null, null, Color.BLUE},
                new Color[]{ Color.PINK, null, null, null, Color.BLUE},
                new Color[]{ Color.ORANGE, Color.ORANGE, null, null, Color.BLUE},
                new Color[]{ Color.ORANGE, Color.ORANGE, null, null, Color.BLUE}
        };
        Color[][] fourQuadripletGOOD = {
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE}
        };

        //assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(tilesNull)));
        //assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(singleQuadriplet)));
        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(fourQuadriplet)));
    }
}