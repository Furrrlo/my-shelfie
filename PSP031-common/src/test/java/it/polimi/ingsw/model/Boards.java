package it.polimi.ingsw.model;

import it.polimi.ingsw.BoardCoord;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.model.BoardView.BOARD_ROWS;
import static it.polimi.ingsw.model.BoardView.BOARD_COLUMNS;

public class Boards {
    private Boards() {
    }

    @VisibleForTesting
    public static void refillBoardRandom(Board board) {
        List<Color> val = List.of(Color.values());
        Random rand = new Random();
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLUMNS; c++) {
                if (board.isValidTile(r, c)) {
                    Property<@Nullable Tile> tileProp = board.tile(r, c);
                    if (tileProp.get() == null) {
                        tileProp.set(new Tile(val.get(rand.nextInt(Color.values().length))));
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public static void refillBoardBag(Board board, List<Tile> bag) {
        for (int r = 0; r < BOARD_ROWS && bag.size() > 0; r++) {
            for (int c = 0; c < BOARD_COLUMNS && bag.size() > 0; c++) {
                if (board.isValidTile(r, c)) {
                    Property<@Nullable Tile> tileProp = board.tile(r, c);
                    if (tileProp.get() == null) {
                        tileProp.set(bag.remove(new Random().nextInt(bag.size())));
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public static void refillBoardCoord(Board board, List<BoardCoord> positions) {
        for (BoardCoord bc : positions) {
            if (board.isValidTile(bc.row(), bc.col())) {
                Property<@Nullable Tile> tileProp = board.tile(bc.row(), bc.col());
                if (tileProp.get() == null) {
                    tileProp.set(new Tile(Color.PINK));
                }
            }
        }
    }
}
