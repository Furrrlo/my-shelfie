package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class JfxGameScene extends Scene {

    public JfxGameScene(GameView game, GameController controller, ClientNetManager netManager) {
        super(createRootNode(game, controller, netManager));
    }

    private static Parent createRootNode(GameView game, GameController controller, ClientNetManager netManager) {
        var mainPane = new CenteringFitPane();
        mainPane.getChildren().add(new GamePane(game, controller, netManager));
        mainPane.setPadding(new Insets(10));
        mainPane.setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/sfondo parquet.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));
        return mainPane;
    }
}
