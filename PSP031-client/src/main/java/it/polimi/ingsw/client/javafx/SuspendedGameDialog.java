package it.polimi.ingsw.client.javafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

class SuspendedGameDialog extends DialogVbox {

    private final Timeline timeline;

    public SuspendedGameDialog() {
        super("Player has disconnected",
                Color.LIGHTGRAY, Color.LIGHTSEAGREEN,
                "The game is suspended because all other players have disconnected.\n" +
                        "If no one reconnects within 30 seconds, the game will end");

        ProgressBar progress = new ProgressBar();
        AnchorPane progressPane = new AnchorPane(progress);

        this.timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
                new KeyFrame(Duration.minutes(0.5), e -> {
                    System.out.println("Minute over");
                }, new KeyValue(progress.progressProperty(), 1)));
        timeline.setCycleCount(Animation.INDEFINITE);

        // Stop it if the pane becomes invisible or disabled
        disabledProperty().addListener((obs, old, newV) -> {
            if (newV)
                timeline.stop();
        });
        visibleProperty().addListener((obs, old, newV) -> {
            if (!newV)
                timeline.stop();
        });

        progress.prefWidthProperty().bind(widthProperty());
        progressPane.prefWidthProperty().bind(widthProperty());
        progressPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        this.getChildren().add(progressPane);
    }

    public void play() {
        this.timeline.playFromStart();
    }
}
