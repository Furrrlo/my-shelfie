package it.polimi.ingsw.client.javafx;

import com.sun.javafx.collections.ObservableListWrapper;
import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.model.BoardView;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;
import static javafx.beans.binding.BooleanExpression.booleanExpression;

@SuppressWarnings("NotNullFieldNotInitialized")
public class BoardComponent extends AnchorPane {

    //@formatter:off
    @FXML public ImageView bg;
    @FXML public TileComponent t0x3;
    @FXML public TileComponent t0x4;
    @FXML public TileComponent t1x3;
    @FXML public TileComponent t1x4;
    @FXML public TileComponent t1x5;
    @FXML public TileComponent t2x2;
    @FXML public TileComponent t2x3;
    @FXML public TileComponent t2x4;
    @FXML public TileComponent t2x5;
    @FXML public TileComponent t2x6;
    @FXML public TileComponent t3x1;
    @FXML public TileComponent t3x2;
    @FXML public TileComponent t3x3;
    @FXML public TileComponent t3x4;
    @FXML public TileComponent t3x5;
    @FXML public TileComponent t3x6;
    @FXML public TileComponent t3x7;
    @FXML public TileComponent t3x8;
    @FXML public TileComponent t4x0;
    @FXML public TileComponent t4x1;
    @FXML public TileComponent t4x2;
    @FXML public TileComponent t4x3;
    @FXML public TileComponent t4x4;
    @FXML public TileComponent t4x5;
    @FXML public TileComponent t4x6;
    @FXML public TileComponent t4x7;
    @FXML public TileComponent t4x8;
    @FXML public TileComponent t5x0;
    @FXML public TileComponent t5x1;
    @FXML public TileComponent t5x2;
    @FXML public TileComponent t5x3;
    @FXML public TileComponent t5x4;
    @FXML public TileComponent t5x5;
    @FXML public TileComponent t5x6;
    @FXML public TileComponent t5x7;
    @FXML public TileComponent t6x2;
    @FXML public TileComponent t6x3;
    @FXML public TileComponent t6x4;
    @FXML public TileComponent t6x5;
    @FXML public TileComponent t6x6;
    @FXML public TileComponent t7x3;
    @FXML public TileComponent t7x4;
    @FXML public TileComponent t7x5;
    @FXML public TileComponent t8x4;
    @FXML public TileComponent t8x5;
    //@formatter:on

    private final ListProperty<BoardCoord> pickedTiles = new SimpleListProperty<>(
            new ObservableListWrapper<>(new ArrayList<>()));
    private final @Nullable TileComponent[][] matrix;

