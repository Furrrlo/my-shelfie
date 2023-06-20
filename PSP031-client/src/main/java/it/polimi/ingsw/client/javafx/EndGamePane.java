package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.PlayerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class EndGamePane extends Pane {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndGamePane.class);

    private final ImageView teamPicture;
    /**
     * set to appear when the Game ends ( either due to forced conditions or regular play ) it shows the list of
     * players sorted upon their individual scoring, and giving the player two options :
     * 1 --> by pressing the quit button, it closes the stage and the application is terminated
     * 2 --> by pressing the new game button, the game is closed and the player is brought to a new main Menu scene
     */
    private final VBox rankings;
    private final Button newGame;
    private final Button quit;

    public EndGamePane(FxResourcesLoader resources,
                       ExecutorService threadPool,
                       Stage stage,
                       List<? extends PlayerView> sortedPlayers,
                       ClientNetManager netManager) {

        teamPicture = new ImageView(resources.loadImage("fa/teamPicture.png"));
        teamPicture.setPreserveRatio(true);
        teamPicture.fitWidthProperty().bind(widthProperty());
        teamPicture.fitHeightProperty().bind(heightProperty());
        this.getChildren().add(teamPicture);

        this.rankings = new VBox(15);
        rankings.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / (getWidth() - 400)))),
                new Insets(0)))));

        for (int i = 0; i < sortedPlayers.size(); i++) {
            var p = sortedPlayers.get(i);
            var row = new RankingRow(resources, p, i + 1);
            VBox.setVgrow(row, Priority.ALWAYS);
            rankings.getChildren().add(row);
        }
        rankings.setAlignment(Pos.CENTER);
        getChildren().add(rankings);

        this.newGame = new Button("Start new game");
        newGame.setTextAlignment(TextAlignment.CENTER);
        newGame.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 350))),
                new Insets(0)))));
        this.quit = new Button("Quit");
        quit.setTextAlignment(TextAlignment.CENTER);
        quit.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.INDIANRED,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 350))),
                new Insets(0)))));

        // The stage has a onHidden handler which will handle the closing
        BooleanProperty isRejoiningAGame = new SimpleBooleanProperty(this, "isJoiningAGame");
        quit.setOnMouseClicked(event -> getScene().getWindow().hide());
        newGame.setOnMouseClicked(event -> {
            isRejoiningAGame.set(true);

            threadPool.execute(() -> {
                try {
                    var lobbyAndController = netManager.joinGame();
                    var sceneRoot = new JfxLobbySceneRoot(resources, threadPool, stage, lobbyAndController, netManager);
                    Platform.runLater(() -> stage.getScene().setRoot(sceneRoot));
                } catch (Exception e) {
                    LOGGER.error("Failed to join a new game", e);
                    // TODO: reconnect
                    throw new RuntimeException(e);
                } finally {
                    Platform.runLater(() -> isRejoiningAGame.set(false));
                }
            });
        });
        newGame.disableProperty().bind(isRejoiningAGame);
        quit.disableProperty().bind(isRejoiningAGame);

        this.getChildren().add(newGame);
        this.getChildren().add(quit);
    }

    @Override
    protected void layoutChildren() {
        final double scale = Math.min(getWidth() / 1515, getHeight() / 1080);

        teamPicture.autosize();
        teamPicture.relocate(0, 0);

        rankings.resizeRelocate(200 * scale, 650 * scale, getWidth() - 400 * scale, getHeight() - 800 * scale);
        rankings.setPadding(new Insets(30 * scale, 50 * scale, 30 * scale, 50 * scale));
        newGame.resizeRelocate(getWidth() - (200 + 350) * scale, 970 * scale, 350 * scale, 70 * scale);
        quit.resizeRelocate(200 * scale, 970 * scale, 250 * scale, 70 * scale);
    }

    private static class RankingRow extends HBox {
        public RankingRow(FxResourcesLoader resources, PlayerView player, int pos) {
            setPadding(new Insets(0, 5, 0, 5));
            setMaxHeight(50);
            setMinHeight(0);
            Label nick = new Label(pos + ". " + player.getNick());
            Label score = new Label(player.score().get().toString());

            DoubleBinding scale = heightProperty().divide(nick.heightProperty());

            nick.scaleXProperty().bind(scale);
            nick.scaleYProperty().bind(scale);
            nick.translateXProperty()
                    .bind(nick.widthProperty().multiply(scale).subtract(nick.widthProperty()).divide(2));

            Region padding = new Region();
            padding.prefWidthProperty().bind(widthProperty().divide(2).subtract(nick.widthProperty()));

            score.scaleXProperty().bind(scale);
            score.scaleYProperty().bind(scale);

            if (!player.connected().get()) {
                nick.setTextFill(Color.RED);
                score.setTextFill(Color.RED);
            }

            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-2)))));
            getChildren().add(nick);
            getChildren().add(padding);
            getChildren().add(score);

            if (pos == 1) {
                var imgView = new ImageView(resources.loadImage("fa/trophy.png"));
                imgView.setPreserveRatio(true);
                imgView.fitHeightProperty().bind(score.heightProperty());
                imgView.scaleXProperty().bind(scale);
                imgView.scaleYProperty().bind(scale);

                imgView.translateXProperty()
                        .bind(imgView.fitHeightProperty().multiply(scale)
                                .subtract(imgView.fitHeightProperty()));
                getChildren().add(imgView);
            }

            setAlignment(Pos.CENTER_LEFT);
        }

    }
}
