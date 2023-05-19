package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerPlayerTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var player1 = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        final var player2 = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));

        assertEquals(player1, player1, "Same instance is not the same");
        assertNotEquals(player1, new Object(), "Different object should not be equals");
        assertEquals(player1, player2, "Instances with no differences should be equals");

        final var playerDiffNick = new ServerPlayer("test_player_2", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1, playerDiffNick, "Instances with different nick should not be equals");

        final var playerDiffShelfie = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        playerDiffShelfie.getShelfie().tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(player1, playerDiffShelfie, "Instances with different shelfie should not be equals");

        final var playerDiffGoal = new ServerPlayer("test_player_1", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1, playerDiffGoal, "Instances with different goal should not be equals");

        final var playerDiffConnected = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        playerDiffConnected.connected().set(!player1.connected().get());
        assertNotEquals(player1, playerDiffConnected, "Instances with different connected state should not be equals");

        final var playerDiffPublicScore = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(8),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1, playerDiffPublicScore, "Instances with different public score should not be equals");

        final var playerDiffPrivateScore = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(8));
        assertNotEquals(player1, playerDiffPublicScore, "Instances with different private score should not be equals");
    }

    @Test
    void testHashCode() {
        final var player1 = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        final var player2 = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));

        assertEquals(player1.hashCode(), player1.hashCode(), "Same instance is not the same");
        assertEquals(player1.hashCode(), player2.hashCode(), "Instances with no differences should be equals");

        final var playerDiffNick = new ServerPlayer("test_player_2", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1.hashCode(), playerDiffNick.hashCode(), "Instances with different nick should not be equals");

        final var playerDiffShelfie = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        playerDiffShelfie.getShelfie().tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(player1.hashCode(), playerDiffShelfie.hashCode(),
                "Instances with different shelfie should not be equals");

        final var playerDiffGoal = new ServerPlayer("test_player_1", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1.hashCode(), playerDiffGoal.hashCode(), "Instances with different goal should not be equals");

        final var playerDiffConnected = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        playerDiffConnected.connected().set(!player1.connected().get());
        assertNotEquals(player1.hashCode(), playerDiffConnected.hashCode(),
                "Instances with different connected state should not be equals");

        final var playerDiffPublicScore = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(8),
                p -> new SerializableProperty<>(0));
        assertNotEquals(player1.hashCode(), playerDiffPublicScore.hashCode(),
                "Instances with different public score should not be equals");

        final var playerDiffPrivateScore = new ServerPlayer("test_player_1", new PersonalGoal(1),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(8));
        assertNotEquals(player1.hashCode(), playerDiffPrivateScore.hashCode(),
                "Instances with different private score should not be equals");
    }

    @Test
    void testToString() {
        final var player = new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0));
        assertDoesNotThrow(player::toString);
    }
}