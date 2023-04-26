package it.polimi.ingsw.model;

import com.google.errorprone.annotations.Immutable;

@Immutable
public interface CommonGoalChecker {

    boolean checkCommonGoal(Shelfie shelfie);
}
