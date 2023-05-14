package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.*;
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
        var b = new Board(4);
        refillBoardRandom(b);
        var gamePane = new GamePane(new Game(
                0,
                b,
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null,
                false,
                false));

        AnchorPane.setLeftAnchor(gamePane, 0.0D);
        AnchorPane.setRightAnchor(gamePane, 0.0D);
        AnchorPane.setTopAnchor(gamePane, 0.0D);
        AnchorPane.setBottomAnchor(gamePane, 0.0D);

        Scene scene = new Scene(gamePane);

        stage.setTitle("My Shelfie");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setWidth(1000);
        stage.setMinHeight(500);
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
