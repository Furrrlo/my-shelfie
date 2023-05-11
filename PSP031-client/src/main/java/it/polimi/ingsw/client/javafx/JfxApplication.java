package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.model.BoardView.BOARD_COLUMNS;
import static it.polimi.ingsw.model.BoardView.BOARD_ROWS;

public class JfxApplication extends Application {

    @Override
    public void start(Stage stage) {

        //        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        var pane = new AnchorPane();
        var b = new Board(4);
        refillBoardRandom(b);
        var board = new BoardComponent(b);
        pane.getChildren().add(board);
        AnchorPane.setLeftAnchor(board, 0.0D);
        AnchorPane.setRightAnchor(board, 0.0D);
        AnchorPane.setTopAnchor(board, 0.0D);
        AnchorPane.setBottomAnchor(board, 0.0D);

        Scene scene = new Scene(pane);

        stage.setTitle("My Shelfie");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.show();
    }

    // TODO: remove
    public static void refillBoardRandom(Board board) {
        List<Color> val = List.of(Color.values());
        Random rand = new Random();
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLUMNS; c++) {
                if (board.isValidTile(r, c)) {
                    Property<@Nullable Tile> tileProp = board.tile(r, c);
                    if (tileProp.get() == null) {
                        tileProp.set(new Tile(
                                val.get(rand.nextInt(Color.values().length)),
                                rand.nextInt(3)));
                    }
                }
            }
        }
    }
}
