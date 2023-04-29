package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

public class TuiPrinter {
    private static final @Unmodifiable String pxl = "  ";
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

    public static void tuiPrintBoard(Board board) {
        for (int row = 0; row < BoardView.BOARD_ROWS; row++) {
            for (int i = 0; i < 24; i++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < BoardView.BOARD_COLUMNS; col++) {
                    if (board.isValidTile(row, col)) {
                        Property<@Nullable Tile> tileProp = board.tile(row, col);
                        if (tileProp.get() != null)
                            sb.append(SpriteLine(i, Objects.requireNonNull(tileProp.get()).getColor()));
                        else
                            sb.append(EmptyLine());
                    } else {
                        sb.append(InvalidTileLine());
                    }
                }
                System.out.println(sb);
            }
        }
    }

    private static void printShelfieHeader() {
        StringBuilder sb = new StringBuilder();
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

    private static void printMidShelf3() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(131));
        sb.append(ConsoleColors.RESET);
        System.out.println(sb);
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
        for (int i = 0; i < 5; i++) {
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

    private static StringBuilder EmptyLine() {
        return new StringBuilder(pxl.repeat(24));
    }

    private static StringBuilder InvalidTileLine() {
        return new StringBuilder().append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(24)).append(ConsoleColors.RESET);
    }

    private static StringBuilder NumberLine(int number, int index) {
        String numberLine = switch (number) {
            case 1 -> ONE.get(index);
            case 2 -> TWO.get(index);
            case 3 -> THREE.get(index);
            case 4 -> FOUR.get(index);
            case 5 -> FIVE.get(index);
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
