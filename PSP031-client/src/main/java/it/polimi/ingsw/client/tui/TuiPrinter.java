package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.BoardView.BOARD_COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

public class TuiPrinter {
    /**
     * pxl sets the basic unit for printing complex design, while PXL_FOR_SPRITE, PXL_FOR_NUMBERS and
     * PXL_FOR_PERSONAL_GOAL sets the number of pxl respectively for printing tiles' sprites, number of shelfie and board,
     * and personal goals' tile;
     * List<String> of CAT, TREE, BOOK, TROPHY, GAME and Frame ( and all the other numbers ) represents the
     * lines of each design composed by letters that represent colors that methods in TuiPrinter class handles in order
     * to print different color of pxl
     */
    private static final @Unmodifiable String pxl = "  ";
    private static final @Unmodifiable int PXL_FOR_SPRITE = 24;
    private static final @Unmodifiable int PXL_FOR_NUMBERS = 5;
    private static final @Unmodifiable int PXL_FOR_PERSONAL_GOAL = 12;
    private static final @Unmodifiable List<String> CAT = List.of("GGGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGGGBBGGGBBGGGG",
            "GGGGGBBGGGGGBBPBGBBPBGGG", "GGGGGBBBGGGBAACAAAACAGGG", "GGGGGGBBGGBAAAAAAAAAAAGG", "GGGGGGBBGGBAAAABBBBAAAGG",
            "GGGGBBBBGGBAABDBBBBDBAGG", "GGGBBBBGGGWWABDBBBBDBAWG", "GGGBBGGGGGBAAAAAACAAAAGG", "GGGBBGGGGGWWAAAAWAWAAAWG",
            "GGGAAAAAAAARRAAAAAAAAGGG", "GGGAAAAAAAAAARRRRRRRGGGG", "GGGAAAAAAAAAAAAAAOAAGGGG", "GGGAAAAAAAAAAAAAAAAAGGGG",
            "GGGAAAAAAAAAAAAAAAAAGGGG", "GGBBAAAAAAAAAAAAAAAAGGGG", "GBBBAAAAAAAAAAAAAAAAAGGG", "GBBBBAAAAAAAAAAAAAAAAGGG",
            "GBBBBBAAAAAAAAAAAAAAAGGG", "GBBBGGGAAAAGAAAGGGAAAAGG", "GBBBGGGGAAAGAAAGGGGAAAGG", "GGWWGGGGGWWGGWWGGGGGWWGG",
            "GGGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGG");

    private static final @Unmodifiable List<String> TREE = List.of("CCCCCCCCCCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCGGGGGGGCCCC",
            "CCCCCCCCCCCGGGGGEEEGGCCC", "CCCCCCCCCCEFEFEEEEEGGCCC", "CCCCCCCCEEEEFEEEEFEGGGCC", "CCCCCCCEEFEEFEFEEEEEEGCC",
            "CCCCEEEEFEEEFEFHEEEEEGCC", "CCCFEEEGEFEIEEFEHEEEEGCC", "CCFFFFFEEEEEFHEEHEEEECCC", "CCCFFFEEFEFFFHHHEEICCCCC",
            "CCCCCFFFGCCCCHHHHCCCCCCC", "CCCCCCCCCCCCCIHCCCCCCCCC", "CCCCCCCCCCCCIIHCCCCCCCCC", "CCCCCCCCCCCCCIHCCCCCCCCC",
            "CCCCCCCCCCCCIIHCCCCCCCCC", "CCCCCCIHCCCCIHIHHICCCCCC", "CCCCCHHHIIHIHHHIHHICCCCC", "CBBBBBBBBBBBBBBBBBBBBBCC",
            "CCBAAAAWWWWWWWWWWWWWBCCC", "CCBBAAAAAAAAAAAAAAABBCCC", "CCCBAAAAAAAWWWWWWWWBCCCC", "CCCBABBBBBBBBBBBBBWBCCCC",
            "CCCBBBCCCCCCCCCCCBBBCCCC", "CCCCCCCCCCCCCCCCCCCCCCCC");

