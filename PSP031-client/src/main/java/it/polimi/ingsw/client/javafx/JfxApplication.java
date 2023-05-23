package it.polimi.ingsw.client.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JfxApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Scene scene = new JfxMainMenuScene(stage);

        stage.setTitle("My Shelfie");

        // Let jfx pick the best fit
        stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Icon 50x50px.png")));
        stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Box 280x280px.png")));

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setWidth(800);
        stage.setMinHeight(500);
        stage.setHeight(500);
        stage.show();
    }
}
