package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class EndGamePane extends StackPane {
    private final ImageView teamPicture;
    private final VBox rankings;
    private final Button newGame;
    private final Button quit;

    public EndGamePane(List<? extends PlayerView> sortedPlayers) {
        this.teamPicture = new ImageView(new Image(FxResources.getResourceAsStream("fa/teamPicture.png")));
        teamPicture.setPreserveRatio(true);
        teamPicture.fitWidthProperty().bind(widthProperty());
        teamPicture.fitHeightProperty().bind(heightProperty());
        this.getChildren().add(teamPicture);

        this.rankings = new VBox();
        rankings.setSpacing(5);
        for (PlayerView p : sortedPlayers) {
            HBox hBox = new HBox();
            Label nick = new Label(p.getNick());
            Label score = new Label(p.score().get().toString());
            hBox.getChildren().add(nick);
            hBox.getChildren().add(score);
            rankings.getChildren().add(hBox);
        }
        HBox hBox = new HBox();
        hBox.setSpacing(40);
        this.newGame = new Button("Start new game");
        this.quit = new Button("Quit");
        hBox.getChildren().add(newGame);
        hBox.getChildren().add(quit);

        rankings.setAlignment(Pos.BOTTOM_RIGHT);
        rankings.getChildren().add(hBox);
        this.getChildren().add(rankings);
    }

    //@Override
    //protected void layoutChildren() {
    //    super.layoutChildren();
    //    final double scale = Math.min(getWidth() / 1515, getHeight() / 1080d);
    //    final double border = 200 * scale;
    //    final double width = 1400d * scale;
    //    final double height = 400 * scale;
    //
    //    this.teamPicture.resizeRelocate(border * scale, 0, 1500, 1080);
    //    this.rankings.resizeRelocate(border * scale, 650 * scale, width * scale, height * scale);
    //    this.newGame.resizeRelocate(getWidth() - border * scale, 660 * scale, 300 * scale, 70 * scale);
    //    this.quit.resizeRelocate(border * scale, 660 * scale, 300 * scale, 70 * scale);
    //}
}
