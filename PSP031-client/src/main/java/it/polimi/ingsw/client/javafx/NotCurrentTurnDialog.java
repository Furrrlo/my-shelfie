package it.polimi.ingsw.client.javafx;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * Message dialog warning you that it is not your turn
 */
class NotCurrentTurnDialog extends DialogVbox {

    public NotCurrentTurnDialog() {
        super("Not your turn",
                Color.LIGHTGRAY, Color.LIGHTSEAGREEN, Color.AQUAMARINE,
                "It's not your current turn, please wait when is your turn before you can " +
                        "select tiles from board again");

        HBox hbox = new HBox();
        hbox.spacingProperty().bind(borderSize());
        hbox.prefWidthProperty().bind(widthProperty());
        hbox.setFillHeight(true);

        Button ok = new DialogButton("ok", Color.LIGHTSEAGREEN);
        ok.prefWidthProperty().bind(widthProperty());
        ok.setOnMouseClicked(e -> this.setVisible(false));

        var emptyPane = new Pane();
        emptyPane.prefWidthProperty().bind(widthProperty());

        hbox.getChildren().add(emptyPane);
        HBox.setHgrow(emptyPane, Priority.SOMETIMES);
        hbox.getChildren().add(ok);
        HBox.setHgrow(ok, Priority.SOMETIMES);
        this.getChildren().add(hbox);
    }
}
