package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.server.model.ServerCommonGoal;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.server.model.ServerPlayerView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Integer provider which calculates and keeps track of the public score of a player.
 * <p>
 * The private score is defined as the score which should be visible to the other
 * players during the game.
 *
 * @see ServerPlayerView#publicScore()
 */
class PublicScoreProvider implements Provider<Integer> {

    private final List<Integer> COMMON_GOAL_SCORE_BY_POS = List.of(8, 6, 4, 2);

    private final Property<Integer> scoreProperty;

    private final ServerPlayer player;
    private final List<ServerCommonGoal> commonGoals;
    private final Provider<? extends @Nullable ServerPlayerView> firstFinisher;

    public PublicScoreProvider(ServerPlayer player,
                               List<ServerCommonGoal> commonGoals,
                               Provider<? extends @Nullable ServerPlayerView> firstFinisher) {
        this.player = player;
        this.commonGoals = commonGoals;
        this.firstFinisher = firstFinisher;
        this.scoreProperty = new SerializableProperty<>(calculateScore());
        registerObservers();
    }

    private void registerObservers() {
        final Consumer<Object> observer = v -> scoreProperty.set(calculateScore());
        player.getShelfie().tiles().forEach(t -> t.tile().registerObserver(observer));
        firstFinisher.registerObserver(observer);
    }

    private int calculateScore() {
        return getCommonGoalsScore() + getGroupsScore() + getFirstFinisherScore();
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
        int sum = 0;
        for (var group : player.getShelfie().groupsOfTiles()) {
            switch (group.size()) {
                case 0, 1, 2 -> sum += 0;
                case 3 -> sum += 2;
                case 4 -> sum += 3;
                case 5 -> sum += 5;
                default -> sum += 8;
            }
        }
        return sum;
    }

    private int getFirstFinisherScore() {
        return Objects.equals(firstFinisher.get(), player) ? 1 : 0;
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
