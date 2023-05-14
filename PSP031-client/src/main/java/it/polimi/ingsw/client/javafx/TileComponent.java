package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class TileComponent extends ImageButton {

    /** Used instead of null for mapping the tile property, as otherwise the map method discards null tiles */
    private static final Tile NULL_TILE = new Tile(it.polimi.ingsw.model.Color.YELLOW);

    private final ObjectProperty<@Nullable Tile> tile = new SimpleObjectProperty<>(this, "tile");
    private final BooleanProperty overlay = new SimpleBooleanProperty(this, "overlay");

    public TileComponent() {
        this(null);
    }

    public TileComponent(@Nullable Tile tile) {
        this.tile.set(tile);

        imageProperty().bind(tileProperty().orElse(NULL_TILE).map(ignored -> {
            var currTile = this.tile.get();
            return currTile == null ? null : new Image(FxResources.getResourceAsStream(switch (currTile.getColor()) {
                case GREEN -> "assets/item tiles/Gatti1." + (currTile.getPicIndex() + 1) + ".png";
                case WHITE -> "assets/item tiles/Libri1." + (currTile.getPicIndex() + 1) + ".png";
                case YELLOW -> "assets/item tiles/Giochi1." + (currTile.getPicIndex() + 1) + ".png";
                case BLUE -> "assets/item tiles/Cornici1." + (currTile.getPicIndex() + 1) + ".png";
                case LIGHTBLUE -> "assets/item tiles/Trofei1." + (currTile.getPicIndex() + 1) + ".png";
                case PINK -> "assets/item tiles/Piante1." + (currTile.getPicIndex() + 1) + ".png";
            }));
        }));

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 10 * Math.min(bounds.getWidth() / 45d, bounds.getHeight() / 45d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }

        effectProperty().bind(overlayProperty().map(o -> {
            if (!o)
                return null;

            var colorAdjust = new ColorAdjust(0, 0, -0.3f, 0);
            colorAdjust.setInput(new GaussianBlur(3.5));
            return colorAdjust;
        }));
    }

    public @Nullable Tile getTile() {
        return tile.get();
    }

    public Property<@Nullable Tile> tileProperty() {
        return tile;
    }

    public void setTile(@Nullable Tile tile) {
        this.tile.set(tile);
    }

    public boolean isOverlay() {
        return overlay.get();
    }

    public BooleanProperty overlayProperty() {
        return overlay;
    }

    public void setOverlay(boolean overlay) {
        this.overlay.set(overlay);
    }
}