    private static final @Unmodifiable List<String> BOOK = List.of("MMMMMMMMMMMMMMMMMMMMMMMM", "MMMMMMMMMMMMMMMMMMMMMMMM",
            "MMMKLKKKKKKKKKKKKKKKKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM",
            "MMLKLKMMKMKMKMMKMKKMMRMM", "MMLKLKMKKMKMKMKKMKKMKRMM", "MMLKLKMMKMMMKMMKMKKMMRMM", "MMLKLKKMKMKMKMKKMKKMKRMM",
            "MMLKLKMMKMKMKMMKMMKMKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM", "MMLKLKKKKKKKKKKKKKKKKRMM",
            "MMLRLRRRRRRRRRRRRRRRRRMM", "MMLLLLLLLLLLLLLLLLLLLLMM", "MMLWWWWWWWWWWWWWWWWWWLMM", "MMLWMMMMRRRRRMMMMMMMMLMM",
            "MMLWMMMMRRRRRMMMMMMMMLMM", "MMLWMMMMMMMMMMMMMMMMMLMM", "MMLKKKKKKKKKKKKKKKKKKKMM", "MMMLLLLLLLLLLLLLLLLLLLMM",
            "MMMMMMMMMMMMMMMMMMMMMMMM", "MMMMMMMMMMMMMMMMMMMMMMMM");

    private static final @Unmodifiable List<String> TROPHY = List.of("NNNNNNNNNNNNNNNNNNNNNNNN", "NNNNNNNNNNNNNNNNNNNNNNNN",
            "NNNNNNNNNNNNNNNNNNNNNNNN", "NNNNNSSSSSOOOOQQOONNNNNN", "NNNNNNSSSSSSSSSSSNNNNNNN", "NNSQQOOSSSOOOOQOOOOQQNNN",
            "NNSQNNSSSSOOOOQOONNSQNNN", "NNSQNNSSSSOOOOQOONNSONNN", "NNSONNSSSSOOOOQOONNSQNNN", "NNSONNSSSSOOOOQOONNSQNNN",
            "NNSONNSSSSOOOOQOONNSONNN", "NNSONNSSSSOOOOQOONNSONNN", "NNSOOOOSSSOOOOQOOOOOONNN", "NNNNNNSSSSOOOOQOONNNNNNN",
            "NNNNNNSSSSOOOOQOONNNNNNN", "NNNNNNSSSSOOOOOOONNNNNNN", "NNNNNNNSSSSSSSSSNNNNNNNN", "NNNNNNNNNNSSSNNNNNNNNNNN",
            "NNNNNNNNNNSOQNNNNNNNNNNN", "NNNNNNNNNNSOQNNNNNNNNNNN", "NNNNNNNNNSSOOQNNNNNNNNNN", "NNNNNNNSSSOOOQOONNNNNNNN",
            "NNNNNSSSSOOOOOQOOONNNNNN", "NNNNNNNNNNNNNNNNNNNNNNNN");

    private static final @Unmodifiable List<String> GAME = List.of("OOOOOOOOOOOOOOOOOOOOOOOO", "OOOOOOOOOOOOOOJJJJJJJJOO",
            "OOOOOOOOOOOOOJBSSSSSSBHO", "OOOOOOOOOOOOOJSSQQQQSSHO", "OOOOOOOOOOOOOJSSQSSQSSHO", "OOOOOOOOOOOOOJSSSSSQSSHO",
            "OOOOOOOOOOOOOJSSSSQSSSHO", "OOOOOOOOOOOOOJUUUSQSSSHO", "OOOOOOORRRRROJUUUSSSSSHO", "OOOOOORRRRRRRRRUUSQSSBHO",
            "OOOOOOHHHUUBUOTTTHHHHHOO", "OOOOOHUHUUUBUUTTTOOOOOOO", "OOOOOHUHHUUUHUUTTOOOOOOO", "OOOOOHHUUUUHHHHTOOOOOOOO",
            "OOOOOOOUUUUUUUTOOOOOOOOO", "OOOTTTTTRTTTRTOOOOOOOOOO", "OOTTTTTTTRTTTROOBOOOOOOO", "OUUTTTTTTRRRRROOBOOOOOOO",
            "OUUUORRTRRORRORBBOOOOOOO", "OOUOBRRRRRRRRRRBBOOOOOOO", "OOOBBBRRRRRRRRRBBOOOOOOO", "OOBBBRRRRRRROOOOOOOOOOOO",
            "OOBOORRRROOOOOOOOOOOOOOO", "OOOOOOOOOOOOOOOOOOOOOOOO");

