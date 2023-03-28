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
        record Index(int r, int c) {
        }
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            int[][] checked = new int[ROWS][COLUMNS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    if (checked[r][c] == 0 && shelfie.tile(r,c).get()!=null) {
                        if(getCheckedForTiles(shelfie, r, c, checked)>=4)
                            count++;
                    }
                }
            }
            return count>=4;
        }
        /**
         * @param shelfie
         * @return number of reached tiles and checks the inspected ones
         */
        public int getCheckedForTiles(Shelfie shelfie, int row, int col, int[][] checked ){
            List<Index> indexes = new ArrayList<>();
            indexes.add(new Index(row,col));
            checked[row][col] = 1;
            int prevSize = 0;
            do {
                prevSize = indexes.size();
                for (int i = 0; i < indexes.size(); i++) {
                    if ( indexes.get(i).r < ROWS-2 && Objects.equals(shelfie.tile(indexes.get(i).r + 1, indexes.get(i).c), shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r + 1, indexes.get(i).c))) {
                        indexes.add(new Index(indexes.get(i).r + 1, indexes.get(i).c));
                        checked[indexes.get(i).r + 1][indexes.get(i).c] = 1;
                    }
                    if (indexes.get(i).c < COLUMNS-2 && Objects.equals(shelfie.tile(indexes.get(i).r, indexes.get(i).c + 1), shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r, indexes.get(i).c + 1))) {
                        indexes.add(new Index(indexes.get(i).r, indexes.get(i).c + 1));
                        checked[indexes.get(i).r][indexes.get(i).c+1] = 1;
                    }
                    if (indexes.get(i).r > 0 && shelfie.tile(indexes.get(i).r - 1, indexes.get(i).c) == shelfie.tile(indexes.get(i).r, indexes.get(i).c)
                            && !indexes.contains(new Index(indexes.get(i).r - 1, indexes.get(i).c))) {
                        indexes.add(new Index(indexes.get(i).r - 1, indexes.get(i).c));
                        checked[indexes.get(i).r-1][indexes.get(i).c] = 1;
                    }
                    if (indexes.get(i).c > 0 && Objects.equals(shelfie.tile(indexes.get(i).r, indexes.get(i).c - 1), shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r, indexes.get(i).c - 1))) {
                        indexes.add(new Index(indexes.get(i).r, indexes.get(i).c - 1));
                        checked[indexes.get(i).r][indexes.get(i).c - 1] = 1;
                    }
                }
            } while (indexes.size() > prevSize);
            return indexes.size();
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