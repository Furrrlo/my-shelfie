package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Shelfie;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

class JfxMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        //        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        var pane = new AnchorPane();
        var shelfie = new ShelfieComponent(new Shelfie());
        pane.getChildren().add(shelfie);
        AnchorPane.setLeftAnchor(shelfie, 0.0D);
        AnchorPane.setRightAnchor(shelfie, 0.0D);
        AnchorPane.setTopAnchor(shelfie, 0.0D);
        AnchorPane.setBottomAnchor(shelfie, 0.0D);

        Scene scene = new Scene(pane);

        stage.setTitle("My Shelfie");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.show();
    }
}
