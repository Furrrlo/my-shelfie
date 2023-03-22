package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommonGoal implements CommonGoalView {

    private final Type type;

    private final Property<List<Player>> achieved;

    public CommonGoal(Type type) {
        this(type, new ArrayList<>());
    }

    public CommonGoal(Type type, List<Player> achieved) {
        this.type = type;
        this.achieved = new SerializableProperty<>(achieved);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Property<List<Player>> achieved() {
        return achieved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CommonGoal that))
            return false;
        return type == that.type && achieved.equals(that.achieved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, achieved);
    }

    @Override
    public String toString() {
        return "CommonGoal{" +
                "type=" + type +
                ", achieved=" + achieved +
                '}';
    }
}