    private static final @Unmodifiable List<String> FRAME = List.of("TTTTTTTTTTTTTTTTTTTTTTTT", "TTTTTTTTTTTTTTTTTTTTTTTT",
            "TTTTTTTTTTTTTTTTTTTTTTTT", "TTIJJJJJJJJJJJJJJJJJJTTT", "TTIJJJJJJJJJJJJJJJJJJTTT", "TTIJJIIIIIIIIIIIIIIJJTTT",
            "TTIJJHIJJJJJJJJJJIIJJTTT", "TTIJJHJIIIIIIIIIIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT",
            "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT",
            "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHXXXXXXXXIJIJJTTT", "TTIJJHJHHHHHHHHHIJIJJTTT", "TTIJJHJJJJJJJJJJJIIJJTTT",
            "TTIJJHHHHHHHHHHHHHIJJTTT", "TTIJJJJJJJJJJJJJJJJJJTTT", "TTIJJJJJJJJJJJJJJJJJJTTT", "TTTTTTTTTTTTTTTTTTTTTTTT",
            "TTTTTTTTTTTTTTTTTTTTTTTT", "TTTTTTTTTTTTTTTTTTTTTTTT");

    private static final @Unmodifiable List<String> ONE = List.of("WWWWWWWWWWBBWWWWWWWWWWWW", "WWWWWWWWWWWBWWWWWWWWWWWW",
            "WWWWWWWWWWWBWWWWWWWWWWWW", "WWWWWWWWWWWBWWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");

    private static final @Unmodifiable List<String> TWO = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWWWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> THREE = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> FOUR = List.of("WWWWWWWWWWBWWWWWWWWWWWWW", "WWWWWWWWWWBWWWWWWWWWWWWW",
            "WWWWWWWWWWBWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> FIVE = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWWWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> SIX = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWWWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> SEVEN = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW",
            "WWWWWWWWWWWBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> EIGHT = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWBWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");
    private static final @Unmodifiable List<String> NINE = List.of("WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWBWBWWWWWWWWWWW",
            "WWWWWWWWWWBBBWWWWWWWWWWW", "WWWWWWWWWWWWBWWWWWWWWWWW", "WWWWWWWWWWBBBWWWWWWWWWWW");

    //TODO : define design of COMMON GOAL, implement them as @Unmodifiable List<String> and add them as the first two
    //  printed tiles in board, so that can be always seen during the game

