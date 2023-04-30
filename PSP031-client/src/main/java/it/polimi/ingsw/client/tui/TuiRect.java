package it.polimi.ingsw.client.tui;

record TuiRect(int row, int col, TuiSize size) {

    static TuiRect fromCoords(int row1, int col1, int row2, int col2) {
        return new TuiRect(row1, col1, new TuiSize(row2 - row1 + 1, col2 - col1 + 1));
    }

    static TuiRect fromSize(int row, int col, int numRows, int numCols) {
        return new TuiRect(row, col, new TuiSize(numRows, numCols));
    }

    public int lastRow() {
        return row + size().rows() - 1;
    }

    public int lastCol() {
        return col + size().cols() - 1;
    }

    public TuiRect expand(int rows, int cols) {
        return new TuiRect(row - rows / 2, col - cols / 2, size.expand(rows, cols));
    }
}
