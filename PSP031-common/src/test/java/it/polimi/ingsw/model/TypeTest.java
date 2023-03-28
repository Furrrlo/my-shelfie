package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    @Test
    void checkCommonGoal_Four_QuadripletsNull() {

        Color[][] tilesNull = {
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null}
        };
        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(tilesNull)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsSingleQUadriplet() {
    
        Color[][] singleQuadriplet = {
                new Color[]{ Color.GREEN, Color.GREEN, null, null, null},
                new Color[]{ null, Color.GREEN, Color.GREEN, Color.GREEN, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null},
                new Color[]{ null, null, null, null, null}
        };

        assertFalse(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(singleQuadriplet)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsEasy() {
    
        Color[][] fourQuadripletEasy = {
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.PINK, null, Color.ORANGE, Color.BLUE}
        };

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(fourQuadripletEasy)));
    }

    @Test
    void checkCommonGoal_Four_QuadripletsNormal() {
    
        Color[][] fourQuadripletNormal = {
                new Color[]{ Color.GREEN, Color.GREEN, Color.GREEN, Color.LIGHTBLUE, Color.BLUE},
                new Color[]{ Color.GREEN, Color.ORANGE, Color.LIGHTBLUE, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.PINK, Color.ORANGE, Color.YELLOW, Color.ORANGE, Color.BLUE},
                new Color[]{ Color.PINK, Color.PINK, null, null, Color.BLUE},
                new Color[]{ Color.PINK, Color.YELLOW, null, Color.ORANGE, null},
                new Color[]{ null, null, null, null, null}
        };

        assertTrue(Type.FOUR_QUADRIPLETS.checkCommonGoal( new Shelfie(fourQuadripletNormal)));
    }

}
