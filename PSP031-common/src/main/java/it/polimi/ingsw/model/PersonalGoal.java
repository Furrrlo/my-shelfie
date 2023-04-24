package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PersonalGoal implements PersonalGoalView {

    private final @Nullable Tile[][] personalGoal;
    private final int index;

    /** Immutable list containing all possible personal goals assignable to players */
    public static final List<PersonalGoal> PERSONAL_GOALS = List.of(new PersonalGoal(0, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , Color.LIGHTBLUE, null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , Color.ORANGE   , null           , null           , null           },
            new Color[]{null           , null           , null           , Color.YELLOW   , null           },
            new Color[]{null           , null           , null           , null           , Color.GREEN    },
            new Color[]{Color.PINK     , null           , Color.BLUE     , null           , null           },
            //@formatter:on
    }), new PersonalGoal(1, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , null           , null           , Color.BLUE     },
            new Color[]{null           , null           , null           , Color.LIGHTBLUE, null           },
            new Color[]{null           , null           , null           , null           , Color.YELLOW   },
            new Color[]{Color.GREEN    , null           , Color.ORANGE   , null           , null           },
            new Color[]{null           , Color.PINK     , null           , null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            //@formatter:on 
    }), new PersonalGoal(2, new Color[][] {
            //@formatter:off
            new Color[]{Color.YELLOW   , null           , null           , null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , Color.GREEN    , null           , null           , Color.LIGHTBLUE},
            new Color[]{null           , null           , Color.PINK     , null           , null           },
            new Color[]{Color.BLUE     , null           , null           , Color.ORANGE   , null           }, 
            new Color[]{null           , null           , null           , null           , null           },
            //@formatter:on
    }), new PersonalGoal(3, new Color[][] {
        //@formatter:off
        new Color[]{null           , null           , null           , null           , null           },
                new Color[]{null           , Color.YELLOW   , Color.GREEN    , null           , null           },
                new Color[]{null           , null           , null           , Color.PINK     , null           },
                new Color[]{Color.LIGHTBLUE, null           , Color.BLUE     , null           , null           },
                new Color[]{null           , null           , null           , null           , null           },
                new Color[]{null           , null           , null           , null           , Color.ORANGE   },
        //@formatter:on
    }), new PersonalGoal(4, new Color[][] {
            //@formatter:off
            new Color[]{Color.ORANGE   , null           , null           , Color.GREEN    , null           },
            new Color[]{null           , null           , null           , null           , Color.PINK     },
            new Color[]{null           , Color.BLUE     , Color.YELLOW   , null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , Color.LIGHTBLUE, null           , null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            //@formatter:on
    }), new PersonalGoal(5, new Color[][] {
            //@formatter:off
            new Color[]{Color.PINK     , null           , null           , null           , null           },
            new Color[]{null           , Color.ORANGE   , null           , Color.BLUE     , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , null           , null           , Color.YELLOW   , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , null           , Color.LIGHTBLUE, null           , Color.GREEN    },
            //@formatter:on
    }), new PersonalGoal(6, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , Color.YELLOW   , null           , null           },
            new Color[]{null           , null           , null           , null           , Color.ORANGE   },
            new Color[]{Color.LIGHTBLUE, null           , null           , null           , null           },
            new Color[]{null           , Color.PINK     , null           , null           , null           },
            new Color[]{null           , null           , null           , Color.BLUE     , null           },
            new Color[]{Color.GREEN    , null           , null           , null           , null           },
            //@formatter:on
    }), new PersonalGoal(7, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , null           , Color.ORANGE   , null           },
            new Color[]{null           , null           , null           , Color.YELLOW   , null           },
            new Color[]{Color.PINK     , null           , null           , null           , null           },
            new Color[]{null           , null           , Color.LIGHTBLUE, null           , null           },
            new Color[]{null           , Color.GREEN    , null           , null           , null           },
            new Color[]{null           , null           , null           , null           , Color.BLUE     },
            //@formatter:on
    }), new PersonalGoal(8, new Color[][] {
            //@formatter:off
            new Color[]{Color.BLUE     , null           , null           , null           , null           },
            new Color[]{null           , Color.LIGHTBLUE, null           , null           , Color.PINK     },
            new Color[]{null           , null           , null           , null           , Color.YELLOW   },
            new Color[]{null           , null           , Color.GREEN    , null           , null           },
            new Color[]{null           , null           , null           , null           , null           },
            new Color[]{null           , null           , Color.ORANGE   , null           , null           },
            //@formatter:on
    }), new PersonalGoal(9, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , null           , Color.PINK     , null           },
            new Color[]{null           , Color.BLUE     , null           , null           , null           },
            new Color[]{null           , null           , null           , Color.GREEN    , null           },
            new Color[]{Color.YELLOW   , null           , null           , null           , null           },
            new Color[]{null           , Color.ORANGE   , null           , null           , null           },
            new Color[]{null           , null           , null           , null           , Color.LIGHTBLUE},
            //@formatter:on
    }), new PersonalGoal(10, new Color[][] {
            //@formatter:off
            new Color[]{null           , null           , null           , Color.LIGHTBLUE, null           },
            new Color[]{null           , null           , null           , null           , Color.GREEN    },
            new Color[]{null           , null           , Color.BLUE     , null           , null           },
            new Color[]{Color.ORANGE   , null           , null           , null           , null           },
            new Color[]{null           , Color.YELLOW   , null           , null           , null           },
            new Color[]{null           , null           , Color.PINK     , null           , null           },
            //@formatter:on
    }), new PersonalGoal(11, new Color[][] {
            //@formatter:off
            new Color[]{Color.GREEN    , null           , null           , null           , null           },
            new Color[]{null           , null           , null           , null           , Color.ORANGE   },
            new Color[]{null           , null           , null           , Color.LIGHTBLUE, null           },
            new Color[]{null           , null           , Color.BLUE     , null           , null           },
            new Color[]{null           , Color.PINK     , null           , null           , null           },
            new Color[]{null           , null           , Color.YELLOW   , null           , null           },
            //@formatter:on
    }));

    private PersonalGoal(int index, @Nullable Color[][] personalGoal) {
        this(index, colorToTiles(personalGoal));
    }

    /** returns a matrix of Tile[][] from a given matrix of Color[][] passed as parameter */
    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    private static @Nullable Tile[][] colorToTiles(@Nullable Color[][] tiles) {
        return Arrays.stream(tiles)
                .map(row -> Arrays.stream(row)
                        .map(color -> color == null ? null
                                : new Tile(color))
                        .toArray(Tile[]::new))
                .toArray(Tile[][]::new);
    }

    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    private PersonalGoal(int index, @Nullable Tile[][] personalGoal) {
        if (personalGoal.length != ROWS)
            throw new IllegalArgumentException("Provided shelf combination has the wrong row size");
        for (int row = 0; row < personalGoal.length; row++) {
            if (personalGoal[row].length != COLUMNS)
                throw new IllegalArgumentException("Provided shelf combination has the wrong column size at row " + row);
        }
        this.personalGoal = personalGoal;
        this.index = index;
    }

    /**
     * generate a personalGoal corresponding to the element in the specified position of the immutable list personalGoals
     * that contains all possible cases of personal Goals assignable to players
     * 
     * @param index : specifies the index of the personalGoals list from which to extract the personalGoal
     */
    public PersonalGoal(int index) {
        this(index, PERSONAL_GOALS.get(index).personalGoal);
    }

    /** returns true if the shelfie passed as a parameter corresponds to the tiles of the commonGoal */
    @Override
    public boolean achievedPersonalGoal(Shelfie shelfie) {
        return new Shelfie(PERSONAL_GOALS.get(this.index).personalGoal).isOverlapping(shelfie);
    }

    @Override
    public int getIndex() {
        return index;
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
                "personalGoal=" + Arrays.deepToString(personalGoal) +
                '}';
    }
}