    public BoardComponent(BoardView board) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("board.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        matrix = createMatrix();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                TileComponent tileComponent = Objects.requireNonNull(matrix)[r][c]; // NullAway requires this :I
                if (tileComponent == null || !board.isValidTile(r, c))
                    continue;

                // Bind to the model tile
                var boardCoords = new BoardCoord(r, c);
                tileComponent.tileProperty().bind(FxProperties.toFxProperty("t" + r + "x" + c, this, board.tile(r, c)));
                var nonPickable = compositeObservableValue(tileComponent.tileProperty(), pickedTiles).map(ignored -> {
                    // If you already picked it, you can re-pick it to remove it
                    if (pickedTiles.contains(boardCoords))
                        return false;

                    final var list = new ArrayList<>(pickedTiles);
                    list.add(boardCoords);
                    return !board.checkBoardCoord(list);
                });
                var isPickedExpr = booleanExpression(pickedTiles.map(tiles -> tiles.contains(boardCoords)));
                tileComponent.highlightProperty().bind(tileComponent.disabledProperty().not()
                        .and(tileComponent.armedProperty()
                                .or(tileComponent.hoverProperty())
                                .or(isPickedExpr)));
                tileComponent.highlightColorProperty().bind(
                        compositeObservableValue(tileComponent.armedProperty(), tileComponent.hoverProperty(), isPickedExpr)
                                .map(ignored -> isPickedExpr.get() && tileComponent.isArmed()
                                        ? Color.GREEN.darker()
                                        : isPickedExpr.get()
                                                ? Color.GREEN
                                                : tileComponent.isArmed() ? Color.WHITE.darker() : Color.WHITE));
                tileComponent.disableProperty().bind(nonPickable);
                tileComponent.overlayProperty().bind(nonPickable);
                tileComponent.setOnMouseClicked(e -> {
                    if (!pickedTiles.contains(boardCoords))
                        pickedTiles.add(boardCoords);
                    else
                        pickedTiles.remove(boardCoords);
                });
            }
        }

        bg.fitWidthProperty().bind(widthProperty());
        bg.fitHeightProperty().bind(heightProperty());

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            bg.clipProperty().bind(bg.layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 20 * Math.min(bounds.getWidth() / 460d, bounds.getHeight() / 460d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }
    }

    @SuppressWarnings("NullAway") // NullAway doesn't support array, see https://github.com/uber/NullAway/labels/jspecify
    private @Nullable TileComponent[][] createMatrix() {
        return new TileComponent[][] {
                new TileComponent[] { null, null, null, t0x3, t0x4, null, null, null, null },
                new TileComponent[] { null, null, null, t1x3, t1x4, t1x5, null, null, null },
                new TileComponent[] { null, null, t2x2, t2x3, t2x4, t2x5, t2x6, null, null },
                new TileComponent[] { null, t3x1, t3x2, t3x3, t3x4, t3x5, t3x6, t3x7, t3x8 },
                new TileComponent[] { t4x0, t4x1, t4x2, t4x3, t4x4, t4x5, t4x6, t4x7, t4x8 },
                new TileComponent[] { t5x0, t5x1, t5x2, t5x3, t5x4, t5x5, t5x6, t5x7, null },
                new TileComponent[] { null, null, t6x2, t6x3, t6x4, t6x5, t6x6, null, null },
                new TileComponent[] { null, null, null, t7x3, t7x4, t7x5, null, null, null },
                new TileComponent[] { null, null, null, null, t8x4, t8x5, null, null, null },
        };
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void layoutChildren() {
        super.layoutChildren();

        final double _widthScale = getWidth() / 2965d;
        final double _heightScale = getHeight() / 2965d;
        final double scale = Math.min(_widthScale, _heightScale);

        final double widthScale = scale;
        final double heightScale = scale;
        t0x3.resizeRelocate(1039.0 * widthScale, 152.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t0x4.resizeRelocate(1338.0 * widthScale, 152.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t1x3.resizeRelocate(1039.0 * widthScale, 452.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t1x4.resizeRelocate(1338.0 * widthScale, 452.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t1x5.resizeRelocate(1636.0 * widthScale, 452.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t2x2.resizeRelocate(739.0 * widthScale, 752.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t2x3.resizeRelocate(1039.0 * widthScale, 752.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t2x4.resizeRelocate(1338.0 * widthScale, 752.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t2x5.resizeRelocate(1636.0 * widthScale, 752.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t2x6.resizeRelocate(1936.0 * widthScale, 752.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x1.resizeRelocate(439.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x2.resizeRelocate(739.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x3.resizeRelocate(1039.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x4.resizeRelocate(1338.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x5.resizeRelocate(1636.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x6.resizeRelocate(1936.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x7.resizeRelocate(2235.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t3x8.resizeRelocate(2536.0 * widthScale, 1053.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x0.resizeRelocate(139.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x1.resizeRelocate(439.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x2.resizeRelocate(739.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x3.resizeRelocate(1039.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x4.resizeRelocate(1338.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x5.resizeRelocate(1636.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x6.resizeRelocate(1936.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x7.resizeRelocate(2235.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t4x8.resizeRelocate(2536.0 * widthScale, 1351.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x0.resizeRelocate(139.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x1.resizeRelocate(439.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x2.resizeRelocate(739.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x3.resizeRelocate(1039.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x4.resizeRelocate(1338.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x5.resizeRelocate(1636.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x6.resizeRelocate(1936.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t5x7.resizeRelocate(2235.0 * widthScale, 1650.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t6x2.resizeRelocate(739.0 * widthScale, 1949.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t6x3.resizeRelocate(1039.0 * widthScale, 1949.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t6x4.resizeRelocate(1338.0 * widthScale, 1949.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t6x5.resizeRelocate(1636.0 * widthScale, 1949.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t6x6.resizeRelocate(1936.0 * widthScale, 1949.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t7x3.resizeRelocate(1039.0 * widthScale, 2249.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t7x4.resizeRelocate(1338.0 * widthScale, 2249.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t7x5.resizeRelocate(1636.0 * widthScale, 2249.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t8x4.resizeRelocate(1338.0 * widthScale, 2550.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
        t8x5.resizeRelocate(1636.0 * widthScale, 2550.0 * heightScale, 263.0 * widthScale, 263.0 * heightScale);
    }

    public ListProperty<BoardCoord> pickedTilesProperty() {
        return pickedTiles;
    }
}
