package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var player1 = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        final var player2 = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);

        assertEquals(player1, player1, "Same instance is not the same");
        assertNotEquals(player1, new Object(), "Different object should not be equals");
        assertEquals(player1, player2, "Instances with no differences should be equals");

        final var playerDiffNick = new Player(
                "test_player_2",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1, playerDiffNick, "Instances with different nick should not be equals");

        final var playerDiffShelfie = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        playerDiffShelfie.getShelfie().tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(player1, playerDiffShelfie, "Instances with different shelfie should not be equals");

        final var playerDiffConnected = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                true,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1, playerDiffConnected, "Instances with different connected state should not be equals");

        final var playerDiffStartingPlayer = new Player(
                "test_player_1",
                new Shelfie(),
                true,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1, playerDiffStartingPlayer,
                "Instances with different starting player state should not be equals");

        final var playerDiffCurrentTurn = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(true),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1, playerDiffCurrentTurn, "Instances with different current turn state should not be equals");

        final var playerDiffFirstFinisher = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(true),
                0);
        assertNotEquals(player1, playerDiffFirstFinisher, "Instances with different first finisher state should not be equals");

        final var playerDiffScore = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                8);
        assertNotEquals(player1, playerDiffScore, "Instances with different score should not be equals");
    }

    @Test
    void testHashCode() {
        final var player1 = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        final var player2 = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);

        assertEquals(player1.hashCode(), player1.hashCode(), "Same instance is not the same");
        assertEquals(player1.hashCode(), player2.hashCode(), "Instances with no differences should be equals");

        final var playerDiffNick = new Player(
                "test_player_2",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1.hashCode(), playerDiffNick.hashCode(), "Instances with different nick should not be equals");

        final var playerDiffShelfie = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        playerDiffShelfie.getShelfie().tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(player1.hashCode(), playerDiffShelfie.hashCode(),
                "Instances with different shelfie should not be equals");

        final var playerDiffConnected = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                true,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1.hashCode(), playerDiffConnected.hashCode(),
                "Instances with different connected state should not be equals");

        final var playerDiffStartingPlayer = new Player(
                "test_player_1",
                new Shelfie(),
                true,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1.hashCode(), playerDiffStartingPlayer.hashCode(),
                "Instances with different starting player state should not be equals");

        final var playerDiffCurrentTurn = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(true),
                p -> new SerializableProperty<>(false),
                0);
        assertNotEquals(player1.hashCode(), playerDiffCurrentTurn.hashCode(),
                "Instances with different current turn state should not be equals");

        final var playerDiffFirstFinisher = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(true),
                0);
        assertNotEquals(player1.hashCode(), playerDiffFirstFinisher.hashCode(),
                "Instances with different first finisher state should not be equals");

        final var playerDiffScore = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                8);
        assertNotEquals(player1.hashCode(), playerDiffScore.hashCode(), "Instances with different score should not be equals");
    }

    @Test
    void testToString() {
        final var player = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        assertDoesNotThrow(player::toString);
    }
}