    /** prints shelfie with complex design */
    public static void tuiPrintShelfie(Shelfie shelfie) {
        printShelfieHeader();
        for (int row = 0; row < ROWS; row++) {
            printMidShelf1();
            printMidShelf2();
            for (int i = 0; i < 24; i++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < COLUMNS; col++) {
                    if (col == 0)
                        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                                .append(ConsoleColors.RESET);

                    var tile = shelfie.tile(row, col).get();
                    sb.append(tile != null ? SpriteLine(i, tile.getColor()) : EmptyLine());
                    sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl)
                            .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                            .append(ConsoleColors.RESET);
                }
                System.out.println(sb);
            }
        }
        printShelfieBottom();
    }

    /** prints board with complex design */
    public static void tuiPrintBoard(Board board) {
        printBoardSeparatingLine();
        printBoardNumber();
        for (int row = 0; row < BoardView.BOARD_ROWS; row++) {
            for (int i = 0; i < 24; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(VerticalNumberLine(row + 1, i));
                for (int col = 0; col < BoardView.BOARD_COLUMNS; col++) {
                    if (col == 0)
                        sb.append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append(pxl).append(ConsoleColors.RESET);
                    if (board.isValidTile(row, col)) {
                        Property<@Nullable Tile> tileProp = board.tile(row, col);
                        if (tileProp.get() != null)
                            sb.append(SpriteLine(i, Objects.requireNonNull(tileProp.get()).getColor()));
                        else
                            sb.append(EmptyLine());
                    } else {
                        sb.append(InvalidTileLine());
                    }
                    sb.append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append(pxl).append(ConsoleColors.RESET);
                }
                System.out.println(sb);
            }
            printBoardSeparatingLine();
        }
    }

    /** prints personalGoal with complex design **/
    public static void tuiPrintPersonalGoal(PersonalGoal p) {
        for (int row = 0; row < ROWS; row++) {
            System.out.println(PersonalGoalMidShelf1());
            System.out.println(PersonalGoalMidShelf2());
            for (int i = 0; i < PXL_FOR_PERSONAL_GOAL; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(ConsoleColors.RESET);
                for (int col = 0; col < COLUMNS; col++) {
                    if (col == 0)
                        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
                    if (p.get(row, col) == null)
                        sb.append(ConsoleColors.RESET).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL));
                    else
                        sb.append(PersonalGoalLine(Objects.requireNonNull(p.get(row, col)).getColor()));
                    sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl).append(ConsoleColors.ORANGE_BACKGROUND)
                            .append(pxl);
                    sb.append(ConsoleColors.RESET);
                }
                System.out.println(sb);
            }
        }
        System.out.println(PersonalGoalMidShelf1());
    }

    /** prints personalGoal next to shelfie both with complex desing */
    public static void tuiPrintShelfieAndPersonalGoal(Shelfie shelfie, PersonalGoal pg) {
        printShelfieHeader();
        System.out.println(MidShelf1());
        System.out.println(MidShelf2());
        int pg_i = -2;
        int pg_row = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int i = 0; i < PXL_FOR_SPRITE + 2; i++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < COLUMNS; col++) {
                    if (col == 0 && i < PXL_FOR_SPRITE)
                        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                                .append(ConsoleColors.RESET);
                    if (i < PXL_FOR_SPRITE) {
                        var tile = shelfie.tile(row, col).get();
                        sb.append(tile != null ? SpriteLine(i, tile.getColor()) : EmptyLine());
                        sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl)
                                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                                .append(ConsoleColors.RESET);
                    }
                }
                if (i == PXL_FOR_SPRITE && row < ROWS - 1) //midShelf
                    sb.append(MidShelf1());
                if (i == PXL_FOR_SPRITE + 1 && row < ROWS - 1)//midShelf
                    sb.append(MidShelf2());
                if (i == PXL_FOR_SPRITE && row == ROWS - 1)//bottomShelf
                    sb.append(MidShelf1());
                if (i == PXL_FOR_SPRITE + 1 && row == ROWS - 1)//bottomShelf
                    sb.append(MidShelf3());

                sb.append(pxl.repeat(10));//added space

                if (pg_i == PXL_FOR_PERSONAL_GOAL + 2) {
                    pg_i = 0;
                    pg_row++;
                }
                if (pg_row < ROWS && pg_i < PXL_FOR_PERSONAL_GOAL + 2) {
                    if (pg_i == -2)
                        sb.append(PersonalGoalMidShelf1());
                    if (pg_i == -1)
                        sb.append(PersonalGoalMidShelf2());
                    for (int pg_col = 0; pg_col < COLUMNS && pg_i < PXL_FOR_PERSONAL_GOAL && pg_i >= 0; pg_col++) {
                        if (pg_col == 0)
                            sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                                    .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
                        if (pg.get(pg_row, pg_col) == null)
                            sb.append(ConsoleColors.RESET).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL));
                        else
                            sb.append(PersonalGoalLine(Objects.requireNonNull(pg.get(pg_row, pg_col)).getColor()));
                        sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl).append(ConsoleColors.ORANGE_BACKGROUND)
                                .append(pxl);
                        sb.append(ConsoleColors.RESET);
                    }
                    if (pg_i == PXL_FOR_PERSONAL_GOAL)
                        sb.append(PersonalGoalMidShelf1());
                    if (pg_i == PXL_FOR_PERSONAL_GOAL + 1 && pg_row < ROWS - 1) {
                        sb.append(PersonalGoalMidShelf2());
                    }
                    pg_i++;
                }
                System.out.println(sb);
            }
        }
        System.out.println(MidShelf1());
    }

    private static StringBuilder PersonalGoalMidShelf1() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl.repeat(COLUMNS * (PXL_FOR_PERSONAL_GOAL + 2)));
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        return sb;
    }

    private static StringBuilder PersonalGoalMidShelf2() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
        for (int i = 0; i < COLUMNS; i++)
            sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                    .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL + 1));
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        return sb;
    }

    private static void printBoardSeparatingLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(5));
        sb.append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append(pxl.repeat(226)).append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static void printShelfieHeader() {
        printMidShelf1();
        printMidShelf3();
        printMidShelf1();
        printNumberHeader();
        printNumber();
        printNumberHeader();
        printMidShelf1();
        printMidShelf3();

    }

    private static void printShelfieBottom() {
        printMidShelf1();
        printMidShelf3();
        printMidShelf1();
    }

    private static void printMidShelf1() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl.repeat(131))
                .append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static StringBuilder MidShelf1() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl.repeat(131))
                .append(ConsoleColors.RESET);
        return sb;
    }

    private static void printMidShelf2() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static StringBuilder MidShelf2() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(25))
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                .append(ConsoleColors.RESET);
        return sb;
    }

    private static void printMidShelf3() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(131));
        sb.append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static StringBuilder MidShelf3() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(131));
        sb.append(ConsoleColors.RESET);
        return sb;
    }

    private static void printNumberHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
        for (int i = 0; i < COLUMNS; i++)
            sb.append(ConsoleColors.ORANGE_BACKGROUND)
                    .append(pxl).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl.repeat(24))
                    .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl);
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    private static void printNumber() {
        for (int i = 0; i < PXL_FOR_NUMBERS; i++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 0)
                    sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                            .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                            .append(ConsoleColors.RESET);
                sb.append(NumberLine(col + 1, i));
                sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl)
                        .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                        .append(ConsoleColors.RESET);
            }
            System.out.println(sb);
        }
    }

    private static void printBoardNumber() {
        for (int i = 0; i < PXL_FOR_NUMBERS; i++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                if (col == 0)
                    sb.append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(6)).append(ConsoleColors.RESET);
                sb.append(NumberLine(col + 1, i));
                sb.append(ConsoleColors.BLUE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
            }
            System.out.println(sb);
        }
    }

    /**
     * takes a list of 5 elements of Strings of length == 24 of a NUMBER defined in TuiPrinter and converts it in to
     * a list of 24 elements of Strings of length == 5
     */
    private static List<String> convertToVerticalNumber(List<String> number) {
        List<String> ss = new ArrayList<>();
        for (int i = 0; i < PXL_FOR_SPRITE; i++) {
            String s = String.valueOf(number.get(4).charAt(i)) + String.valueOf(number.get(3).charAt(i)) +
                    String.valueOf(number.get(2).charAt(i)) + String.valueOf(number.get(1).charAt(i)) +
                    String.valueOf(number.get(0).charAt(i));
            ss.add(s);
        }
        return ss;
    }

    private static StringBuilder EmptyLine() {
        return new StringBuilder(pxl.repeat(24));
    }

    private static StringBuilder InvalidTileLine() {
        return new StringBuilder().append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(24)).append(ConsoleColors.RESET);
    }

    /** returns the colored String, at given index, of a specified VerticalNumber as StringBuilder */
    private static StringBuilder VerticalNumberLine(int number, int index) {
        String verticalNumberLine = switch (number) {
            case 1 -> convertToVerticalNumber(ONE).get(index);
            case 2 -> convertToVerticalNumber(TWO).get(index);
            case 3 -> convertToVerticalNumber(THREE).get(index);
            case 4 -> convertToVerticalNumber(FOUR).get(index);
            case 5 -> convertToVerticalNumber(FIVE).get(index);
            case 6 -> convertToVerticalNumber(SIX).get(index);
            case 7 -> convertToVerticalNumber(SEVEN).get(index);
            case 8 -> convertToVerticalNumber(EIGHT).get(index);
            case 9 -> convertToVerticalNumber(NINE).get(index);
            default -> throw new IllegalStateException("Unexpected value: " + number);
        };
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET);
        for (int i = 0; i < PXL_FOR_NUMBERS; i++) {
            String consoleColor = switch (verticalNumberLine.charAt(i)) {
                case 'W' -> ConsoleColors.WHITE_BACKGROUND_BRIGHT;
                case 'B' -> ConsoleColors.BLACK_BACKGROUND;
                default -> ConsoleColors.RESET;
            };
            sb.append(consoleColor).append(pxl);
        }
        sb.append(ConsoleColors.RESET);
        return sb;
    }

    /** returns the colored String, at given index, of a specified number as StringBuilder */
    private static StringBuilder NumberLine(int number, int index) {
        String numberLine = switch (number) {
            case 1 -> ONE.get(index);
            case 2 -> TWO.get(index);
            case 3 -> THREE.get(index);
            case 4 -> FOUR.get(index);
            case 5 -> FIVE.get(index);
            case 6 -> SIX.get(index);
            case 7 -> SEVEN.get(index);
            case 8 -> EIGHT.get(index);
            case 9 -> NINE.get(index);
            default -> throw new IllegalStateException("Unexpected value: " + number);
        };
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET);
        for (int i = 0; i < 24; i++) {
            String consoleColor = switch (numberLine.charAt(i)) {
                case 'W' -> ConsoleColors.WHITE_BACKGROUND_BRIGHT;
                case 'B' -> ConsoleColors.BLACK_BACKGROUND;
                default -> ConsoleColors.RESET;
            };
            sb.append(consoleColor).append(pxl);
        }
        sb.append(ConsoleColors.RESET);
        return sb;
    }

    /** returns the colored String, at given index, of a specified coloredTile as StringBuilder */
    private static StringBuilder SpriteLine(int index, Color color) {
        String spriteLine = switch (color) {
            case GREEN -> CAT.get(index);
            case PINK -> TREE.get(index);
            case WHITE -> BOOK.get(index);
            case LIGHTBLUE -> TROPHY.get(index);
            case YELLOW -> GAME.get(index);
            case BLUE -> FRAME.get(index);
        };

        StringBuilder ss = new StringBuilder();
        ss.append(ConsoleColors.RESET);
        for (int i = 0; i < 24; i++) {
            String consoleColor = switch (spriteLine.charAt(i)) {
                case 'B' -> ConsoleColors.BLACK_BACKGROUND;
                case 'G' -> ConsoleColors.GREEN_BACKGROUND_BRIGHT;
                case 'E' -> ConsoleColors.GREEN_BACKGROUND;
                case 'F' -> ConsoleColors.GREEN_DARK_BACKGROUND;
                case 'H' -> ConsoleColors.BROWN_BACKGROUND;
                case 'I' -> ConsoleColors.BROWN_DARK_BACKGROUND;
                case 'A' -> ConsoleColors.BLACK_BACKGROUND_BRIGHT;
                case 'P' -> ConsoleColors.PURPLE_BACKGROUND;
                case 'C' -> ConsoleColors.PURPLE_BACKGROUND_BRIGHT;
                case 'R' -> ConsoleColors.RED_BACKGROUND_BRIGHT;
                case 'W' -> ConsoleColors.WHITE_BACKGROUND;
                case 'D', 'Q', 'M' -> ConsoleColors.WHITE_BACKGROUND_BRIGHT;
                case 'K' -> ConsoleColors.RED_DARK_BACKGROUND;
                case 'L' -> ConsoleColors.RED_VERY_DARK_BACKGROUND;
                case 'O' -> ConsoleColors.YELLOW_BACKGROUND_BRIGHT;
                case 'N' -> ConsoleColors.CYAN_BACKGROUND_BRIGHT;
                case 'S', 'J' -> ConsoleColors.ORANGE_BACKGROUND;
                case 'T' -> ConsoleColors.BLUE_BACKGROUND;
                case 'U', 'X' -> ConsoleColors.PINK_BACKGROUND;
                default -> ConsoleColors.RESET;
            };
            ss.append(consoleColor).append(pxl);
        }
        ss.append(ConsoleColors.RESET);
        return ss;
    }

    /** returns the colored String, at given index, of a specified PersonalGoal as StringBuilder */
    private static StringBuilder PersonalGoalLine(Color color) {
        StringBuilder sb = new StringBuilder();
        return switch (color) {
            case GREEN -> sb.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case WHITE -> sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case BLUE -> sb.append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case LIGHTBLUE -> sb.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case YELLOW -> sb.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case PINK -> sb.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
        };
    }

    @VisibleForTesting
    public static void tuiPrintTile(Color color) {
        for (int i = 0; i < 24; i++)
            tuiPrintSpriteLine(i, color);
    }

    @VisibleForTesting
    public static void tuiPrintSpriteLine(int index, Color color) {
        System.out.println(SpriteLine(index, color));
    }
}
