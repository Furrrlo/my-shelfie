package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class JfxGameScene extends Scene {

    public JfxGameScene(FxResourcesLoader resources, Stage stage, GameView game, GameController controller,
                        ClientNetManager netManager) {
        super(createRootNode(resources, stage, game, controller, netManager));
    }

    private static Parent createRootNode(FxResourcesLoader resources,
                                         Stage stage,
                                         GameView game,
                                         GameController controller,
                                         ClientNetManager netManager) {
        stage.setMinWidth(800);
        stage.setWidth(1080);
        stage.setMinHeight(500);
        stage.setHeight(720);

        var mainPane = new CenteringFitPane();
        mainPane.setStyle(mainPane.getStyle() + "-fx-font-family: \"Inter Regular\";");
        mainPane.setPadding(new Insets(10));
        mainPane.setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        mainPane.getChildren().setAll(new ProgressIndicator());

        new Thread(() -> {
            var gamePane = new GamePane(resources, game, controller, netManager);
            Platform.runLater(() -> mainPane.getChildren().setAll(gamePane));
        }).start();

        return mainPane;
    }
}
