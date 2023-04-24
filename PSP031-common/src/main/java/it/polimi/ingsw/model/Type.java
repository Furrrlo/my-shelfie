package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.*;

import java.io.Serializable;

public enum Type implements Serializable {
    SIX_COUPLES(new SixCouplesCommonGoalChecker()),
    ALL_CORNERS(new AllCornersCommonGoalChecker()),
    FOUR_QUADRIPLETS(new FourQuadripletsCommonGoalChecker()),
    TWO_SQUARES(new TwoSquaresCommonGoalChecker()),
    THREE_COLUMNS(new ThreeColumnsCommonGoalChecker()),
    EIGHT_EQUAL_TILES(new EightEqualTilesCommonGoalChecker()),
    DIAGONAL(new DiagonalCommonGoalChecker()),
    FOUR_ROWS(new FourRowsCommonGoalChecker()),

    TWO_ALL_DIFF_COLUMNS(new TwoAllDiffColumnsCommonGoalChecker()),
    TWO_ALL_DIFF_ROWS(new TwoAllDiffRowsCommonGoalChecker()),
    CROSS(new CrossCommonGoalChecker()),
    TRIANGLE(new TriangleCommonGoalChecker());

    private final CommonGoalChecker checker;

    Type(CommonGoalChecker checker) {
        this.checker = checker;
    }

    /**
     * Returns true if the common goal of given type is achieved
     **/
    public boolean checkCommonGoal(Shelfie shelfie) {
        return checker.checkCommonGoal(shelfie);
    }
}