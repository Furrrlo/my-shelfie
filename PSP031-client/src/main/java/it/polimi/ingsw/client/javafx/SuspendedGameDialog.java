package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.GameView;

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

/**
 * Message dialog warning you that the game is suspended
 */
class SuspendedGameDialog extends DialogVbox {

    private final Timeline timeline;
    private final AnchorPane progressPane;
    private final ProgressBar progress;

    public SuspendedGameDialog() {
        super("Player has disconnected",
                Color.LIGHTGRAY, Color.LIGHTSEAGREEN,
                "The game is suspended because all other players have disconnected.\n" +
                        "If no one reconnects within " +
                        GameView.SUSPENDED_GAME_TIMEOUT.toSeconds() + " seconds, the game will end");

        this.progress = new ProgressBar();
        this.progressPane = new AnchorPane();

        this.timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
                new KeyFrame(
                        Duration.millis(GameView.SUSPENDED_GAME_TIMEOUT.toMillis()),
                        new KeyValue(progress.progressProperty(), 1)));
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

        progressPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        this.getChildren().add(progressPane);
        this.getChildren().add(progress);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);

        double border = 5 * scale;
        double width = getWidth() - 2 * border;
        double button_height = 28 * scale;

        this.progressPane.resizeRelocate(border, getHeight() - button_height - border, width, button_height);
        this.progress.resizeRelocate(2 * border, getHeight() - button_height, width - 2 * border,
                button_height - 2 * border);

    }

    public void play() {
        this.timeline.playFromStart();
    }
}
