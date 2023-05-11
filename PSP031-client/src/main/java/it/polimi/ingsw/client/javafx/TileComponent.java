package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;

public class TileComponent extends Button {

    /** Used instead of null for mapping the tile property, as otherwise the map method discards null tiles */
    private static final Tile NULL_TILE = new Tile(it.polimi.ingsw.model.Color.YELLOW);

    private final ObjectProperty<@Nullable Tile> tile = new SimpleObjectProperty<>(this, "tile");
    private final BooleanProperty highlight = new SimpleBooleanProperty(this, "highlight");
    private final ObjectProperty<Color> highlightColor = new SimpleObjectProperty<>(this, "highlightColor");
    private final BooleanProperty overlay = new SimpleBooleanProperty(this, "overlay");

    public TileComponent() {
        this(null);
    }

    public TileComponent(@Nullable Tile tile) {
        this.tile.set(tile);
        setPadding(Insets.EMPTY);

        backgroundProperty().bind(tileProperty().orElse(NULL_TILE).map(ignored -> {
            var currTile = this.tile.get();
            Image img = currTile == null ? null : new Image(FxResources.getResourceAsStream(switch (currTile.getColor()) {
                case GREEN -> "assets/item tiles/Gatti1." + (currTile.getPicIndex() + 1) + ".png";
                case WHITE -> "assets/item tiles/Libri1." + (currTile.getPicIndex() + 1) + ".png";
                case YELLOW -> "assets/item tiles/Giochi1." + (currTile.getPicIndex() + 1) + ".png";
                case BLUE -> "assets/item tiles/Cornici1." + (currTile.getPicIndex() + 1) + ".png";
                case LIGHTBLUE -> "assets/item tiles/Trofei1." + (currTile.getPicIndex() + 1) + ".png";
                case PINK -> "assets/item tiles/Piante1." + (currTile.getPicIndex() + 1) + ".png";
            }));

            if (img == null)
                return Background.fill(Color.TRANSPARENT);

            return new Background(new BackgroundImage(
                    img,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    new BackgroundPosition(Side.LEFT, 0, false, Side.TOP, 0, false),
                    new BackgroundSize(100, 100, true, true, false, true)));
        }));

        effectProperty().bind(overlayProperty().map(o -> {
            if (!o)
                return null;

            var colorAdjust = new ColorAdjust(0, 0, -0.3f, 0);
            colorAdjust.setInput(new GaussianBlur(3.5));
            return colorAdjust;
        }));

        highlightProperty().bind(disabledProperty().not().and(armedProperty().or(hoverProperty())));
        highlightColorProperty().bind(compositeObservableValue(armedProperty(), hoverProperty())
                .map(ignored -> isArmed() ? Color.WHITE.darker() : Color.WHITE));

        borderProperty().bind(compositeObservableValue(highlightProperty(), highlightColorProperty())
                .map(ignored -> highlight.get()
                        ? new Border(new BorderStroke(highlightColor.get(), BorderStrokeStyle.SOLID,
                                new CornerRadii(2), new BorderWidths(4)))
                        : Border.EMPTY));

        this.textProperty().set("");
        this.ellipsisStringProperty().set("");
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

    public boolean isHighlight() {
        return highlight.get();
    }

    public BooleanProperty highlightProperty() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight.set(highlight);
    }

    public Color getHighlightColor() {
        return highlightColor.get();
    }

    public ObjectProperty<Color> highlightColorProperty() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor.set(highlightColor);
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
