package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Board;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class JfxApplication extends Application {

    @Override
    public void start(Stage stage) {

        //        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        var pane = new AnchorPane();
        var board = new BoardComponent(new Board(4));
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
}
