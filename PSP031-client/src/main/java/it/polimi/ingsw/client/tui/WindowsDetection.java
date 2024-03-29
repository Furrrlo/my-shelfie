package it.polimi.ingsw.client.tui;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.WindowsSupport;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import java.util.function.IntConsumer;

import static it.polimi.ingsw.client.tui.TuiPrintStream.*;
import static org.fusesource.jansi.internal.Kernel32.*;

/**
 * Detect which color modes are supported by the current terminal, on Windows.
 * Windows terminal supports truecolor, but doesn't set any environment variables, so jansi doesn't work properly
 */
@SuppressWarnings("SameParameterValue")
class WindowsDetection {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsDetection.class);

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
    public static final AnsiColors OUT_SUPPORTED_COLORS;
    public static final AnsiColors ERR_SUPPORTED_COLORS;

    static {
        if (IS_WINDOWS) {
            var res = detectSupportedColorsByAnsiSequence();
            OUT_SUPPORTED_COLORS = res.out;
            ERR_SUPPORTED_COLORS = res.err;
        } else {
            OUT_SUPPORTED_COLORS = AnsiColors.Colors16;
            ERR_SUPPORTED_COLORS = AnsiColors.Colors16;
        }
    }

    private WindowsDetection() {
    }

    public static void detectSupportedCapabilities() {
        // Force static initializer to run
    }

    private static final int ENABLE_LINE_INPUT = 0x0002;
    private static final int ENABLE_ECHO_INPUT = 0x0004;
    private static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

    private static StreamsColors detectSupportedColorsByAnsiSequence() {
        long inConsole = GetStdHandle(STD_INPUT_HANDLE);
        final int[] mode = new int[1];
        if (GetConsoleMode(inConsole, mode) == 0)
            return new StreamsColors(AnsiColors.Colors16, AnsiColors.Colors16);

        rawMode(inConsole, true);
        try {
            return new StreamsColors(
                    doDetectSupportedColorsByAnsiSequence(GetStdHandle(STD_OUTPUT_HANDLE), System.out, inConsole),
                    doDetectSupportedColorsByAnsiSequence(GetStdHandle(STD_ERROR_HANDLE), System.err, inConsole));
        } finally {
            rawMode(inConsole, false);
        }
    }

    private static AnsiColors doDetectSupportedColorsByAnsiSequence(long outConsole, PrintStream out, long inConsole) {
        final int[] mode = new int[1];
        final boolean isOutConsole = GetConsoleMode(outConsole, mode) != 0;
        // Try to enable Virtual Terminal
        if (!isOutConsole || SetConsoleMode(outConsole, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) == 0)
            return AnsiColors.Colors16;

        try {
            // Test taken from here https://github.com/termstandard/colors
            // Simply try sending a truecolor value to the terminal,
            // followed by a query to ask what color it currently has.
            // If the response indicates the same color as was just set, then truecolor is supported.
            if (ansiCheckColor(out, inConsole, "48;2;1;2;3")) // set background color to RGB(1,2,3)
                return AnsiColors.TrueColor;
            if (ansiCheckColor(out, inConsole, "48;5;225")) // set background color to color 225
                return AnsiColors.Colors256;
            return AnsiColors.Colors16;
        } catch (IOException ex) {
            LOGGER.error("Unexpected exception while trying to detect supported colors", ex);
            return AnsiColors.Colors16;
        } finally {
            // Disable Virtual Terminal
            GetConsoleMode(outConsole, mode);
            SetConsoleMode(outConsole, mode[0] & ~ENABLE_VIRTUAL_TERMINAL_PROCESSING);
        }
    }

    private static void rawMode(long console, boolean enabled) {
        final int[] mode = new int[1];
        GetConsoleMode(console, mode);
        var newMode = enabled
                ? mode[0] & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT)
                : mode[0] | ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT;

        if (SetConsoleMode(console, newMode) == 0)
            throw new UnsupportedOperationException(enabled
                    ? "Failed to enable raw mode"
                    : "Failed to disable raw mode",
                    new IOException(WindowsSupport.getLastErrorMessage()));
    }

    private static boolean ansiCheckColor(PrintStream out, long inConsole, String color) throws IOException {
        // Consume any leftover input
        consumeRemainingInput(inConsole, null);

        out.print(CSI + color + "m");
        out.print(DCS + "$qm" + ST); // DECRQSS (Request Selection or Setting) for SGR
        out.flush();

        try {
            var response = readResponseColor(inConsole);
            // Reset color
            out.print(CSI + "0m");
            out.flush();

            // The 0; is added by Windows Terminal to 'to reset the SGR attributes to the defaults'
            // https://github.com/microsoft/terminal/blob/20eabb35ba0e263f86bc4879eeb7a3b34334ab9b/src/terminal/adapter/adaptDispatch.cpp#L3682-L3685
            if (response.equals(color) || response.equals("0;" + color))
                return true;

            LOGGER.error("Read back incorrect color {}", response);
            return false;
        } catch (UnsupportedOperationException | TimeoutException e) {
            // Reset color
            out.print(CSI + "0m");
            out.flush();
            LOGGER.error("Failed to read back color", e);
            return false;
        }
    }

    private static String readResponseColor(long inConsole) throws IOException, TimeoutException {
        enum ParserState {
            LOOKING_FOR_ESC,
            LOOKING_FOR_DCS,
            LOOKING_FOR_UNDERSTOOD,
            LOOKING_FOR_$,
            LOOKING_FOR_REPORT,
            LOOKING_FOR_SGR,
            DONE,
            TIMEOUT,
            ERROR,
        }

        // DCS s $ r D...D ST
        // Expected: DCS P1 $ r <color> m";
        var responseBuilder = new StringBuilder();
        var responseColor = new StringBuilder();
        String errorString = null;

        var state = ParserState.LOOKING_FOR_ESC;
        while (state != ParserState.DONE && state != ParserState.ERROR && state != ParserState.TIMEOUT) {
            int ch;
            try {
                // First character needs to be read without timeout cause Reader#available() won't work
                ch = readWithTimeout(inConsole);
            } catch (TimeoutException ex) {
                state = ParserState.TIMEOUT;
                continue;
            }

            responseBuilder.append((char) ch);
            state = switch (state) {
                case LOOKING_FOR_ESC -> ch == FIRST_ESC_CHAR ? ParserState.LOOKING_FOR_DCS : ParserState.ERROR;
                case LOOKING_FOR_DCS -> ch == DCS.charAt(1) ? ParserState.LOOKING_FOR_UNDERSTOOD : ParserState.ERROR;
                case LOOKING_FOR_UNDERSTOOD -> {
                    if (ch == '1')
                        yield ParserState.LOOKING_FOR_$;
                    if (ch == '0')
                        errorString = "The DECRQSS was not understood";
                    yield ParserState.ERROR;
                }
                case LOOKING_FOR_$ -> ch == '$' ? ParserState.LOOKING_FOR_REPORT : ParserState.ERROR;
                case LOOKING_FOR_REPORT -> ch == 'r' ? ParserState.LOOKING_FOR_SGR : ParserState.ERROR;
                case LOOKING_FOR_SGR -> {
                    if (ch == 'm')
                        yield ParserState.DONE;

                    responseColor.append((char) ch);
                    yield ParserState.LOOKING_FOR_SGR;
                }
                case DONE, ERROR, TIMEOUT -> throw new AssertionError("Shouldn't get here with state " + state);
            };
        }
        // Consume remaining input
        consumeRemainingInput(inConsole, ch -> responseBuilder.append((char) ch));

        if (state == ParserState.DONE)
            return responseColor.toString();

        var response = responseBuilder.toString()
                .replace(String.valueOf(FIRST_ESC_CHAR), "ESC")
                .replace("\n", "\\n");
        if (state == ParserState.TIMEOUT)
            throw new TimeoutException("Timeout expired (collected response '" + response + "')");

        throw new UnsupportedOperationException(errorString != null
                ? errorString + "(response: '" + response + "')"
                : "Unrecognized response '" + response + '\'');
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void consumeRemainingInput(long inConsole, @Nullable IntConsumer consumer) throws IOException {
        try {
            while (true) {
                if (consumer == null)
                    readWithTimeout(inConsole);
                else
                    consumer.accept((char) readWithTimeout(inConsole));
            }
        } catch (TimeoutException ignored) {
            // No more input
        }
    }

    private static int readWithTimeout(long inConsole) throws IOException, TimeoutException {
        var timeoutMillis = 100;
        var startTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTimeMillis < timeoutMillis) {
            Thread.onSpinWait();

            // Peek first and if there's one, take it
            if (readConsoleInputHelper(inConsole, 1, true).length == 0)
                continue;

            INPUT_RECORD[] inputRecordArr;
            if ((inputRecordArr = readConsoleInputHelper(inConsole, 1, false)).length == 0)
                continue;
            // Discard anything that isn't a keyUp KEY_EVENT
            var inputRecord = inputRecordArr[0];
            if (inputRecord.eventType != INPUT_RECORD.KEY_EVENT || inputRecord.keyEvent.keyDown)
                continue;

            return inputRecord.keyEvent.uchar;
        }

        throw new TimeoutException();
    }

    private record StreamsColors(AnsiColors out, AnsiColors err) {
    }
}
