package it.polimi.ingsw.client.tui;

record TuiSize(int rows, int cols) {

    public TuiSize expand(int rows, int cols) {
        return new TuiSize(this.rows + rows, this.cols + cols);
    }
}
