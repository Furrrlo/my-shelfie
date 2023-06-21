package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testForEveryone() {
        var message = UserMessage.forEveryone("p1", "", "text");
        var message2 = new UserMessage("p1", "", "text", UserMessage.EVERYONE_RECIPIENT, "");
        assertEquals(message, message2);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertEquals(message, message);
        assertNotEquals(message, new Object());

        final var message2 = new UserMessage("p1", "", "message", "p2", "");
        assertEquals(message, message2);

        final var diffSender = new UserMessage("p2", "", "message", "p2", "");
        assertNotEquals(message, diffSender);

        final var diffRecipient = new UserMessage("p1", "", "message", "p3", "");
        assertNotEquals(message, diffRecipient);

        final var diffText = new UserMessage("p1", "", "text", "p2", "");
        assertNotEquals(message, diffText);
    }

    @Test
    void testHashCode() {
        final var message = new UserMessage("p1", "", "message", "p2", "");
        assertEquals(message.hashCode(), message.hashCode());
        assertNotEquals(message.hashCode(), new Object().hashCode());

        final var message2 = new UserMessage("p1", "", "message", "p2", "");
        assertEquals(message.hashCode(), message2.hashCode());

        final var diffSender = new UserMessage("p2", "", "message", "p2", "");
        assertNotEquals(message.hashCode(), diffSender.hashCode());

        final var diffRecipient = new UserMessage("p1", "", "message", "p3", "");
        assertNotEquals(message.hashCode(), diffRecipient.hashCode());

        final var diffText = new UserMessage("p1", "", "text", "p2", "");
        assertNotEquals(message.hashCode(), diffText.hashCode());
    }
}