package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    @Test
    void testExample() {
        assertDoesNotThrow(Type.TRIANGLE::getExample);
    }

    @Test
    void testImmutableMatrix() {
        var m = new Type.ImmutableColorMatrix(new Color[][] {
                new Color[] { Color.GREEN, null }
        });
        assertEquals(Color.GREEN, m.get(0, 0));
        assertNull(m.get(0, 1));
    }

}