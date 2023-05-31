package it.polimi.ingsw.client.javafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class DialogVbox extends VBox {

    public static int QUIT_GAME = 0;
    public static int NOT_CURRENT_TURN = 1;
    public static int DISCONNECTED = 2;

    private final Timeline timeline;

    public DialogVbox(int type) {
        setSpacing(15);
        if (type == NOT_CURRENT_TURN || type == DISCONNECTED) {
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(-10)))));
        } else
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(-10)))));
        String errorTitle = "";
        switch (type) {
            case 2 -> errorTitle = "Player has disconnected";
            case 1 -> errorTitle = "Not your turn";
            case 0 -> errorTitle = "Quit Game";
            default -> errorTitle = "Error";
        }
        Label errorName = new Label("Error : " + errorTitle);
        errorName.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize()));

        if (type == NOT_CURRENT_TURN || type == DISCONNECTED) {
            errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTSEAGREEN,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-5)))));
        } else
            errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.INDIANRED,
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
            case 0 -> errorMessage = "Are you sure you want to quit the game?\nIf you quit the game you will have " +
                    "only 30 seconds to reconnect, otherwise the game will end and will not resume.";
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

        if (type == NOT_CURRENT_TURN) {
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
        if (type == QUIT_GAME) {
            HBox hbox = new HBox();
            hbox.setSpacing(15);
            hbox.prefWidthProperty().bind(widthProperty());
            Button cancel = new Button("Go back");
            cancel.setAlignment(Pos.CENTER_LEFT);
            cancel.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTSEAGREEN,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-5)))));
            hbox.getChildren().add(cancel);
            cancel.setOnMouseClicked(e -> {
                this.setVisible(false);
                for (Node n : this.getParent().getChildrenUnmodifiable()) {
                    n.setDisable(false);
                    n.setOpacity(1);
                }
            });

            Button quitGame = new Button("Quit Game");
            quitGame.setAlignment(Pos.CENTER_RIGHT);
            hbox.getChildren().add(quitGame);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            quitGame.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.INDIANRED,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-5)))));

            //TODO: set close game on quit game press
            this.getChildren().add(hbox);
        }

        ProgressBar progress = new ProgressBar();
        this.timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
                new KeyFrame(Duration.minutes(0.5), e -> {
                    System.out.println("Minute over");
                }, new KeyValue(progress.progressProperty(), 1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        progress.prefWidthProperty().bind(widthProperty());
        if (type == DISCONNECTED) {
            AnchorPane progressPane = new AnchorPane(progress);
            progressPane.prefWidthProperty().bind(widthProperty());
            progressPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTSEAGREEN,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-5)))));
            this.getChildren().add(progressPane);
        }
    }

    public void play() {
        this.timeline.playFromStart();
    }
}
