package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class DialogVbox extends VBox {

    public static int NOT_CURRENT_TURN = 1;
    public static int DISCONNECTED = 2;

    public DialogVbox(int type) {
        setSpacing(15);
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(-10)))));
        String errorTitle = "";
        switch (type) {
            case 2 -> errorTitle = "Player has disconnected";
            case 1 -> errorTitle = "Not your turn";
            default -> errorTitle = "Error";
        }
        Label errorName = new Label("Error : " + errorTitle);
        errorName.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize()));
        errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        errorName.setAlignment(Pos.CENTER);
        errorName.prefWidthProperty().bind(this.widthProperty());
        this.getChildren().add(errorName);

        String errorMessage = "";
        switch (type) {
            case 2 -> errorMessage = "The game is suspended because all other players have disconnected.\n" +
                    "If no one reconnects within 30 seconds, the game will end";
            case 1 -> errorMessage = "It's not your current turn, please wait when is your turn before you can " +
                    "select tiles from board again";
            default -> errorMessage = "Error";
        }
        Label dialogue = new Label(errorMessage);
        dialogue.setWrapText(true);
        dialogue.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        dialogue.setAlignment(Pos.CENTER);
        dialogue.setTextAlignment(TextAlignment.CENTER);
        dialogue.setGraphicTextGap(20);
        this.getChildren().add(dialogue);
        dialogue.prefWidthProperty().bind(widthProperty());
        dialogue.prefHeightProperty().bind(heightProperty());

        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(widthProperty());
        Button ok = new Button("ok");
        ok.setAlignment(Pos.CENTER_RIGHT);
        hbox.getChildren().add(ok);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        ok.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        ok.setOnMouseClicked(e -> this.setVisible(false));
        this.getChildren().add(hbox);
    }
}
