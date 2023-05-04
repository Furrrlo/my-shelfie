package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

class TuiDetailedCommonGoalPrinter implements TuiPrinter2 {
    public static final int PXL_FOR_COMMON_GOAL = 24;
    private static final @Unmodifiable List<String> TWO_SQUARES = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWBBBBBBBBBBBBBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR",
            "RWWBWRRRWBWRRRWBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR", "RWWBWRRRWBWRRRWBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR",
            "RWWBBBBBBBBBBBBBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR", "RWWBWRRRWBWRRRWBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR",
            "RWWBWRRRWBWRRRWBWWWWWWWR", "RWWBWWWWWBWWWWWBWWWWWWWR", "RWWBBBBBBBBBBBBBWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWWWWWWWWWWBWWWBWBBBWWWR", "RWWWWWWWWWWWBWBWWWWBWWWR", "RWWWWWWWWWWWWBWWWBBBWWWR", "RWWWWWWWWWWWBWBWWBWWWWWR",
            "RWWWWWWWWWWBWWWBWBBBWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> TWO_ALL_DIFF_COLUMNS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWBBBBWWWWWWWWWWWWWWWR", "RWWWBWWBWWWWWWWWWWWWWWWR",
            "RWWWBWWBWWWWWWWWWWWWWWWR", "RWWWBBBBWWBBBBBBBBBWWWWR", "RWWWBWWBWWBWWWWWWWBWWWWR", "RWWWBWWBWWBWWWWRWWBWWWWR",
            "RWWWBBBBWWBWRRRRRWBWWWWR", "RWWWBWWBWWBWWWRWWWBWWWWR", "RWWWBWWBWWBWRRRRRWBWWWWR", "RWWWBBBBWWBWWRWWWWBWWWWR",
            "RWWWBWWBWWBWWWWWWWBWWWWR", "RWWWBWWBWWBBBBBBBBBWWWWR", "RWWWBBBBWWWWWWWWWWWWWWWR", "RWWWBWWBWWWBWWWBWBBBWWWR",
            "RWWWBWWBWWWWBWBWWWWBWWWR", "RWWWBBBBWWWWWBWWWBBBWWWR", "RWWWBWWBWWWWBWBWWBWWWWWR", "RWWWBWWBWWWBWWWBWBBBWWWR",
            "RWWWBBBBWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> FOUR_QUADRIPLETS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWBBBBBWWWWWWWWWWWWWWWR",
            "RWWBWWWBWWWWWWWWWWWWWWWR", "RWWBWWWBWWBBBBBBBBBWWWWR", "RWWBWWWBWWBWWWWWWWBWWWWR", "RWWBBBBBWWBWWWWWWWBWWWWR",
            "RWWBWWWBWWBWRRRRRWBWWWWR", "RWWBWWWBWWBWWWWWWWBWWWWR", "RWWBWWWBWWBWRRRRRWBWWWWR", "RWWBBBBBWWBWWWWWWWBWWWWR",
            "RWWBWWWBWWBWWWWWWWBWWWWR", "RWWBWWWBWWBBBBBBBBBWWWWR", "RWWBWWWBWWWWWWWWWWWWWWWR", "RWWBBBBBWWWBWWWBWBWBWWWR",
            "RWWBWWWBWWWWBWBWWBWBWWWR", "RWWBWWWBWWWWWBWWWBWBWWWR", "RWWBWWWBWWWWBWBWWBBBWWWR", "RWWBBBBBWWWBWWWBWWWBWWWR",
            "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> SIX_COUPLES = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWBBBBBBBWWWWWWWWWWWWWR", "RWWBWWWWWBWWWWWWWWWWWWWR", "RWWBWRRRWBWWWWWWWWWWWWWR",
            "RWWBWWWWWBWWWWWWWWWWWWWR", "RWWBWRRRWBWBWWWBWBBBWWWR", "RWWBWWWWWBWWBWBWWBWWWWWR", "RWWBBBBBBBWWWBWWWBBBWWWR",
            "RWWBWWWWWBWWBWBWWBWBWWWR", "RWWBWRRRWBWBWWWBWBBBWWWR", "RWWBWWWWWBWWWWWWWWWWWWWR", "RWWBWRRRWBWWWWWWWWWWWWWR",
            "RWWBWWWWWBWWWWWWWWWWWWWR", "RWWBBBBBBBWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> THREE_COLUMNS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBBBBWRRRRRWRRRWWRWRWWR",
            "RWBWWBWRWRWRWRWRWWWRWWWR", "RWBWWBWRWRWRWRRRRWRWRWWR", "RWBBBBWWWWWWWWWWWWWWWWWR", "RWBWWBWRRWBBBBBBBBBWWWWR",
            "RWBWWBWWRWBWWWWWWWBWWWWR", "RWBBBBWRRWBWWWWRWWBWWWWR", "RWBWWBWWRWBWRRRRRWBWWWWR", "RWBWWBWRRWBWWWRWWWBWWWWR",
            "RWBBBBWWWWBWRRRRRWBWWWWR", "RWBWWBWWWWBWWRWWWWBWWWWR", "RWBWWBWWWWBWWWWWWWBWWWWR", "RWBBBBWWWWBBBBBBBBBWWWWR",
            "RWBWWBWWWWWWWWWWWWWWWWWR", "RWBWWBWWWWWBWWWBWBBBWWWR", "RWBBBBWWWWWWBWBWWWWBWWWR", "RWBWWBWWWWWWWBWWWBBBWWWR",
            "RWBWWBWWWWWWBWBWWWWBWWWR", "RWBBBBWWWWWBWWWBWBBBWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> TWO_ALL_DIFF_ROWS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBBBBBBBBBBBBBBBBWWWWWR", "RWBWWBWWBWWBWWBWWBWWWWWR", "RWBWWBWWBWWBWWBWWBWWWWWR",
            "RWBBBBBBBBBBBBBBBBWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBBBBBBBBBWWWWWWWWWWWWR", "RWBWWWWWWWBWWWWWWWWWWWWR",
            "RWBWWWWRWWBWBWWWBWBBBWWR", "RWBWRRRRRWBWWBWBWWWWBWWR", "RWBWWWRWWWBWWWBWWWBBBWWR", "RWBWRRRRRWBWWBWBWWBWWWWR",
            "RWBWWRWWWWBWBWWWBWBBBWWR", "RWBWWWWWWWBWWWWWWWWWWWWR", "RWBBBBBBBBBWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> FOUR_ROWS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWRRRRRWRRRWWRWRWWWWWWWR", "RWRWRWRWRWRWWWRWWWWWWWWR",
            "RWRWRWRWRRRRWRWRWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWWWWWRRWBBBBBBBBBWR", "RWWWWWWWWWWRWBWWWWWWWBWR",
            "RWWWWWWWWWRRWBWWWWRWWBWR", "RWWWWWWWWWWRWBWRRRRRWBWR", "RWWWWWWWWWRRWBWWWRWWWBWR", "RWWWWWWWWWWWWBWRRRRRWBWR",
            "RWBWWWBWBWBWWBWWRWWWWBWR", "RWWBWBWWBWBWWBWWWWWWWBWR", "RWWWBWWWBWBWWBBBBBBBBBWR", "RWWBWBWWBBBWWWWWWWWWWWWR",
            "RWBWWWBWWWBWWWWWWWWWWWWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWWWWWBBBBBBBBBBBBBBBBWR", "RWWWWWBWWBWWBWWBWWBWWBWR",
            "RWWWWWBWWBWWBWWBWWBWWBWR", "RWWWWWBBBBBBBBBBBBBBBBWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final @Unmodifiable List<String> ALL_CORNERS = List.of(
            "RRRRRRRRRRRRRRRRRRRRRRRR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBBBBBBBWBBWBWBBBBBBBWR", "RWBWWWWWBWWWWWWBWWWWWBWR",
            "RWBWRRRWBWWWWWWBWRRRWBWR", "RWBWWWWWBWWWWWWBWWWWWBWR", "RWBWRRRWBWWWWWWBWRRRWBWR", "RWBWWWWWBWWWWWWBWWWWWBWR",
            "RWBBBBBBBWWWWWWBBBBBBBWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBWWWWWWWWWWWWWWWWWWBWR", "RWWWWWWWWWWWWWWWWWWWWWWR",
            "RWBWWWWWWWWWWWWWWWWWWBWR", "RWBWWWWWWWWWWWWWWWWWWBWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RWBBBBBBBWWWWWWBBBBBBBWR",
            "RWBWWWWWBWWWWWWBWWWWWBWR", "RWBWRRRWBWWWWWWBWRRRWBWR", "RWBWWWWWBWWWWWWBWWWWWBWR", "RWBWRRRWBWWWWWWBWRRRWBWR",
            "RWBWWWWWBWWWWWWBWWWWWBWR", "RWBBBBBBBWBBWBWBBBBBBBWR", "RWWWWWWWWWWWWWWWWWWWWWWR", "RRRRRRRRRRRRRRRRRRRRRRRR");
    private static final Map<Type, TuiDetailedCommonGoalPrinter> COMMON_GOAL_PRINTERS = Arrays.stream(Type.values())
            .collect(Collectors.toUnmodifiableMap(Function.identity(), TuiDetailedCommonGoalPrinter::new));

    public static TuiDetailedCommonGoalPrinter of(Type type) {
        return Objects.requireNonNull(COMMON_GOAL_PRINTERS.get(type), "Unrecognized common goal for this " + type);
    }

    private final Type type;

    public TuiDetailedCommonGoalPrinter(Type type) {
        this.type = type;
    }

    public static StringBuilder CommonGoalLine(int lineIndex, Type type) {
        String spriteLine = switch (type) {
            case TWO_SQUARES -> TWO_SQUARES.get(lineIndex);
            case TWO_ALL_DIFF_COLUMNS -> TWO_ALL_DIFF_COLUMNS.get(lineIndex);
            case FOUR_QUADRIPLETS -> FOUR_QUADRIPLETS.get(lineIndex);
            case SIX_COUPLES -> SIX_COUPLES.get(lineIndex);
            case THREE_COLUMNS -> THREE_COLUMNS.get(lineIndex);
            case TWO_ALL_DIFF_ROWS -> TWO_ALL_DIFF_ROWS.get(lineIndex);
            case FOUR_ROWS -> FOUR_ROWS.get(lineIndex);
            case ALL_CORNERS -> ALL_CORNERS.get(lineIndex);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        StringBuilder ss = new StringBuilder();
        ss.append(ConsoleColors.RESET);
        for (int i = 0; i < 24; i++) {
            String consoleColor = switch (spriteLine.charAt(i)) {
                case 'B' -> ConsoleColors.BLACK_BACKGROUND;
                case 'R' -> ConsoleColors.RED_BACKGROUND_BRIGHT;
                case 'W' -> ConsoleColors.WHITE_BACKGROUND_BRIGHT;
                default -> ConsoleColors.RESET;
            };
            ss.append(consoleColor).append(pxl);
        }
        ss.append(ConsoleColors.RESET);
        return ss;
    }

    @Override
    public void print(TuiPrintStream out) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PXL_FOR_COMMON_GOAL; i++)
            sb.append(CommonGoalLine(i, type)).append('\n');
        out.print(sb);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(PXL_FOR_COMMON_GOAL, PXL_FOR_COMMON_GOAL * pxl.length());
    }
}