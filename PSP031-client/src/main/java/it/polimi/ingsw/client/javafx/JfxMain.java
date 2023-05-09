package it.polimi.ingsw.client.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class JfxMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("My Shelfie");
        stage.setScene(scene);
        stage.show();
    }
}
