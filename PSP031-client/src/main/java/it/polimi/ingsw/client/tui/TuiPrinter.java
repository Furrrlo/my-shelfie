package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
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

    public static void tuiPrintShelfie(Shelfie shelfie) {
        printShelfieHeader();
        for (int row = 0; row < ROWS; row++) {
            printMidShelf1();
            printMidShelf2();
            for (int i = 0; i < 24; i++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < COLUMNS; col++) {
                    if (col == 0)
                        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl).append(ConsoleColors.ORANGE_BACKGROUND)
                                .append(pxl).append(ConsoleColors.RESET);
                    if (shelfie.tile(row, col).get() != null)
                        sb.append(SpriteLine(i, Objects.requireNonNull(shelfie.tile(row, col).get()).getColor()));
                    else
                        sb.append(EmptyLine());
                    sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl).append(ConsoleColors.ORANGE_BACKGROUND)
                            .append(pxl).append(ConsoleColors.RESET);
                }
                System.out.println(sb);
            }
        }
        printShelfieBottom();
    }

    public static void printShelfieHeader() {
        //TODO : implement Shelfie header with selection number
    }

    public static void printShelfieBottom() {
        //TODO : implement Shelfie bottom with selection number
    }

    public static void printMidShelf1() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl.repeat(131)).append(ConsoleColors.RESET);
        System.out.println(sb);
    }

    public static void printMidShelf2() {
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

    public static StringBuilder EmptyLine() {
        return new StringBuilder().append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl)
                .append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl)
                .append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl).append(pxl)
                .append(pxl);
    }

    public static StringBuilder SpriteLine(int index, Color color) {
        String s = "";
        if (color.equals(Color.GREEN))
            s = CAT.get(index);
        if (color.equals(Color.PINK))
            s = TREE.get(index);
        if (color.equals(Color.WHITE))
            s = BOOK.get(index);
        if (color.equals(Color.LIGHTBLUE))
            s = TROPHY.get(index);
        if (color.equals(Color.YELLOW))
            s = GAME.get(index);
        if (color.equals(Color.BLUE))
            s = FRAME.get(index);

        StringBuilder ss = new StringBuilder();
        ss.append(ConsoleColors.RESET);
        for (int i = 0; i < 24; i++) {
            if (s.charAt(i) == 'B')
                ss.append(ConsoleColors.BLACK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'G')
                ss.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'E')
                ss.append(ConsoleColors.GREEN_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'F')
                ss.append(ConsoleColors.GREEN_DARK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'H')
                ss.append(ConsoleColors.BROWN_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'I')
                ss.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'A')
                ss.append(ConsoleColors.BLACK_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'P')
                ss.append(ConsoleColors.PURPLE_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'C')
                ss.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'R')
                ss.append(ConsoleColors.RED_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'W')
                ss.append(ConsoleColors.WHITE_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'D')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'K')
                ss.append(ConsoleColors.RED_DARK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'L')
                ss.append(ConsoleColors.RED_VERY_DARK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'M')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'O')
                ss.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'N')
                ss.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'S')
                ss.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'Q')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
            if (s.charAt(i) == 'T')
                ss.append(ConsoleColors.BLUE_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'U') // should be pink but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'J') // should be brown bright but not working
                ss.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
            if (s.charAt(i) == 'X') // should be brown bright but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append(pxl);
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
        String s = "";
        if (color.equals(Color.GREEN))
            s = CAT.get(index);
        if (color.equals(Color.PINK))
            s = TREE.get(index);
        if (color.equals(Color.WHITE))
            s = BOOK.get(index);
        if (color.equals(Color.LIGHTBLUE))
            s = TROPHY.get(index);
        if (color.equals(Color.YELLOW))
            s = GAME.get(index);
        if (color.equals(Color.BLUE))
            s = FRAME.get(index);

        StringBuilder ss = new StringBuilder();
        ss.append(ConsoleColors.RESET);
        for (int i = 0; i < 24; i++) {
            if (s.charAt(i) == 'B')
                ss.append(ConsoleColors.BLACK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'G')
                ss.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'E')
                ss.append(ConsoleColors.GREEN_BACKGROUND).append("  ");
            if (s.charAt(i) == 'F')
                ss.append(ConsoleColors.GREEN_DARK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'H')
                ss.append(ConsoleColors.BROWN_BACKGROUND).append("  ");
            if (s.charAt(i) == 'I')
                ss.append(ConsoleColors.BROWN_DARK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'A')
                ss.append(ConsoleColors.BLACK_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'P')
                ss.append(ConsoleColors.PURPLE_BACKGROUND).append("  ");
            if (s.charAt(i) == 'C')
                ss.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'R')
                ss.append(ConsoleColors.RED_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'W')
                ss.append(ConsoleColors.WHITE_BACKGROUND).append("  ");
            if (s.charAt(i) == 'D')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'K')
                ss.append(ConsoleColors.RED_DARK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'L')
                ss.append(ConsoleColors.RED_VERY_DARK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'M')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'O')
                ss.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'N')
                ss.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'S')
                ss.append(ConsoleColors.ORANGE_BACKGROUND).append("  ");
            if (s.charAt(i) == 'Q')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'T')
                ss.append(ConsoleColors.BLUE_BACKGROUND).append("  ");
            if (s.charAt(i) == 'U') // should be pink but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append("  ");
            if (s.charAt(i) == 'J') // should be brown bright but not working
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("  ");
            if (s.charAt(i) == 'X') // should be brown bright but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append("  ");
        }
        ss.append(ConsoleColors.RESET);
        System.out.println(ss);
    }
}