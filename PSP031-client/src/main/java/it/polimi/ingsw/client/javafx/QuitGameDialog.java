package it.polimi.ingsw.client.javafx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

class QuitGameDialog extends DialogVbox {

    public QuitGameDialog() {
        super("Quit Game",
                Color.LIGHTGRAY, Color.INDIANRED,
                "Are you sure you want to quit the game?\nIf you quit the game you will have " +
                        "only 30 seconds to reconnect, otherwise the game will end and will not resume.");

        HBox hbox = new HBox();
        hbox.setSpacing(15);
        hbox.prefWidthProperty().bind(widthProperty());
        hbox.setAlignment(Pos.CENTER_RIGHT);

        Button cancel = new DialogButton("Go back", Color.LIGHTSEAGREEN);
        cancel.setOnMouseClicked(e -> {
            this.setVisible(false);
            for (Node n : this.getParent().getChildrenUnmodifiable()) {
                n.setDisable(false);
                n.setOpacity(1);
            }
        });
        hbox.getChildren().add(cancel);

        Button quitGame = new DialogButton("Quit Game", Color.INDIANRED);
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        hbox.getChildren().add(quitGame);
        this.getChildren().add(hbox);
    }
}
