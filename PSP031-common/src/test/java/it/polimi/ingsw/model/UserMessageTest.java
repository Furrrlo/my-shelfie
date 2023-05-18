package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserMessageTest {
    @Test
    void testToConsoleString() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertDoesNotThrow(message::toConsoleString);
        final var message1 = new UserMessage("p1", "", "message", UserMessage.EVERYONE_RECIPIENT, "");
        assertDoesNotThrow(message1::toConsoleString);
    }

    @Test
    void testInstanceOf() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertInstanceOf(UserMessage.class, message);
    }
}