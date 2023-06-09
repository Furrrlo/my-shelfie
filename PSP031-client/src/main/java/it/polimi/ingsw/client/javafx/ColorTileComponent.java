package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;

public class ColorTileComponent extends AnchorPane {

    public ColorTileComponent(@Nullable Tile tile) {
        if (tile != null) {
            switch (tile.getColor()) {
                case PINK -> setBackground(Background.fill(javafx.scene.paint.Color.PINK));
                case GREEN -> setBackground(Background.fill(javafx.scene.paint.Color.GREEN));
                case YELLOW -> setBackground(Background.fill(javafx.scene.paint.Color.YELLOW));
                case BLUE -> setBackground(Background.fill(javafx.scene.paint.Color.BLUE));
                case WHITE -> setBackground(Background.fill(javafx.scene.paint.Color.WHITE));
                case LIGHTBLUE -> setBackground(Background.fill(javafx.scene.paint.Color.LIGHTBLUE));
            }
        } else
            setBackground(Background.EMPTY);

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 10 * Math.min(bounds.getWidth() / 45d, bounds.getHeight() / 45d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }
    }
}
