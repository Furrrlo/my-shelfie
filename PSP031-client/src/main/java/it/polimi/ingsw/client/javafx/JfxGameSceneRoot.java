package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.utils.ThreadPools;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

public class JfxGameSceneRoot extends CenteringFitPane {

    public JfxGameSceneRoot(FxResourcesLoader resources,
                            ExecutorService threadPool,
                            Stage stage,
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

        threadPool.submit(ThreadPools.giveNameToTask("jfx-load-gamepane-thread", () -> {
            var gamePane = new GamePane(resources, threadPool, stage, game, controller, netManager);
            Platform.runLater(() -> getChildren().setAll(gamePane));
        }));
    }
}
