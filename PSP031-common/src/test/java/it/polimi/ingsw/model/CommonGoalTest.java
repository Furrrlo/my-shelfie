package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var player = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        final var commonGoal1 = new CommonGoal(Type.CROSS, List.of(player));
        final var commonGoal2 = new CommonGoal(Type.CROSS, List.of(player));

        assertEquals(commonGoal1, commonGoal1, "Same instance is not the same");
        assertNotEquals(commonGoal1, new Object(), "Different object should not be equals");
        assertEquals(commonGoal1, commonGoal2, "Instances with no differences should be equals");

        final var commonGoalDiffType = new CommonGoal(Type.ALL_CORNERS, List.of(player));
        assertNotEquals(commonGoal1, commonGoalDiffType, "Instances with different types should not be equals");

        final var playerDiffAchieved = new CommonGoal(Type.CROSS, List.of(new Player(
                "test_player_2",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0)));
        assertNotEquals(commonGoal1, playerDiffAchieved, "Instances with different achieved players should not be equals");
    }

    @Test
    void testHashCode() {
        final var player = new Player(
                "test_player_1",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0);
        final var commonGoal1 = new CommonGoal(Type.CROSS, List.of(player));
        final var commonGoal2 = new CommonGoal(Type.CROSS, List.of(player));

        assertEquals(commonGoal1.hashCode(), commonGoal1.hashCode(), "Same instance is not the same");
        assertEquals(commonGoal1.hashCode(), commonGoal2.hashCode(), "Instances with no differences should be equals");

        final var commonGoalDiffType = new CommonGoal(Type.ALL_CORNERS, List.of(player));
        assertNotEquals(commonGoal1.hashCode(), commonGoalDiffType.hashCode(),
                "Instances with different types should not be equals");

        final var playerDiffAchieved = new CommonGoal(Type.CROSS, List.of(new Player(
                "test_player_2",
                new Shelfie(),
                false,
                false,
                p -> new SerializableProperty<>(false),
                p -> new SerializableProperty<>(false),
                0)));
        assertNotEquals(commonGoal1.hashCode(), playerDiffAchieved.hashCode(),
                "Instances with different achieved players should not be equals");
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
        final var commonGoal = new CommonGoal(Type.CROSS, List.of(player));
        assertDoesNotThrow(commonGoal::toString);
    }
}