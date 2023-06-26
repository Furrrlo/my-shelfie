package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#FOUR_QUADRIPLETS}
 */
@Immutable
public class FourQuadripletsCommonGoalChecker implements CommonGoalChecker {

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        return shelfie.groupsOfTiles().stream()
                .filter(group -> group.size() >= 4)
                .count() >= 4;
    }
}
