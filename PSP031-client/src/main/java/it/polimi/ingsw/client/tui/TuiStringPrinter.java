package it.polimi.ingsw.client.tui;

public class TuiStringPrinter implements TuiPrinter {
    private final String string;
    private final TuiSize size;

    /**
     * Creates a printer that can print strings.
     * If the string cannot fit one line, it will go on new line
     * 
     * @param string string to print
     */
    public TuiStringPrinter(String string) {
        this(string, string.length());
    }

    /**
     * Creates a printer that can print strings on one line.
     * If the string is longer than maxCols, it will be truncated
     *
     * @param string string to print
     * @param maxCols max num of columns that can be used
     */
    public TuiStringPrinter(String string, int maxCols) {
        this(string, new TuiSize(1, Math.min(maxCols, string.length())));
    }

    /**
     * Creates a printer that can print a string,
     * going on a new line if necessary to fit the given size
     *
     * @param string string to print
     * @param size size
     */
    public TuiStringPrinter(String string, TuiSize size) {
        this.string = string;
        this.size = size;
    }

    @Override
    public void print(TuiPrintStream out) {
        String[] split = string.split("[ \n]");
        StringBuilder stringBuilder = new StringBuilder();
        final int maxCharacters = size.cols() * size.rows();
        int col = 0, row = 0;
        for (String s : split) {
            if (col + s.length() < size.cols()) {
                //There is enough space on this line: add a space if it isn't the first word
                if (col != 0) {
                    stringBuilder.append(' ');
                    col++;
                }
            } else if (row + 1 < size.rows()) {
                //There is no enough space on this line, go on new line
                stringBuilder.append('\n');
                row++;
                col = 0;
            } else {
                //there is no enough space on this line, and we have no more lines.
                if (maxCharacters - ((col + 1) * (row + 1)) >= 3) {
                    //We have at least 3 free spaces
                    stringBuilder.append("...");
                } else {
                    //There is no space for 3 character: replace the last ones.
                    stringBuilder.replace(maxCharacters - 3, maxCharacters, "...");
                }
                break;
            }
            stringBuilder.append(s);
            col += s.length();
        }
        out.print(stringBuilder);
    }

    @Override
    public TuiSize getSize() {
        return size;
    }
}
