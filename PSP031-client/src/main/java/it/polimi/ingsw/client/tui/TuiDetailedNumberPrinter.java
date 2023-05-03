package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.tui.TuiDetailedTilePrinter.PXL_FOR_SPRITE;
import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

class TuiDetailedNumberPrinter implements TuiPrinter2 {

    public static final int PXL_ROWS_FOR_NUMBERS = 5;
    public static final int PXL_COLS_FOR_NUMBERS = PXL_FOR_SPRITE;

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

    private static final TuiDetailedNumberPrinter[] NUMBER_PRINTERS = new TuiDetailedNumberPrinter[] {
            new TuiDetailedNumberPrinter(1, false),
            new TuiDetailedNumberPrinter(2, false),
            new TuiDetailedNumberPrinter(3, false),
            new TuiDetailedNumberPrinter(4, false),
            new TuiDetailedNumberPrinter(5, false),
            new TuiDetailedNumberPrinter(6, false),
            new TuiDetailedNumberPrinter(7, false),
            new TuiDetailedNumberPrinter(8, false),
            new TuiDetailedNumberPrinter(9, false),
    };
    private static final TuiDetailedNumberPrinter[] VERTICAL_NUMBER_PRINTERS = new TuiDetailedNumberPrinter[] {
            new TuiDetailedNumberPrinter(1, true),
            new TuiDetailedNumberPrinter(2, true),
            new TuiDetailedNumberPrinter(3, true),
            new TuiDetailedNumberPrinter(4, true),
            new TuiDetailedNumberPrinter(5, true),
            new TuiDetailedNumberPrinter(6, true),
            new TuiDetailedNumberPrinter(7, true),
            new TuiDetailedNumberPrinter(8, true),
            new TuiDetailedNumberPrinter(9, true),
    };

    public static TuiDetailedNumberPrinter of(@Range(from = 1, to = 9) int num) {
        return of(num, false);
    }

    public static TuiDetailedNumberPrinter of(@Range(from = 1, to = 9) int num, boolean vertical) {
        return vertical ? VERTICAL_NUMBER_PRINTERS[num - 1] : NUMBER_PRINTERS[num - 1];
    }

    private final int num;
    private final boolean vertical;

    private TuiDetailedNumberPrinter(int num, boolean vertical) {
        this.num = num;
        this.vertical = vertical;
    }

    @Override
    public void print(TuiPrintStream out) {
        if (!vertical)
            printNumber(out);
        else
            printVerticalNumber(out);
    }

    private void printNumber(TuiPrintStream out) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++)
            sb.append(NumberLine(num, i)).append('\n');
        out.print(sb);
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

    private void printVerticalNumber(TuiPrintStream out) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PXL_COLS_FOR_NUMBERS; i++)
            sb.append(VerticalNumberLine(num, i)).append('\n');
        out.print(sb);
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
        for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++) {
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

    /**
     * takes a list of 5 elements of Strings of length == 24 of a NUMBER defined in TuiPrinter and converts it in to
     * a list of 24 elements of Strings of length == 5
     */
    private static List<String> convertToVerticalNumber(List<String> number) {
        List<String> ss = new ArrayList<>();
        for (int i = 0; i < PXL_COLS_FOR_NUMBERS; i++)
            ss.add(String.valueOf(number.get(4).charAt(i)) + number.get(3).charAt(i) +
                    number.get(2).charAt(i) + number.get(1).charAt(i) +
                    number.get(0).charAt(i));
        return ss;
    }

    @Override
    public TuiSize getSize() {
        return !vertical
                ? new TuiSize(PXL_ROWS_FOR_NUMBERS, PXL_COLS_FOR_NUMBERS * pxl.length())
                : new TuiSize(PXL_COLS_FOR_NUMBERS, PXL_ROWS_FOR_NUMBERS * pxl.length());
    }
}
