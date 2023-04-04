package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.server.model.ServerCommonGoal;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.server.model.ServerPlayerView;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

class ScoreProvider implements Provider<Integer>, Serializable {

    private final List<Integer> COMMON_GOAL_SCORE_BY_POS = List.of(8, 6, 4, 2);
    //@formatter:off
    private final List<Integer> GROUP_GOAL_SCORE_BY_SIZE = List.of(
            /* 0  */ 0,
            /* 1  */ 0,
            /* 2  */ 0,
            /* 3  */ 2,
            /* 4  */ 3,
            /* 5  */ 5,
            /* 6+ */ 8
    );
    //@formatter:on

    private final Property<Integer> scoreProperty;

    private final ServerPlayer player;
    private final List<ServerCommonGoal> commonGoals;
    private final Provider<? extends ServerPlayerView> firstFinisher;

    public ScoreProvider(ServerPlayer player,
                         List<ServerCommonGoal> commonGoals,
                         Provider<? extends ServerPlayerView> firstFinisher) {
        this.player = player;
        this.commonGoals = commonGoals;
        this.firstFinisher = firstFinisher;
        this.scoreProperty = new SerializableProperty<>(calculateScore());
        registerObservers();
    }

    @Serial
    private Object readResolve() throws ObjectStreamException {
        registerObservers();
        return this;
    }

    private void registerObservers() {
        final Consumer<Object> observer = v -> scoreProperty.set(calculateScore());
        player.getShelfie().tiles().forEach(t -> t.tile().registerObserver(observer));
        firstFinisher.registerObserver(observer);
    }

    private int calculateScore() {
        return getPersonalGoalScore() + getCommonGoalsScore() + getGroupsScore() + getFirstFinisherScore();
    }

    private int getPersonalGoalScore() {
        return 0; // TODO: given the shelfie and the personal goal, calculate the matches
    }

    private int getCommonGoalsScore() {
        return commonGoals.stream().mapToInt(goal -> {
            var playersAchieved = goal.achieved().get();
            return IntStream.range(0, playersAchieved.size())
                    .filter(idx -> playersAchieved.get(idx).equals(player))
                    .map(COMMON_GOAL_SCORE_BY_POS::get)
                    .findFirst()
                    .orElseGet(() -> {
                        boolean hasAchievedIt = goal.getType().checkCommonGoal(player.getShelfie());
                        if (hasAchievedIt) {
                            goal.achieved().update(old -> {
                                var newAchieved = new ArrayList<>(old);
                                newAchieved.add(player);
                                return Collections.unmodifiableList(newAchieved);
                            });
                            return COMMON_GOAL_SCORE_BY_POS.get(goal.achieved().get().size() - 1);
                        }

                        return 0;
                    });
        }).sum();
    }

    private int getGroupsScore() {
        return 0; // TODO: given the shelfie, calculate the groups and the score for each
    }

    private int getFirstFinisherScore() {
        return firstFinisher.get().equals(player) ? 1 : 0;
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
    public void unregisterObserver(Consumer<? super Integer> o) {
        scoreProperty.unregisterObserver(o);
    }
}
