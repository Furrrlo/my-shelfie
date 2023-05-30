package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.List;

public class EndGamePane extends StackPane {
    /**
     * set to appear when the Game ends ( either due to forced conditions or regular play ) it shows the list of
     * players sorted upon their individual scoring, and giving the player to option :
     * 1 --> by pressing the quit button, it closes the stage and the application is terminated
     * 2 --> by pressing the new game button, the game is closed and the player is brought to a new main Menu scene
     */
    private final AnchorPane scoringBackground;
    private final VBox rankings;
    private final Button newGame;
    private final Button quit;

    public EndGamePane(List<? extends PlayerView> sortedPlayers) {
        var teamPicture = new ImageView(new Image(FxResources.getResourceAsStream("fa/teamPicture.png")));
        teamPicture.setPreserveRatio(true);
        teamPicture.fitWidthProperty().bind(widthProperty());
        teamPicture.fitHeightProperty().bind(heightProperty());
        this.getChildren().add(teamPicture);

        this.scoringBackground = new AnchorPane();
        scoringBackground.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / (getWidth() - 400)))),
                new Insets(0)))));
        this.getChildren().add(scoringBackground);

        this.rankings = new VBox();
        rankings.setSpacing(12);
        for (int i = sortedPlayers.size(); i > 0; i--) {
            var p = sortedPlayers.get(i - 1);
            HBox hBox = new HBox();
            hBox.setSpacing(15);
            Label nick = new Label(p.getNick());
            Label score = new Label(p.score().get().toString());
            hBox.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-2)))));
            hBox.getChildren().add(nick);
            hBox.getChildren().add(score);
            hBox.setAlignment(Pos.CENTER);
            rankings.getChildren().add(hBox);
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

        quit.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });
        newGame.setOnMouseClicked(event -> {
            //closes the existing my shelfie window and opens a new one
            Stage stage = (Stage) getScene().getWindow();
            stage.close();

            //opens a new Stage
            Scene scene = new JfxMainMenuScene(stage);

            stage.setTitle("My Shelfie");

            // Let jfx pick the best fit
            stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Icon 50x50px.png")));
            stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Box 280x280px.png")));

            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setWidth(800);
            stage.setMinHeight(500);
            stage.setHeight(500);
            stage.show();

        });
        this.getChildren().add(newGame);
        this.getChildren().add(quit);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double scale = Math.min(getWidth() / 1515, getHeight() / 1080);
        scoringBackground.resizeRelocate(200 * scale, 650 * scale, getWidth() - 400 * scale, getHeight() - 800 * scale);
        rankings.resizeRelocate((200 + 100) * scale, (650 + 20) * scale, getWidth() - (400 + 200) * scale,
                getHeight() - (800 + 40) * scale);
        newGame.resizeRelocate(getWidth() - (200 + 350) * scale, 970 * scale, 350 * scale, 70 * scale);
        quit.resizeRelocate(200 * scale, 970 * scale, 250 * scale, 70 * scale);
    }
}
