package it.polimi.ingsw.client.tui;

/**
 * Size of an object in the console
 *
 * @param rows number of rows
 * @param cols number of columns
 */
record TuiSize(int rows, int cols) {

    public TuiSize expand(int rows, int cols) {
        return new TuiSize(this.rows + rows, this.cols + cols);
    }

    public TuiSize reduce(int rows, int cols) {
        return expand(-rows, -cols);
    }
}
