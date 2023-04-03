package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.model.ShelfieView.*;

public enum Type implements Serializable {
    SIX_COUPLES {
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int[][] checked = new int[ROWS][COLUMNS];
            int count = numCouples(shelfie, checked);
            if (count >= 6)
                printCommonGoal(shelfie, checked, "SIX COUPLES");
            return count >= 6;
        }

        /**
         * Returns number of couples in given shelfie and marks them with progressive number representing the
         * order in which they have been identified by the program
         **/
        public int numCouples(Shelfie shelfie, int[][] checked) {
            int count = 0;
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    if (c < COLUMNS - 1 && shelfie.tile(r, c).get() != null
                            && Objects.equals(shelfie.tile(r, c + 1).get(), shelfie.tile(r, c).get()) &&
                            checked[r][c] == 0 && checked[r][c + 1] == 0) {
                        count++;
                        checked[r][c] = count;
                        checked[r][c + 1] = count;
                    }
                    if (r < ROWS - 1 && shelfie.tile(r, c).get() != null
                            && Objects.equals(shelfie.tile(r + 1, c).get(), shelfie.tile(r, c).get()) &&
                            checked[r][c] == 0 && checked[r + 1][c] == 0) {
                        count++;
                        checked[r][c] = count;
                        checked[r + 1][c] = count;
                    }
                }
            }
            return count;
        }
    },
    ALL_CORNERS {
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int[][] checked = new int[ROWS][COLUMNS];
            boolean achieved = shelfie.tile(0, 0).get() != null &&
                    Objects.equals(shelfie.tile(0, 0).get(), shelfie.tile(0, COLUMNS - 1).get()) &&
                    Objects.requireNonNull(shelfie.tile(0, COLUMNS - 1).get()).equals(shelfie.tile(ROWS - 1, 0).get()) &&
                    Objects.requireNonNull(shelfie.tile(ROWS - 1, 0).get()).equals(shelfie.tile(ROWS - 1, COLUMNS - 1).get());
            if (achieved) {
                checked[0][0] = checked[ROWS - 1][0] = checked[0][COLUMNS - 1] = checked[ROWS - 1][COLUMNS - 1] = 1;
                printCommonGoal(shelfie, checked, "ALL CORNERS");
            }
            return achieved;
        }
    },
    FOUR_QUADRIPLETS {
        record Index(int r, int c) {
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            boolean achieved = false;
            int[][] checked = new int[ROWS][COLUMNS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    if (checked[r][c] == 0 && shelfie.tile(r, c).get() != null) {
                        if (getQuadrupletCheck(shelfie, r, c, checked, count + 1) >= 4)
                            count++;
                    }
                }
            }
            achieved = count >= 4;
            if (achieved)
                printCommonGoal(shelfie, checked, "FOUR QUADRIPLETS");
            return achieved;
        }

        /**
         * Returns the number of adjacent tiles to the one specified by given row and col, marking them if more than 4
         * with the number of quadriplet they belong to (specified by parameter marker), if not, marks them with -1
         * to ensure they don't get inspected further
         **/
        public int getQuadrupletCheck(Shelfie shelfie, int row, int col, int[][] checked, int marker) {
            List<Index> indexes = new ArrayList<>();
            indexes.add(new Index(row, col));
            checked[row][col] = marker;
            int prevSize = 0;
            do {
                prevSize = indexes.size();
                for (int i = 0; i < indexes.size(); i++) {
                    if (indexes.get(i).r < ROWS - 1
                            && Objects.equals(shelfie.tile(indexes.get(i).r + 1, indexes.get(i).c),
                                    shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r + 1, indexes.get(i).c))) {
                        indexes.add(new Index(indexes.get(i).r + 1, indexes.get(i).c));
                        checked[indexes.get(i).r + 1][indexes.get(i).c] = marker;
                    }
                    if (indexes.get(i).c < COLUMNS - 1
                            && Objects.equals(shelfie.tile(indexes.get(i).r, indexes.get(i).c + 1),
                                    shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r, indexes.get(i).c + 1))) {
                        indexes.add(new Index(indexes.get(i).r, indexes.get(i).c + 1));
                        checked[indexes.get(i).r][indexes.get(i).c + 1] = marker;
                    }
                    if (indexes.get(i).r > 0
                            && shelfie.tile(indexes.get(i).r - 1, indexes.get(i).c) == shelfie.tile(indexes.get(i).r,
                                    indexes.get(i).c)
                            && !indexes.contains(new Index(indexes.get(i).r - 1, indexes.get(i).c))) {
                        indexes.add(new Index(indexes.get(i).r - 1, indexes.get(i).c));
                        checked[indexes.get(i).r - 1][indexes.get(i).c] = marker;
                    }
                    if (indexes.get(i).c > 0
                            && Objects.equals(shelfie.tile(indexes.get(i).r, indexes.get(i).c - 1),
                                    shelfie.tile(indexes.get(i).r, indexes.get(i).c))
                            && !indexes.contains(new Index(indexes.get(i).r, indexes.get(i).c - 1))) {
                        indexes.add(new Index(indexes.get(i).r, indexes.get(i).c - 1));
                        checked[indexes.get(i).r][indexes.get(i).c - 1] = marker;
                    }
                }
            } while (indexes.size() > prevSize);
            if (indexes.size() < 4)
                for (Index i : indexes)
                    checked[i.r][i.c] = -1;
            return indexes.size();
        }
    },
    TWO_SQUARES {
        /**
         * Returns the number of existing squares in a given shelfie, and marks the existing ones with
         * progressive numbers according to the order they have been found
         **/
        public int numSquares(Shelfie shelfie, int[][] checked) {
            int count = 0;
            for (int r = 0; r < ROWS - 1; r++) {
                for (int c = 0; c < COLUMNS - 1; c++) {
                    if (checked[r][c] == 0 && checked[r + 1][c] == 0 && checked[r][c + 1] == 0 && checked[r + 1][c + 1] == 0) {
                        if (shelfie.tile(r, c).get() != null &&
                                Objects.equals(shelfie.tile(r + 1, c).get(), shelfie.tile(r, c).get()) &&
                                Objects.equals(shelfie.tile(r, c + 1).get(), shelfie.tile(r, c).get()) &&
                                Objects.equals(shelfie.tile(r + 1, c + 1).get(), shelfie.tile(r, c).get())) {
                            count++;
                            checked[r][c] = count;
                            checked[r + 1][c] = count;
                            checked[r][c + 1] = count;
                            checked[r + 1][c + 1] = count;
                        }
                    }
                }
            }
            return count;
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            boolean achieved = false;
            int[][] checked = new int[ROWS][COLUMNS];
            achieved = numSquares(shelfie, checked) >= 2;
            if (achieved)
                printCommonGoal(shelfie, checked, "TWO SQUARES");
            return achieved;
        }
    },
    THREE_COLUMNS {
        /**
         * Returns the number of different colors present in a given column c of a shelfie, and if the number of
         * colors is less than 4, it marks the columns with a progressive number according to the order they have
         * been found
         */
        public int numColorsForColumn(Shelfie shelfie, int c, int[][] checked, int marker) {
            List<Color> colors = new ArrayList<>();
            boolean fullColumn = true;

            for (int r = 0; r < ROWS && fullColumn; r++) {
                if (shelfie.tile(r, c).get() != null
                        && !colors.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                    colors.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
                if (shelfie.tile(r, c).get() == null)
                    fullColumn = false;
            }
            if (colors.size() <= 3 && fullColumn)
                for (int r = 0; r < ROWS; r++) {
                    checked[r][c] = marker;
                }
            return fullColumn ? colors.size() : COLUMNS;
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            boolean achieved = false;
            int[][] checked = new int[ROWS][COLUMNS];
            for (int c = 0; c < COLUMNS; c++) {
                if (numColorsForColumn(shelfie, c, checked, count + 1) <= 3)
                    count++;
            }
            achieved = (count >= 3);
            if (achieved)
                printCommonGoal(shelfie, checked, "THREE COLUMNS");
            return achieved;
        }
    },
    EIGHT_EQUAL_TILES {
        /** Returns the amount of tiles of a given color present in the shelfie */
        public int equalColoredTiles(Shelfie shelfie, int[][] checked, Color color) {
            int count = 0;
            int[][] oldChecked = Arrays.stream(checked).map(int[]::clone).toArray(int[][]::new);
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    if (shelfie.tile(r, c).get() != null
                            && Objects.requireNonNull(shelfie.tile(r, c).get()).getColor().equals(color)) {
                        count++;
                        checked[r][c] = count;
                    }
                }
            }
            if (count < 8) {
                for (int r = 0; r < ROWS; r++)
                    System.arraycopy(oldChecked[r], 0, checked[r], 0, COLUMNS);
            }
            return count;
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            boolean achieved = false;
            int[][] checked = new int[ROWS][COLUMNS];
            for (Color c : Color.values()) {
                if (equalColoredTiles(shelfie, checked, c) >= 8)
                    achieved = true;
            }
            if (achieved)
                printCommonGoal(shelfie, checked, "EIGHT EQUAL TILES");
            return achieved;
        }
    },
    DIAGONAL {
        /**
         * Returns true if the diagonal in the shelfie built from tile in position r,c is made by the same colored
         * tiles, and it's made by exactly 5 tiles, otherwise returns false
         */
        public boolean checkDiagonal(Shelfie shelfie, int[][] checked, int r, int c) {
            if (r >= ROWS - 4 || !(c == 0 || c == COLUMNS - 1) || shelfie.tile(r, c).get() == null)
                return false;
            if (c == 0) {
                for (int i = 0; i < 4; i++) {
                    if (!Objects.equals(shelfie.tile(r, c).get(), shelfie.tile(r + i, c + i).get()))
                        return false;
                }
                for (int i = 0; i <= 4; i++)
                    checked[r + i][c + i] = 1;
            } else {
                for (int i = 0; i > -4; i--) {
                    if (!Objects.equals(shelfie.tile(r, c).get(), shelfie.tile(r + i, c + i).get()))
                        return false;
                }
                for (int i = 0; i >= -4; i--)
                    checked[r + i][c + i] = 1;
            }
            return true;
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            boolean achieved = false;
            int[][] checked = new int[ROWS][COLUMNS];
            for (int r = 0; r < ROWS && !achieved; r++) {
                for (int c = 0; c < COLUMNS && !achieved; c++) {
                    achieved = checkDiagonal(shelfie, checked, r, c);
                }
            }
            if (achieved)
                printCommonGoal(shelfie, checked, "DIAGONAL");
            return achieved;
        }
    },
    FOUR_ROWS {
        /**
         * Returns the number of different colors present in a given row r of a shelfie, excluding null tiles
         */
        public int numColorsForRow(Shelfie shelfie, int r) {
            List<Color> count = new ArrayList<>();
            for (int c = 0; c < COLUMNS; c++) {
                //if there is at least a null tile the row doesn't count because it must be full
                if (shelfie.tile(r, c).get() == null)
                    return ROWS;
                if (!count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                    count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
            }
            return count.size();
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            for (int r = 0; r < ROWS; r++) {
                if (numColorsForRow(shelfie, r) <= 3)
                    count++;
            }
            return count >= 4;
        }
    },

    TWO_ALL_DIFF_COLUMNS {
        /**
         * Returns the number of different colors present in a given color c of a shelfie, excluding null tiles
         */
        public int numColorsForColumn(Shelfie shelfie, int c) {
            List<Color> count = new ArrayList<>();
            for (int r = 0; r < ROWS; r++) {
                if (shelfie.tile(r, c).get() != null
                        && !count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                    count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
            }
            return count.size();
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            for (int c = 0; c < COLUMNS; c++) {
                if (numColorsForColumn(shelfie, c) == ROWS)
                    count++;
            }
            return count >= 2;
        }
    },
    TWO_ALL_DIFF_ROWS {
        /**
         * Returns the number of different colors present in a given row r of a shelfie, excluding null tiles
         */
        public int numColorsForRow(Shelfie shelfie, int r) {
            List<Color> count = new ArrayList<>();
            for (int c = 0; c < COLUMNS; c++) {
                if (shelfie.tile(r, c).get() != null
                        && !count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                    count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
            }
            return count.size();
        }

        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            int count = 0;
            for (int r = 0; r < ROWS; r++) {
                if (numColorsForRow(shelfie, r) == COLUMNS)
                    count++;
            }
            return count >= 2;
        }
    },
    CROSS {
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {
            for (int r = 0; r < ROWS - 2; r++) {
                for (int c = 0; c < COLUMNS - 2; c++) {
                    if (shelfie.tile(r, c).get() != null &&
                            shelfie.tile(r, c).equals(shelfie.tile(r + 2, c)) &&
                            shelfie.tile(r, c).equals(shelfie.tile(r, c + 2)) &&
                            shelfie.tile(r, c).equals(shelfie.tile(r + 1, c + 1)) &&
                            shelfie.tile(r, c).equals(shelfie.tile(r + 2, c + 2)))
                        return true;
                }
            }
            return false;
        }
    },
    TRIANGLE {
        @Override
        public boolean checkCommonGoal(Shelfie shelfie) {

            for (int r = 0; r < 2; r++) {
                if (shelfie.tile(r, 0).get() != null && shelfie.tile(r + 1, 0).get() == null &&
                        shelfie.tile(r + 1, 1).get() != null && shelfie.tile(r + 2, 1).get() == null &&
                        shelfie.tile(r + 2, 2).get() != null && shelfie.tile(r + 3, 2).get() == null &&
                        shelfie.tile(r + 3, 3).get() != null && shelfie.tile(r + 4, 3).get() == null &&
                        shelfie.tile(r + 4, 4).get() != null)
                    return true;
            }
            for (int r = 0; r < 2; r++) {
                if (shelfie.tile(r, 4).get() != null && shelfie.tile(r + 1, 4).get() == null &&
                        shelfie.tile(r + 1, 3).get() != null && shelfie.tile(r + 2, 3).get() == null &&
                        shelfie.tile(r + 2, 2).get() != null && shelfie.tile(r + 3, 2).get() == null &&
                        shelfie.tile(r + 3, 1).get() != null && shelfie.tile(r + 4, 1).get() == null &&
                        shelfie.tile(r + 4, 0).get() != null)
                    return true;
            }

            return false;

        }
    };

    /**
     * @return true if the common goal of given type is achieved
     **/
    public abstract boolean checkCommonGoal(Shelfie shelfie);

    public void printCommonGoal(Shelfie shelfie, int[][] checked, String s) {
        System.out.println("Congratulation you achieved : " + s + " common goal");
        for (int row = 0; row < ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                msg.append("   1  2  3  4  5 \n");
            }
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(" ");
                if (shelfie.tile(row, col).get() == null) {
                    msg.append("| |");
                } else {
                    Color color = Objects.requireNonNull(shelfie.tile(row, col).get()).getColor();
                    if (checked[row][col] < 1) {
                        if (color.equals(Color.BLUE))
                            msg.append(ConsoleColors.BLUE_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                        if (color.equals(Color.GREEN))
                            msg.append(ConsoleColors.GREEN_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                        if (color.equals(Color.ORANGE))
                            msg.append(ConsoleColors.ORANGE_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                        if (color.equals(Color.PINK))
                            msg.append(ConsoleColors.PURPLE_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                        if (color.equals(Color.YELLOW))
                            msg.append(ConsoleColors.YELLOW_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                        if (color.equals(Color.LIGHTBLUE))
                            msg.append(ConsoleColors.CYAN_BACKGROUND).append("   ").append(ConsoleColors.RESET);
                    } else {
                        if (color.equals(Color.BLUE))
                            msg.append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                        if (color.equals(Color.GREEN))
                            msg.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                        if (color.equals(Color.ORANGE))
                            msg.append(ConsoleColors.ORANGE_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                        if (color.equals(Color.PINK))
                            msg.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                        if (color.equals(Color.YELLOW))
                            msg.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                        if (color.equals(Color.LIGHTBLUE))
                            msg.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append(ConsoleColors.BLACK_BOLD).append(" ")
                                    .append(checked[row][col]).append(" ")
                                    .append(ConsoleColors.RESET);
                    }
                }
            }
            System.out.println(msg);
        }
    }
}