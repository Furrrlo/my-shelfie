package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;

public class JfxGameSceneRoot extends CenteringFitPane {

    public JfxGameSceneRoot(FxResourcesLoader resources,
                            GameView game,
                            GameController controller,
                            ClientNetManager netManager) {

        setStyle(getStyle() + "-fx-font-family: \"Inter Regular\";");
        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        getChildren().setAll(new ProgressIndicator());

        new Thread(() -> {
            var gamePane = new GamePane(resources, game, controller, netManager);
            Platform.runLater(() -> getChildren().setAll(gamePane));
        }).start();
    }
}
