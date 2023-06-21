package it.polimi.ingsw.client.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Message dialog warning you that it is not your turn
 */
class NotCurrentTurnDialog extends DialogVbox {

    public NotCurrentTurnDialog() {
        super("Not your turn",
                Color.LIGHTGRAY, Color.LIGHTSEAGREEN,
                "It's not your current turn, please wait when is your turn before you can " +
                        "select tiles from board again");

        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(widthProperty());

        Button ok = new DialogButton("ok", Color.LIGHTSEAGREEN);
        ok.setOnMouseClicked(e -> this.setVisible(false));

        hbox.getChildren().add(ok);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        this.getChildren().add(hbox);
    }
}
