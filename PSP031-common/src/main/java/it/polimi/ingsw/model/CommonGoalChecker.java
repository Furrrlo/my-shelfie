package it.polimi.ingsw.model;

import com.google.errorprone.annotations.Immutable;

/**
 * Interface in charge of checking whether a given {@link Shelfie} has achieved a common goal.
 * <p>
 * Each implementing object is in charge of checking for one specific common goal type
 */
@Immutable
public interface CommonGoalChecker {

    boolean checkCommonGoal(Shelfie shelfie);
}
