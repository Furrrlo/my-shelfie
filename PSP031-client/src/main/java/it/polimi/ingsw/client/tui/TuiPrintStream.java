package it.polimi.ingsw.client.tui;

import com.google.errorprone.annotations.MustBeClosed;
import org.fusesource.jansi.AnsiPrintStream;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Objects;

import static org.fusesource.jansi.internal.CLibrary.ioctl;
import static org.fusesource.jansi.internal.Kernel32.*;

class TuiPrintStream extends PrintStream {

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
    private static final int DEFAULT_TERMINAL_WIDTH = 80;
    private static final int DEFAULT_TERMINAL_HEIGHT = 24;

    /** First ANSI escape code character */
    @VisibleForTesting
    static final char FIRST_ESC_CHAR = '\033';
    /** Second ANSI escape code character */
    private static final char SECOND_ESC_CHAR = '[';

    /** Stack used to keep translations applied to the console screen */
    private final Deque<Translation> translationStack = new ArrayDeque<>();

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
                if (IS_WINDOWS) {
                    final long console = GetStdHandle(STD_OUTPUT_HANDLE);
                    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                    GetConsoleScreenBufferInfo(console, info);
                    yield info.windowHeight();
                }
                // *nix TTY
                CLibrary.WinSize sz = new CLibrary.WinSize();
                ioctl(CLibrary.STDOUT_FILENO, CLibrary.TIOCGWINSZ, sz);
                yield sz.ws_col;
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(2);
            write('J');
        }
    }

    /** Erase all content visible on the console screen */
    public void eraseInDisplay() {
        synchronized (this) {
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            write('J');
        }
    }

    @MustBeClosed
    public NoExceptionAutoCloseable saveCursorPos() {
        synchronized (this) {
            // Cursor saving is not standardized, see https://github.com/fusesource/jansi/issues/226
            // Print both known sequences
            // DEC sequence
            write(FIRST_ESC_CHAR);
            print(7);
            // SCO sequence
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            write('s');
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
                write(FIRST_ESC_CHAR);
                write(SECOND_ESC_CHAR);
                write('u');
                // DEC sequence
                write(FIRST_ESC_CHAR);
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

            row = Math.max(0, lastTranslation != null ? lastTranslation.row() + row : row);
            col = Math.max(0, lastTranslation != null ? lastTranslation.col() + col : col);
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

            int row = Math.max(0, lastTranslation != null ? lastTranslation.row() : 0);
            col = Math.max(0, lastTranslation != null ? lastTranslation.col() + col : col);
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(Math.max(0, row) + 1);
            write(';');
            print(Math.max(0, col) + 1);
            write('H');
        }
    }

    /**
     * Move the cursor to the given absolute col
     *
     * @param col absolute col to move to
     */
    public void cursorToCol(int col) {
        synchronized (this) {
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(Math.max(0, col) + 1);
            write('G');
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(y);
            write('A');
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(y);
            write('B');
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(x);
            write('C');
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
            write(FIRST_ESC_CHAR);
            write(SECOND_ESC_CHAR);
            print(x);
            write('D');
        }
    }

    /**
     * Move cursor of the given number of rows and cols
     *
     * @param x number of cols
     * @param y number of rows
     */
    public void moveCursor(int x, int y) {
        synchronized (this) {
            moveCursorRight(x);
            moveCursorDown(y);
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
            if (b == '\n' && (t = outer.translationStack.peekLast()) != null && t.col != -1)
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
                if (b[i] == '\n' && (t = outer.translationStack.peekLast()) != null && t.col != -1) {
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
