package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Button to quit the game
 */
class QuitGameButton extends InGameButton {

    public QuitGameButton() {
        super("Quit game", Color.INDIANRED);

        backgroundRadiusProperty().bind(widthProperty()
                .map(w -> new CornerRadii(Math.min(10, 10 * (w.doubleValue() / 210d)))));
        setBackgroundInsets(new Insets(0));

        Fonts.changeWeight(fontProperty(), FontWeight.BOLD);
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);
        Fonts.changeSize(fontProperty(), 14 * scale);

        super.layoutChildren();
    }
}
