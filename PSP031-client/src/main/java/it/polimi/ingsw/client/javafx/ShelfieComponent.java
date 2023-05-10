package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Shelfie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

@SuppressWarnings("NotNullFieldNotInitialized")
class ShelfieComponent extends AnchorPane {
    //@formatter:off
    @FXML public ImageView bg;
    @FXML public TileComponent t0x0;
    @FXML public TileComponent t0x1;
    @FXML public TileComponent t0x2;
    @FXML public TileComponent t0x3;
    @FXML public TileComponent t0x4;
    @FXML public TileComponent t1x0;
    @FXML public TileComponent t1x1;
    @FXML public TileComponent t1x2;
    @FXML public TileComponent t1x3;
    @FXML public TileComponent t1x4;
    @FXML public TileComponent t2x0;
    @FXML public TileComponent t2x2;
    @FXML public TileComponent t2x1;
    @FXML public TileComponent t2x3;
    @FXML public TileComponent t2x4;
    @FXML public TileComponent t3x0;
    @FXML public TileComponent t3x1;
    @FXML public TileComponent t3x2;
    @FXML public TileComponent t3x3;
    @FXML public TileComponent t3x4;
    @FXML public TileComponent t4x0;
    @FXML public TileComponent t4x1;
    @FXML public TileComponent t4x2;
    @FXML public TileComponent t4x3;
    @FXML public TileComponent t4x4;
    @FXML public TileComponent t5x0;
    @FXML public TileComponent t5x1;
    @FXML public TileComponent t5x2;
    @FXML public TileComponent t5x3;
    @FXML public TileComponent t5x4;
    //@formatter:on

    private final TileComponent[][] matrix;

    public ShelfieComponent(Shelfie shelfie) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("shelfie.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        matrix = new TileComponent[][] {
                new TileComponent[] { t0x0, t0x1, t0x2, t0x3, t0x4 },
                new TileComponent[] { t1x0, t1x1, t1x2, t1x3, t1x4 },
                new TileComponent[] { t2x0, t2x1, t2x2, t2x3, t2x4 },
                new TileComponent[] { t3x0, t3x1, t3x2, t3x3, t3x4 },
                new TileComponent[] { t4x0, t4x1, t4x2, t4x3, t4x4 },
                new TileComponent[] { t5x0, t5x1, t5x2, t5x3, t5x4 },
        };

        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                TileComponent tileComponent = matrix[r][c];
                // Bind to the model tile
                var tileProp = FxProperties.toFxProperty("t" + r + "x" + c, this, shelfie.tile(r, c));
                tileComponent.tileProperty().bind(tileProp);
            }
        }

        bg.fitWidthProperty().bind(widthProperty());
        bg.fitHeightProperty().bind(heightProperty());
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void layoutChildren() {
        super.layoutChildren();

        final double _widthScale = getWidth() / 1218d;
        final double _heightScale = getHeight() / 1235d;
        final double scale = Math.min(_widthScale, _heightScale);

        final double widthScale = scale;
        final double heightScale = scale;
        t0x0.resizeRelocate(146.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x1.resizeRelocate(342.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x2.resizeRelocate(537.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x3.resizeRelocate(732.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x4.resizeRelocate(930.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x0.resizeRelocate(148.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x1.resizeRelocate(342.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x2.resizeRelocate(537.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x3.resizeRelocate(730.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x4.resizeRelocate(928.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x0.resizeRelocate(150.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x1.resizeRelocate(537.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x2.resizeRelocate(342.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x3.resizeRelocate(730.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x4.resizeRelocate(924.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x0.resizeRelocate(154.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x1.resizeRelocate(344.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x2.resizeRelocate(537.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x3.resizeRelocate(730.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x4.resizeRelocate(922.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x0.resizeRelocate(156.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x1.resizeRelocate(346.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x2.resizeRelocate(537.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x3.resizeRelocate(730.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x4.resizeRelocate(915.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x0.resizeRelocate(156.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x1.resizeRelocate(346.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x2.resizeRelocate(537.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x3.resizeRelocate(730.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x4.resizeRelocate(915.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
    }
}
