package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PersonalGoal implements PersonalGoalView {

    private final @Nullable Tile[][] personalGoal;

    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    public PersonalGoal(@Nullable Tile[][] personalGoal) {
        if (personalGoal.length != ROWS)
            throw new IllegalArgumentException("Provided shelf combination has the wrong row size");
        for (int row = 0; row < personalGoal.length; row++) {
            if (personalGoal[row].length != COLUMNS)
                throw new IllegalArgumentException("Provided shelf combination has the wrong column size at row " + row);
        }

        this.personalGoal = personalGoal;
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    public @Nullable Tile get(int r, int c) {
        return personalGoal[r][c];
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    public Stream<TileAndCoords<@Nullable Tile>> tiles() {
        return IntStream.range(0, ROWS).boxed().flatMap(row -> IntStream.range(0, COLUMNS).boxed()
                .map(col -> new TileAndCoords<>(personalGoal[row][col], row, col)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PersonalGoal that))
            return false;
        return Arrays.deepEquals(personalGoal, that.personalGoal);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(personalGoal);
    }

    @Override
    public String toString() {
        return "PersonalGoal{" +
                "personalGoal=" + Arrays.toString(personalGoal) +
                '}';
    }
}