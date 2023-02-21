package it.polimi.shelfie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    public void test() {
        assertTrue(true, "Preciso tutto ok");
    }

    @Test
    public void testFailure() {
        fail("This should fail");
    }
}