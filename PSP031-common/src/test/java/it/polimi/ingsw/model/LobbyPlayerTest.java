package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyPlayerTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var player1 = new LobbyPlayer("test_player_1", true);
        final var player2 = new LobbyPlayer("test_player_1", true);

        assertEquals(player1, player1, "Same instance is not the same");
        assertNotEquals(player1, new Object(), "Different object should not be equals");
        assertNotEquals(player1, null, "instance should not be equal to null");
        assertEquals(player1, player2, "Instances with no differences should be equals");

        final var playerDiffNick = new LobbyPlayer("test_player_2", true);
        assertNotEquals(player1, playerDiffNick, "Instances with different joined players should not be equals");

        final var playerDiffReady = new LobbyPlayer("test_player_1", false);
        assertNotEquals(player1, playerDiffReady, "Instances with different ready states should not be equals");
    }

    @Test
    void testHashCode() {
        final var player1 = new LobbyPlayer("test_player_1", true);
        final var player2 = new LobbyPlayer("test_player_1", true);

        assertEquals(player1.hashCode(), player1.hashCode(), "Same instance is not the same");
        assertEquals(player1.hashCode(), player2.hashCode(), "Instances with no differences should be equals");

        final var playerDiffNick = new LobbyPlayer("test_player_2", true);
        assertNotEquals(player1.hashCode(), playerDiffNick.hashCode(),
                "Instances with different joined players should not be equals");

        final var playerDiffReady = new LobbyPlayer("test_player_1", false);
        assertNotEquals(player1.hashCode(), playerDiffReady.hashCode(),
                "Instances with different ready states should not be equals");
    }

    @Test
    void testToString() {
        final var player = new LobbyPlayer("test_player", false);
        assertDoesNotThrow(player::toString);
    }
}