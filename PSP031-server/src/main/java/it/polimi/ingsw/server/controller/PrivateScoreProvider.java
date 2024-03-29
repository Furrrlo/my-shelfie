package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.server.model.ServerPlayerView;

import java.util.List;
import java.util.function.Consumer;

/**
 * Integer provider which calculates and keeps track of the private score of a player.
 * <p>
 * The private score is defined as the score which should only be visible to the player
 * itself during the game and to everyone at the end of the game.
 *
 * @see ServerPlayerView#privateScore()
 */
class PrivateScoreProvider implements Provider<Integer> {

    private final List<Integer> PERSONAL_GOAL_SCORE_BY_NUM = List.of(
            /* 0 -> */ 0,
            /* 1 -> */ 1,
            /* 2 -> */ 2,
            /* 3 -> */ 4,
            /* 4 -> */ 6,
            /* 5 -> */ 9,
            /* 6 -> */ 12);

    private final Property<Integer> scoreProperty;

    private final ServerPlayer player;

    public PrivateScoreProvider(ServerPlayer player) {
        this.player = player;
        this.scoreProperty = new SerializableProperty<>(calculateScore());
        registerObservers();
    }

    private void registerObservers() {
        final Consumer<Object> observer = v -> scoreProperty.set(calculateScore());
        player.getShelfie().tiles().forEach(t -> t.tile().registerObserver(observer));
        player.publicScore().registerObserver(observer);
    }

    private int calculateScore() {
        return player.publicScore().get() + getPersonalGoalScore();
    }

    private int getPersonalGoalScore() {
        return PERSONAL_GOAL_SCORE_BY_NUM
                .get(player.getShelfie().numTilesOverlappingWithPersonalGoal(player.getPersonalGoal()));
    }

    @Override
    public Integer get() {
        return scoreProperty.get();
    }

    @Override
    public void registerObserver(Consumer<? super Integer> o) {
        scoreProperty.registerObserver(o);
    }

    @Override
    public void registerWeakObserver(Consumer<? super Integer> o) {
        scoreProperty.registerWeakObserver(o);
    }

    @Override
    public void unregisterObserver(Consumer<? super Integer> o) {
        scoreProperty.unregisterObserver(o);
    }
}
