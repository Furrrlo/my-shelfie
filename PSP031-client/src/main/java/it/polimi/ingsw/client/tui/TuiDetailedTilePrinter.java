package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

/**
 * Printer for {@link Tile}.
 * <p>
 * Prints a detailed drawing of the given tile in {@value #PXL_FOR_SPRITE} x {@value #PXL_FOR_SPRITE} pxl
 */
class TuiDetailedTilePrinter implements TuiPrinter {

    /**
     * pxl sets the basic unit for printing complex design, while PXL_FOR_SPRITE, PXL_FOR_NUMBERS and
     * PXL_FOR_PERSONAL_GOAL sets the number of pxl respectively for printing tiles' sprites, number of shelfie and board,
     * and personal goals' tile;
     */
    public static final int PXL_FOR_SPRITE = 24;

    /*
     * List<String> of CAT, TREE, BOOK, TROPHY, GAME and Frame ( and all the other numbers ) represents the
     * lines of each design composed by letters that represent colors that methods in TuiPrinter class handles in order
     * to print different color of pxl
     */
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

    private static final Map<Color, TuiDetailedTilePrinter> TILE_PRINTERS = Arrays.stream(Color.values())
            .collect(Collectors.toUnmodifiableMap(Function.identity(), TuiDetailedTilePrinter::new));
    private static final TuiDetailedTilePrinter NULL_TILE_PRINTERS = new TuiDetailedTilePrinter(null);

    public static TuiDetailedTilePrinter of(@Nullable Tile tile) {
        return tile == null
                ? NULL_TILE_PRINTERS
                : Objects.requireNonNull(TILE_PRINTERS.get(tile.getColor()), "Unrecognized color for tile " + tile);
    }

    public static TuiDetailedTilePrinter of(@Nullable Color color) {
        return color == null
                ? NULL_TILE_PRINTERS
                : Objects.requireNonNull(TILE_PRINTERS.get(color), "Unrecognized color " + color);
    }

    private final @Nullable Color tileColor;

    public TuiDetailedTilePrinter(@Nullable Color tileColor) {
        this.tileColor = tileColor;
    }

    @Override
    public void print(TuiPrintStream out) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PXL_FOR_SPRITE; i++)
            sb.append(tileColor != null ? spriteLine(i, tileColor) : emptyLine()).append('\n');
        out.print(sb);
    }

    /** returns the colored String, at given index, of a specified coloredTile as StringBuilder */
    private static StringBuilder spriteLine(int lineIndex, Color color) {
        String spriteLine = switch (color) {
            case GREEN -> CAT.get(lineIndex);
            case PINK -> TREE.get(lineIndex);
            case WHITE -> BOOK.get(lineIndex);
            case LIGHTBLUE -> TROPHY.get(lineIndex);
            case YELLOW -> GAME.get(lineIndex);
            case BLUE -> FRAME.get(lineIndex);
        };

        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET);
        for (int i = 0; i < PXL_FOR_SPRITE; i++) {
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
            sb.append(consoleColor).append(pxl);
        }
        sb.append(ConsoleColors.RESET);
        return sb;
    }

    private static String emptyLine() {
        return pxl.repeat(PXL_FOR_SPRITE);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(PXL_FOR_SPRITE, PXL_FOR_SPRITE * pxl.length());
    }
}
