package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerCommonGoalTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var goal1 = new ServerCommonGoal(Type.CROSS);
        goal1.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        final var goal2 = new ServerCommonGoal(Type.CROSS);
        goal2.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));

        assertEquals(goal1, goal1, "Same instance is not the same");
        assertNotEquals(goal1, new Object(), "Different object should not be equals");
        assertEquals(goal1, goal2, "Instances with no differences should be equals");

        final var goalDiffType = new ServerCommonGoal(Type.SIX_COUPLES);
        goalDiffType.achieved().update(l -> List.of(new ServerPlayer("test_player",
                new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        assertNotEquals(goal1, goalDiffType, "Instances with a different type should not be equals");

        final var goalDiffAchieved = new ServerCommonGoal(Type.CROSS);
        goalDiffAchieved.achieved().update(l -> List.of(new ServerPlayer("test_player_1", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        assertNotEquals(goal1, goalDiffAchieved, "Instances with a different achieved list should not be equals");
    }

    @Test
    void testHashCode() {
        final var goal1 = new ServerCommonGoal(Type.CROSS);
        goal1.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        final var goal2 = new ServerCommonGoal(Type.CROSS);
        goal2.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));

        assertEquals(goal1.hashCode(), goal1.hashCode(), "Same instance is not the same");
        assertEquals(goal1.hashCode(), goal2.hashCode(), "Instances with no differences should be equals");

        final var goalDiffType = new ServerCommonGoal(Type.SIX_COUPLES);
        goalDiffType.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        assertNotEquals(goal1.hashCode(), goalDiffType.hashCode(), "Instances with a different type should not be equals");

        final var goalDiffAchieved = new ServerCommonGoal(Type.CROSS);
        goalDiffAchieved.achieved().update(l -> List.of(new ServerPlayer("test_player_1", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        assertNotEquals(goal1.hashCode(), goalDiffAchieved.hashCode(),
                "Instances with a different achieved list should not be equals");
    }

    @Test
    void testToString() {
        final var goal = new ServerCommonGoal(Type.CROSS);
        goal.achieved().update(l -> List.of(new ServerPlayer("test_player", new PersonalGoal(2),
                p -> new SerializableProperty<>(0),
                p -> new SerializableProperty<>(0))));
        assertDoesNotThrow(goal::toString);
    }
}