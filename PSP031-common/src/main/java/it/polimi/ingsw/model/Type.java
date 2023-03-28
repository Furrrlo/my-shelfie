package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.model.ShelfieView.*;

public enum Type implements Serializable {
    SIX_COUPLES{
        final int[][] checked = new int[ROWS][COLUMNS];
        @Override
        public boolean checkCommonGoal(Shelfie shelfie){
            int count=0;
            for(int r=0; r<ROWS-1; r++){
                for(int c=0; c<COLUMNS-1; c++){
                    if(shelfie.tile(r+1,c).equals(shelfie.tile(r,c))&&
                            checked[r][c]==0 && checked[r+1][c]==0){
                        count++;
                        checked[r][c]=1;
                        checked[r+1][c]=1;
                    }
                    if(shelfie.tile(r,c+1).equals(shelfie.tile(r,c))&&
                            checked[r][c]==0 && checked[r+1][c]==0){
                        count++;
                        checked[r][c]=1;
                        checked[r][c+1]=1;
                    }
                }
            }
            return count >= 6;
        }
    },
    ALL_CORNERS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    FOUR_QUADRIPLETS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    TWO_SQUARES{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    THREE_COLUMNS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    EIGHT_EQUAL_TILES{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    DIAGONAL{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    FOUR_ROWS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    TWO_ALL_DIFF_COLUMNS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    TWO_ALL_DIFF_ROWS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    CROSS{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    },
    TRIANGLE{
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            return false;
        }
    };

    /**
     * @return true if the common goal of given type is achieved
     **/
    public abstract boolean checkCommonGoal(Shelfie shelfie);
}