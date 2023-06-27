package it.polimi.ingsw.client.javafx;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Message dialog asking for confirmation to exit the game
 */
class QuitGameDialog extends DialogVbox {
    private final Button cancel;
    private final Button quitGame;

    public QuitGameDialog() {
        super("Quit Game",
                Color.LIGHTGRAY, Color.INDIANRED,
                "Are you sure you want to quit the game?\nIf you quit the game you will have " +
                        "only 30 seconds to reconnect, otherwise the game will end and will not resume.");

        this.cancel = new DialogButton("Go back", Color.LIGHTSEAGREEN);
        cancel.setOnMouseClicked(e -> this.setVisible(false));

        this.quitGame = new DialogButton("Quit Game", Color.INDIANRED);
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        this.getChildren().add(quitGame);
        this.getChildren().add(cancel);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);

        double border = 5 * scale;
        double width = getWidth() / 2 - 2 * border;
        double text_height = 14 * scale;
        double button_height = 28 * scale;

        Fonts.changeSize(cancel.fontProperty(), text_height);
        Fonts.changeSize(quitGame.fontProperty(), text_height);
        Fonts.changeWeight(quitGame.fontProperty(), FontWeight.BOLD);
        Fonts.changeWeight(cancel.fontProperty(), FontWeight.BOLD);

        this.setBorder(new Border(new BorderStroke(Color.INDIANRED,
                BorderStrokeStyle.SOLID, new CornerRadii(Math.min(10, 10 * (getWidth() / 230d))), BorderWidths.DEFAULT)));

        this.cancel.resizeRelocate(border, getHeight() - button_height - border, width, button_height);
        this.quitGame.resizeRelocate(3 * border + width, getHeight() - button_height - border, getWidth() / 2 - 2 * border,
                button_height);
    }
}
