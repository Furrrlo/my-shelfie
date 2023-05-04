package it.polimi.ingsw.client.tui;

import com.google.errorprone.annotations.MustBeClosed;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiPrintStream;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import static org.fusesource.jansi.internal.CLibrary.ioctl;
import static org.fusesource.jansi.internal.Kernel32.*;

class TuiPrintStream extends PrintStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(TuiPrintStream.class);

    private static final int DEFAULT_TERMINAL_WIDTH = 80;
    private static final int DEFAULT_TERMINAL_HEIGHT = 24;

    public static final String pxl = "   ";

    public static final int BOX_TOP = 0x1;
    public static final int BOX_BOTTOM = 0x10;
    public static final int BOX_LEFT = 0x100;
    public static final int BOX_RIGHT = 0x1000;

    /** First ANSI escape code character */
    static final char FIRST_ESC_CHAR = '\033';
    /** Second ANSI escape code character */
    static final char SECOND_ESC_CHAR = '[';

    /* See https://www.xfree86.org/current/ctlseqs.html */

    /** Control Sequence Introducer */
    static final String CSI = FIRST_ESC_CHAR + String.valueOf(SECOND_ESC_CHAR);
    /** Device Control String */
    static final String DCS = FIRST_ESC_CHAR + "P";
    /** String Terminator */
    static final String ST = FIRST_ESC_CHAR + "\\";

    /** Stack used to keep translations applied to the console screen */
    private final Deque<Translation> translationStack = new ArrayDeque<>();

    public static TuiPrintStream installToStdOut() {
        if (System.out instanceof TuiPrintStream)
            return (TuiPrintStream) System.out;

        installToStdStreams();
        return (TuiPrintStream) System.out;
    }

    public static TuiPrintStream installToStdErr() {
        if (System.err instanceof TuiPrintStream)
            return (TuiPrintStream) System.err;

        installToStdStreams();
        return (TuiPrintStream) System.err;
    }

    private static void installToStdStreams() {
        // Fix for jansi, ot uses the two old sun internal properties
        var stdoutEncoding = System.getProperty("stdout.encoding");
        if (stdoutEncoding == null && System.console() != null)
            stdoutEncoding = System.console().charset().name();

        if (stdoutEncoding != null) {
            System.setProperty("sun.stdout.encoding", stdoutEncoding);
            System.setProperty("sun.stderr.encoding", stdoutEncoding);
        }

        WindowsDetection.detectSupportedCapabilities();
        // Fix for jansi not detecting colors at all on Windows
        if (WindowsDetection.IS_WINDOWS) {
            System.setProperty(AnsiConsole.JANSI_OUT_COLORS, switch (WindowsDetection.OUT_SUPPORTED_COLORS) {
                case Colors16 -> AnsiConsole.JANSI_COLORS_16;
                case Colors256 -> AnsiConsole.JANSI_COLORS_256;
                case TrueColor -> AnsiConsole.JANSI_COLORS_TRUECOLOR;
            });
            System.setProperty(AnsiConsole.JANSI_ERR_COLORS, switch (WindowsDetection.ERR_SUPPORTED_COLORS) {
                case Colors16 -> AnsiConsole.JANSI_COLORS_16;
                case Colors256 -> AnsiConsole.JANSI_COLORS_256;
                case TrueColor -> AnsiConsole.JANSI_COLORS_TRUECOLOR;
            });

            LOGGER.info("Detected stdout color capabilities: {}", WindowsDetection.OUT_SUPPORTED_COLORS);
            LOGGER.info("Detected stderr color capabilities: {}", WindowsDetection.ERR_SUPPORTED_COLORS);
        }
        AnsiConsole.systemInstall();

        TuiPrintStream out = new TuiPrintStream(System.out, System.console() != null
                ? System.console().charset()
                : Charset.defaultCharset());
        TuiPrintStream err = new TuiPrintStream(System.err, System.console() != null
                ? System.console().charset()
                : Charset.defaultCharset());
        System.setOut(out);
        System.setErr(err);
    }

    public TuiPrintStream(OutputStream out, Charset encoding) {
        this(new TranslatingOutputStream(out), encoding, null);
    }

    /**
     * Internal constructor to be able to have a reference to out after passing it to the
     * super constructor
     */
    private TuiPrintStream(TranslatingOutputStream out, Charset encoding, @SuppressWarnings("unused") @Nullable Void unused) {
        super(out, false, encoding);
        out.outer = this;
    }

    private OutputStream getInnerOut() {
        return ((TranslatingOutputStream) out).out();
    }

    public TuiSize getTerminalSize() {
        return new TuiSize(getTerminalRows(), getTerminalCols());
    }

    public TuiCoords getCursorPos() {
        // TODO: works on windows, but can't rely on it cause it's not implemented for *nix
        if (true)
            return new TuiCoords(0, 0);

        var innerOut = getInnerOut();
        final AnsiType type = innerOut instanceof AnsiOutputStream ansiOut
                ? ansiOut.getType()
                : innerOut instanceof AnsiPrintStream ansiPrint
                        ? ansiPrint.getType()
                        : null;
        return type == null ? new TuiCoords(0, 0) : switch (type) {
            case Unsupported, Redirected -> new TuiCoords(0, 0);
            case Emulation, VirtualTerminal -> {
                final long console = GetStdHandle(STD_OUTPUT_HANDLE);
                Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                GetConsoleScreenBufferInfo(console, info);
                yield new TuiCoords(info.cursorPosition.y, info.cursorPosition.x);
            }
            case Native -> {
                // IS_CONEMU || IS_CYGWIN || IS_MSYSTEM
                if (WindowsDetection.IS_WINDOWS) {
                    final long console = GetStdHandle(STD_OUTPUT_HANDLE);
                    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                    GetConsoleScreenBufferInfo(console, info);
                    yield new TuiCoords(info.cursorPosition.y, info.cursorPosition.x);
                }

                yield new TuiCoords(0, 0);
            }
        };
    }

    public int getTerminalCols() {
        var innerOut = getInnerOut();
        if (innerOut instanceof AnsiOutputStream ansiOut) {
            final int cols = ansiOut.getTerminalWidth();
            return cols == 0 ? DEFAULT_TERMINAL_WIDTH : cols;
        }

        if (innerOut instanceof AnsiPrintStream ansiPrint) {
            final int cols = ansiPrint.getTerminalWidth();
            return cols == 0 ? DEFAULT_TERMINAL_WIDTH : cols;
        }

        return DEFAULT_TERMINAL_WIDTH;
    }

    public int getTerminalRows() {
        var innerOut = getInnerOut();
        final AnsiType type = innerOut instanceof AnsiOutputStream ansiOut
                ? ansiOut.getType()
                : innerOut instanceof AnsiPrintStream ansiPrint
                        ? ansiPrint.getType()
                        : null;
        return type == null ? DEFAULT_TERMINAL_HEIGHT : switch (type) {
            case Unsupported, Redirected -> DEFAULT_TERMINAL_HEIGHT;
            case Emulation, VirtualTerminal -> {
                final long console = GetStdHandle(STD_OUTPUT_HANDLE);
                Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                GetConsoleScreenBufferInfo(console, info);
                yield info.windowHeight();
            }
            case Native -> {
                // IS_CONEMU || IS_CYGWIN || IS_MSYSTEM
                if (WindowsDetection.IS_WINDOWS) {
                    final long console = GetStdHandle(STD_OUTPUT_HANDLE);
                    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                    GetConsoleScreenBufferInfo(console, info);
                    yield info.windowHeight();
                }
                // *nix TTY
                CLibrary.WinSize sz = new CLibrary.WinSize();
                ioctl(CLibrary.STDOUT_FILENO, CLibrary.TIOCGWINSZ, sz);
                yield sz.ws_row;
            }
        };
    }

    /**
     * Erase all content on the entire console screen
     *
     * @implNote does not work well on Windows (it does not erase the content)
     */
    public void eraseScreen() {
        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(2);
            print('J');
        }
    }

    /** Erase all content visible on the console screen */
    public void eraseInDisplay() {
        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print('J');
        }
    }

    @MustBeClosed
    public NoExceptionAutoCloseable saveCursorPos() {
        synchronized (this) {
            // Cursor saving is not standardized, see https://github.com/fusesource/jansi/issues/226
            // Print both known sequences
            // DEC sequence
            print(FIRST_ESC_CHAR);
            print(7);
            // SCO sequence
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print('s');
            return new RestoreCursorPosCloseable();
        }
    }

    /** Closeable to restore the cursor position */
    private class RestoreCursorPosCloseable implements NoExceptionAutoCloseable {
        @Override
        public void close() {
            synchronized (TuiPrintStream.this) {
                // Cursor saving is not standardized, see https://github.com/fusesource/jansi/issues/226
                // Print both known sequences
                // SCO sequence
                print(FIRST_ESC_CHAR);
                print(SECOND_ESC_CHAR);
                print('u');
                // DEC sequence
                print(FIRST_ESC_CHAR);
                print(8);
            }
        }
    }

    /**
     * Moves the cursor of the given row + col relative to the current translation position
     * and translates each subsequent printed new line to said position.
     *
     * When the returned object is closed, the current translation is brought back to the
     * previous position.
     *
     * @param row row to translate to, relative to the current translation position
     * @param col col to translate to, relative to the current translation position
     * @return closeable to go back to the previous translation position
     */
    @MustBeClosed
    public NoExceptionAutoCloseable translateCursor(int row, int col) {
        synchronized (this) {
            var lastTranslation = translationStack.peek();

            var currPos = getCursorPos();
            row = Math.max(0, lastTranslation != null ? lastTranslation.row() + row : currPos.row() + row);
            col = Math.max(0, lastTranslation != null ? lastTranslation.col() + col : currPos.col() + col);
            cursor(row, col);

            var translation = new Translation(row, col);
            translationStack.push(translation);
            return new PopTranslationCloseable(translation);
        }
    }

    /**
     * Moves the cursor of the given col relative to the current translation position
     * and translates each subsequent printed new line to said position.
     *
     * When the returned object is closed, the current translation is brought back to the
     * previous position.
     *
     * @param col col to translate to, relative to the current translation position
     * @return closeable to go back to the previous translation position
     */
    @MustBeClosed
    public NoExceptionAutoCloseable translateCursorToCol(int col) {
        synchronized (this) {
            var lastTranslation = translationStack.peek();

            var currPos = getCursorPos();
            int row = Math.max(0, lastTranslation != null ? lastTranslation.row() : currPos.row());
            col = Math.max(0, lastTranslation != null ? lastTranslation.col() + col : currPos.col() + col);
            cursorToCol(col);

            var translation = new Translation(row, col);
            translationStack.push(translation);
            return new PopTranslationCloseable(translation);
        }
    }

    /**
     * Object representing a translation, in absolute rows + cols
     * 
     * @param row absolute row
     * @param col absolute col
     */
    private record Translation(int row, int col) {
    }

    /** Closeable to pop a given translation from the translations stack */
    private class PopTranslationCloseable implements NoExceptionAutoCloseable {

        private final Translation translation;

        public PopTranslationCloseable(Translation translation) {
            this.translation = translation;
        }

        @Override
        public void close() {
            synchronized (TuiPrintStream.this) {
                if (Objects.equals(translationStack.peek(), translation))
                    translationStack.pop();
            }
        }
    }

    /**
     * Move the cursor to the given absolute row and col
     *
     * @param row absolute row to move to
     * @param col absolute col to move to
     */
    public void cursor(int row, int col) {
        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(Math.max(0, row) + 1);
            print(';');
            print(Math.max(0, col) + 1);
            print('H');
        }
    }

    /**
     * Move the cursor to the given absolute col
     *
     * @param col absolute col to move to
     */
    public void cursorToCol(int col) {
        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(Math.max(0, col) + 1);
            print('G');
        }
    }

    /**
     * Move cursor up of the given number of lines
     *
     * @param y number of lines
     */
    public void moveCursorUp(int y) {
        if (y == 0)
            return;

        if (y < 0) {
            moveCursorDown(-y);
            return;
        }

        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(y);
            print('A');
        }
    }

    /**
     * Move cursor down of the given number of lines
     *
     * @param y number of lines
     */
    public void moveCursorDown(int y) {
        if (y == 0)
            return;

        if (y < 0) {
            moveCursorUp(-y);
            return;
        }

        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(y);
            print('B');
        }
    }

    /**
     * Move cursor to the right of the given number of cols
     *
     * @param x number of cols
     */
    public void moveCursorRight(int x) {
        if (x == 0)
            return;

        if (x < 0) {
            moveCursorLeft(-x);
            return;
        }

        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(x);
            print('C');
        }
    }

    /**
     * Move cursor to the left of the given number of cols
     *
     * @param x number of cols
     */
    public void moveCursorLeft(int x) {
        if (x == 0)
            return;

        if (x < 0) {
            moveCursorRight(-x);
            return;
        }

        synchronized (this) {
            print(FIRST_ESC_CHAR);
            print(SECOND_ESC_CHAR);
            print(x);
            write('D');
        }
    }

    /**
     * Move cursor of the given number of rows and cols
     *
     * @param cols number of cols
     * @param rows number of rows
     */
    public void moveCursor(int rows, int cols) {
        synchronized (this) {
            moveCursorDown(rows);
            moveCursorRight(cols);
        }
    }

    public TuiRect printAligned(TuiPrinter2 printer, TuiRect rect, TuiHAlignment hAlign, TuiVAlignment vAlign) {
        final var drawnRect = getAlignedRect(printer.getSize(), rect, hAlign, vAlign);

        cursor(0, 0);
        try (var ignored = translateCursor(drawnRect.row(), drawnRect.col())) {
            printer.print(this);
            return drawnRect;
        }
    }

    public TuiRect getAlignedRect(TuiSize sizeToPrint, TuiRect rect, TuiHAlignment hAlign, TuiVAlignment vAlign) {
        return new TuiRect(
                rect.row() + switch (vAlign) {
                    case TOP -> 0;
                    case BOTTOM -> rect.size().rows() - sizeToPrint.rows();
                    case CENTER -> rect.size().rows() / 2 - sizeToPrint.rows() / 2;
                },
                rect.col() + switch (hAlign) {
                    case LEADING, LEFT -> 0;
                    case TRAILING, RIGHT -> rect.size().cols() - sizeToPrint.cols();
                    case CENTER -> rect.size().cols() / 2 - sizeToPrint.cols() / 2;
                },
                sizeToPrint);
    }

    public TuiRect printAligned(TuiPrinter2 printer, TuiSize rectSize, TuiHAlignment hAlign, TuiVAlignment vAlign) {
        final var size = printer.getSize();
        final var cursorPos = getCursorPos();
        final var drawnRect = new TuiRect(
                cursorPos.row() + switch (vAlign) {
                    case TOP -> 0;
                    case BOTTOM -> rectSize.rows() - size.rows();
                    case CENTER -> rectSize.rows() / 2 - size.rows() / 2;
                },
                cursorPos.col() + switch (hAlign) {
                    case LEADING, LEFT -> 0;
                    case TRAILING, RIGHT -> rectSize.cols() - size.cols();
                    case CENTER -> rectSize.cols() / 2 - size.cols() / 2;
                },
                size);

        cursor(0, 0);
        try (var ignored = translateCursor(drawnRect.row(), drawnRect.col())) {
            printer.print(this);
            return drawnRect;
        }
    }

    public void printBox(TuiRect box, @MagicConstant(flags = { BOX_TOP, BOX_BOTTOM, BOX_LEFT, BOX_RIGHT }) int sides) {
        boolean hasTop = (sides & BOX_TOP) != 0;
        boolean hasBottom = (sides & BOX_BOTTOM) != 0;
        boolean hasLeft = (sides & BOX_LEFT) != 0;
        boolean hasRight = (sides & BOX_RIGHT) != 0;

        cursor(box.row(), box.col());
        if (hasTop || hasLeft)
            print(hasTop && hasLeft ? '┌' : hasTop ? '─' : '│');

        if (hasTop) {
            for (int i = box.col() + 1; i <= box.lastCol() - 1; i++)
                print('─');
        }

        if (hasTop || hasRight) {
            if (!hasTop)
                cursor(box.row(), box.lastCol());
            print(hasTop && hasRight ? '┐' : hasTop ? '─' : '│');
        }

        cursor(box.lastRow(), box.col());
        if (hasBottom || hasLeft)
            print(hasBottom && hasLeft ? '└' : hasBottom ? '─' : '│');

        if (hasBottom) {
            for (int i = box.col() + 1; i <= box.lastCol() - 1; i++)
                print('─');
        }

        if (hasBottom || hasRight) {
            if (!hasBottom)
                cursor(box.lastRow(), box.lastCol());
            print(hasBottom && hasRight ? '┘' : hasBottom ? '─' : '│');
        }

        if (hasLeft) {
            cursor(box.row() + 1, 0);
            try (var ignored = translateCursorToCol(box.col())) {
                for (int i = box.row() + 1; i <= box.lastRow() - 2; i++)
                    println("│");
                print("│"); // Don't print a new line to prevent scrolling
            }
        }

        if (hasRight) {
            cursor(box.row() + 1, 0);
            try (var ignored = translateCursorToCol(box.lastCol())) {
                for (int i = box.row() + 1; i <= box.lastRow() - 2; i++)
                    println("│");
                print("│"); // Don't print a new line to prevent scrolling
            }
        }
    }

    /** Output stream filter which applies the translation stack */
    private static class TranslatingOutputStream extends FilterOutputStream {

        @SuppressWarnings({ "NotNullFieldNotInitialized", "NullAway" })
        TuiPrintStream outer;

        public TranslatingOutputStream(OutputStream out) {
            super(out);
        }

        private OutputStream out() {
            return out;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);

            Translation t;
            if (b == '\n' && (t = outer.translationStack.peek()) != null && t.col != -1)
                writeEscapeCommand('G', t.col);
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            int lastWritten = off;
            for (int i = off; i < len; i++) {
                Translation t;
                if (b[i] == '\n' && (t = outer.translationStack.peek()) != null && t.col != -1) {
                    // Write as far as we got
                    out.write(b, lastWritten, i - lastWritten + 1);
                    lastWritten = i + 1;
                    writeEscapeCommand('G', t.col);
                }
            }

            // Write leftovers
            if (lastWritten < len)
                out.write(b, lastWritten, len - lastWritten);
        }

        private void writeEscapeCommand(char command, int arg) throws IOException {
            out.write(FIRST_ESC_CHAR);
            out.write(SECOND_ESC_CHAR);
            String args = String.valueOf(arg + 1);
            for (int i = 0; i < args.length(); i++)
                out.write(args.charAt(i));
            out.write(command);
        }
    }
}
