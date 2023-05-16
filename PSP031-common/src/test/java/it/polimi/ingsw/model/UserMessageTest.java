package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserMessageTest {
    @Test
    void testToString() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertDoesNotThrow(message::toString);
        final var message1 = new UserMessage("p1", "", "message", "all", "");
        assertDoesNotThrow(message1::toString);
    }

    @Test
    void testInstanceOf() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertInstanceOf(UserMessage.class, message);
    }
}