package it.polimi.ingsw.client.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Message dialog asking for confirmation to exit the game
 */
class QuitGameDialog extends DialogVbox {
    private final Button cancel;
    private final Button quitGame;

    public QuitGameDialog() {
        super("Quit Game",
                Color.LIGHTGRAY, Color.INDIANRED, Color.INDIANRED,
                "Are you sure you want to quit the game?\nIf you quit the game you will have " +
                        "only 30 seconds to reconnect, otherwise the game will end and will not resume.");

        HBox hbox = new HBox();
        hbox.spacingProperty().bind(borderSize());
        hbox.prefWidthProperty().bind(widthProperty());
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setFillHeight(true);

        this.cancel = new DialogButton("Go back", Color.LIGHTSEAGREEN);
        cancel.prefWidthProperty().bind(widthProperty());
        Fonts.changeWeight(cancel.fontProperty(), FontWeight.BOLD);
        cancel.setOnMouseClicked(e -> this.setVisible(false));

        this.quitGame = new DialogButton("Quit Game", Color.INDIANRED);
        Fonts.changeWeight(quitGame.fontProperty(), FontWeight.BOLD);
        quitGame.prefWidthProperty().bind(widthProperty());
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        hbox.getChildren().add(cancel);
        HBox.setHgrow(cancel, Priority.SOMETIMES);
        hbox.getChildren().add(quitGame);
        HBox.setHgrow(quitGame, Priority.SOMETIMES);
        getChildren().add(hbox);
    }
}
