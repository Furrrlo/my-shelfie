package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DialogVbox extends VBox {

    public static int NOT_CURRENT_TURN = 1;
    public static int DISCONNECTED = 2;

    public DialogVbox(int type) {
        setSpacing(10);
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        String errorTitle = "";
        switch (type) {
            case 1 -> errorTitle = "Player has disconnected";
            case 2 -> errorTitle = "Not your turn";
            default -> errorTitle = "Error";
        }
        Label errorName = new Label("Error : " + errorTitle);
        errorName.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize()));
        errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.RED,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        errorName.setAlignment(Pos.CENTER);
        this.getChildren().add(errorName);

        String errorMessage = "";
        switch (type) {
            case 1 -> errorMessage = "One or more player have disconnected, the game will resume only once all the " +
                    "disconnected players have rejoined the game, or more than 30 second have passed";
            case 2 -> errorMessage = "It's not your current turn, please wait when is your turn before you can " +
                    "select tiles from board again";
            default -> errorMessage = "Error";
        }
        Label dialogue = new Label(errorMessage);
        dialogue.setWrapText(true);
        dialogue.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        this.getChildren().add(dialogue);

        Button ok = new Button("ok");
        ok.setAlignment(Pos.BOTTOM_RIGHT);
        ok.setBackground(Background.fill(Color.LIGHTSEAGREEN));
        ok.setOnMouseClicked(e -> this.setVisible(false));
        this.getChildren().add(ok);
    }
}
