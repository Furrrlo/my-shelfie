package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.*;

import java.io.Serializable;

public enum Type implements Serializable {
    SIX_COUPLES(new SixCouplesCommonGoalChecker(), "Six groups each containing at least\n" +
            "2 tiles of the same type (not necessarily in the depicted shape).\n" +
            "The tiles of one group can be different from those of another group."),
    ALL_CORNERS(new AllCornersCommonGoalChecker(), "Four tiles of the same type in the four corners of the bookshelf."),
    FOUR_QUADRIPLETS(new FourQuadripletsCommonGoalChecker(), "Four groups each containing at least\n" +
            "4 tiles of the same type (not necessarily in the depicted shape).\n" +
            "The tiles of one group can be different from those of another group."),
    TWO_SQUARES(new TwoSquaresCommonGoalChecker(), "Two groups each containing 4 tiles of the same type in a 2x2 square.\n " +
            "The tiles of one square can be different from those of the other square."),
    THREE_COLUMNS(new ThreeColumnsCommonGoalChecker(), "Three columns each formed by 6 tiles\n" +
            "of maximum three different types. One column can show the same or a different combination of another column."),
    EIGHT_EQUAL_TILES(new EightEqualTilesCommonGoalChecker(), "Eight tiles of the same type.\n" +
            "There’s no restriction about the position of these tiles."),
    DIAGONAL(new DiagonalCommonGoalChecker(), "Five tiles of the same type forming a diagonal."),
    FOUR_ROWS(new FourRowsCommonGoalChecker(), "Four lines each formed by 5 tiles of maximum three different types.\n" +
            " One line can show the same or a different combination of another line."),
    TWO_ALL_DIFF_COLUMNS(new TwoAllDiffColumnsCommonGoalChecker(), "Two columns each formed by 6 different types of tiles."),
    TWO_ALL_DIFF_ROWS(new TwoAllDiffRowsCommonGoalChecker(), "Two lines each formed by 5 different types of tiles.\n" +
            " One line can show the same or a different combination of the other line."),
    CROSS(new CrossCommonGoalChecker(), "Five tiles of the same type forming an X"),
    TRIANGLE(new TriangleCommonGoalChecker(), "Five columns of increasing or decreasing height.\n" +
            "Starting from the first column on the left or on the right, each next column must be made of exactly one more tile.\n"
            +
            "Tiles can be of any type.");

    private final CommonGoalChecker checker;
    private final String description;

    Type(CommonGoalChecker checker, String description) {
        this.checker = checker;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns true if the common goal of given type is achieved
     **/
    public boolean checkCommonGoal(Shelfie shelfie) {
        return checker.checkCommonGoal(shelfie);
    }
}