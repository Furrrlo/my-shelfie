package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

@Immutable
public class FourQuadripletsCommonGoalChecker implements CommonGoalChecker {

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        return shelfie.groupsOfTiles().stream()
                .filter(group -> group.size() >= 4)
                .count() >= 4;
    }
}
