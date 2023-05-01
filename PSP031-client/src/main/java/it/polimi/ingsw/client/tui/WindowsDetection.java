package it.polimi.ingsw.client.tui;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.WindowsSupport;

import java.io.*;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static it.polimi.ingsw.client.tui.TuiPrintStream.*;
import static org.fusesource.jansi.internal.Kernel32.*;

@SuppressWarnings("SameParameterValue")
class WindowsDetection {

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
    public static final AnsiColors OUT_SUPPORTED_COLORS;
    public static final AnsiColors ERR_SUPPORTED_COLORS;

    static {
        long inConsole = 0;
        final int[] mode = new int[1];

        boolean shouldCheck = false;
        if (IS_WINDOWS) {
            inConsole = GetStdHandle(STD_INPUT_HANDLE);
            shouldCheck = GetConsoleMode(inConsole, mode) != 0;
        }

        if (shouldCheck) {
            rawMode(inConsole, true);
            InterruptibleInputStream iin = null;
            try {
                // InterruptibleInputStream must be created after entering raw mode
                // I think it's because by creating it before, windows sees that someone is
                // constantly trying to read input, so it doesn't apply it
                iin = InterruptibleInputStream.wrap(System.in, Thread.ofPlatform()
                        .name("stdin-read-th")
                        .factory());
                System.setIn(iin); // Set it back to System.in, so if anybody else tries to rewrap it, there won't be issues
                iin.configureDefaultTimeout(1000, TimeUnit.MILLISECONDS);

                OUT_SUPPORTED_COLORS = detectSupportedColors(GetStdHandle(STD_OUTPUT_HANDLE), System.out, iin);
                ERR_SUPPORTED_COLORS = detectSupportedColors(GetStdHandle(STD_ERROR_HANDLE), System.err, iin);
            } finally {
                if (iin != null)
                    iin.clearDefaultTimeout();
                rawMode(inConsole, false);
            }
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

    private static AnsiColors detectSupportedColors(long outConsole, PrintStream out, InputStream in) {
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
            var reader = new InputStreamReader(in, System.console() != null
                    ? System.console().charset()
                    : Charset.defaultCharset());
            if (checkColor(out, reader, "48;2;1;2;3")) // set background color to RGB(1,2,3)
                return AnsiColors.TrueColor;
            if (checkColor(out, reader, "48;5;225")) // set background color to color 225
                return AnsiColors.Colors256;
            return AnsiColors.Colors16;
        } catch (IOException ex) {
            // TODO: logging
            System.err.println("Unexpected exception while trying to detect supported colors");
            ex.printStackTrace();
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

    private static boolean checkColor(PrintStream out, Reader in, String color) throws IOException {
        out.print(CSI + color + "m");
        out.print(DCS + "$qm" + ST); // DECRQSS (Request Selection or Setting) for SGR
        out.flush();

        try {
            var response = readResponseColor(in);
            // Reset color
            out.print(CSI + "0m");
            out.flush();

            // The 0; is added by Windows Terminal to 'to reset the SGR attributes to the defaults'
            // https://github.com/microsoft/terminal/blob/20eabb35ba0e263f86bc4879eeb7a3b34334ab9b/src/terminal/adapter/adaptDispatch.cpp#L3682-L3685
            if (response.equals(color) || response.equals("0;" + color))
                return true;

            System.err.println("Read back incorrect color " + response);
            return false;
        } catch (UnsupportedOperationException | TimeoutException e) {
            // Reset color
            out.print(CSI + "0m");
            out.flush();
            // TODO: logging
            System.err.println("Failed to read back color");
            System.err.println(e.getMessage());
            return false;
        }
    }

    private static String readResponseColor(Reader in) throws IOException, TimeoutException {
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
                ch = readWithTimeout(in);
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
        while (in.ready())
            responseBuilder.append((char) in.read());

        if (state == ParserState.DONE)
            return responseColor.toString();

        var response = responseBuilder.toString().replace(String.valueOf(FIRST_ESC_CHAR), "ESC");
        if (state == ParserState.TIMEOUT)
            throw new TimeoutException("Timeout expired (collected response '" + response + "')");

        throw new UnsupportedOperationException(errorString != null
                ? errorString + "(response: '" + response + "')"
                : "Unrecognized response '" + response + '\'');
    }

    private static int readWithTimeout(Reader in) throws IOException, TimeoutException {
        int ch;
        try {
            ch = in.read();
        } catch (InterruptedByTimeoutException ex) {
            throw (TimeoutException) new TimeoutException().initCause(ex);
        }

        if (ch == -1)
            throw new EOFException();
        return ch;
    }
}
