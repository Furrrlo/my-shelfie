package it.polimi.ingsw.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public class CommonGoal implements CommonGoalView {

    private final Type type;

    private final Property<@Unmodifiable List<Player>> achieved;

    public CommonGoal(Type type) {
        this(type, List.of());
    }

    public CommonGoal(Type type, List<Player> achieved) {
        this.type = type;
        this.achieved = new SerializableProperty<>(List.copyOf(achieved));
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Property<@Unmodifiable List<Player>> achieved() {
        return achieved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CommonGoal that))
            return false;
        return type == that.type && achieved.get().equals(that.achieved.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, achieved.get());
    }

    @Override
    public String toString() {
        return "CommonGoal{" +
                "type=" + type +
                ", achieved=" + achieved +
                '}';
    }
}