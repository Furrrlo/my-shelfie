package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TileAndCoords;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * Base component for local and remote players
 *
 * @see ShelfieComponent
 */
abstract class PlayerShelfieComponent extends Pane {

    protected final NickLabel label;
    protected final ShelfieComponent shelfieComponent;
    protected final Pane chair;

    private final PlayerView player;

    public PlayerShelfieComponent(FxResourcesLoader resources,
                                  PlayerView player,
                                  boolean mouseTransparent,
                                  boolean showScore) {
        this.player = player;
        getChildren().add(this.shelfieComponent = new ShelfieComponent(resources, player.getShelfie()));

        setMouseTransparent(mouseTransparent);

        getChildren().add(this.label = new NickLabel(showScore));

        final var chairImgView = new ImageView(resources.loadImage("assets/misc/firstplayertoken.png"));
        getChildren().add(this.chair = new AnchorPane(chairImgView));
        AnchorPane.setTopAnchor(chairImgView, 0.0);
        AnchorPane.setBottomAnchor(chairImgView, 0.0);
        AnchorPane.setLeftAnchor(chairImgView, 0.0);
        AnchorPane.setRightAnchor(chairImgView, 0.0);
        chairImgView.fitWidthProperty().bind(chair.widthProperty());
        chairImgView.fitHeightProperty().bind(chair.heightProperty());

        this.chair.setVisible(player.isStartingPlayer());
    }

    @Override
    protected void layoutChildren() {
        // By default, we don't do any layout
    }

    public void setTilesLowOpacity() {
        this.shelfieComponent.setLowOpacity();
    }

    public void restoreTilesOpacity() {
        this.shelfieComponent.restoreOpacity();
    }

    public boolean isColumnSelectionMode() {
        return shelfieComponent.isColumnSelectionMode();
    }

    public BooleanProperty columnSelectionModeProperty() {
        return shelfieComponent.columnSelectionModeProperty();
    }

    public void setColumnSelectionMode(boolean columnSelectionMode) {
        shelfieComponent.setColumnSelectionMode(columnSelectionMode);
    }

    public IntPredicate getIsColumnSelectable() {
        return shelfieComponent.getIsColumnSelectable();
    }

    public ObjectProperty<IntPredicate> isColumnSelectableProperty() {
        return shelfieComponent.isColumnSelectableProperty();
    }

    public void setIsColumnSelectable(IntPredicate isColumnSelectable) {
        shelfieComponent.setIsColumnSelectable(isColumnSelectable);
    }

    public @Nullable Consumer<TileAndCoords<@Nullable Tile>> getOnTileAction() {
        return shelfieComponent.getOnTileAction();
    }

    public ObjectProperty<@Nullable Consumer<TileAndCoords<@Nullable Tile>>> onTileActionProperty() {
        return shelfieComponent.onTileActionProperty();
    }

    public void setOnTileAction(@Nullable Consumer<TileAndCoords<@Nullable Tile>> onTileAction) {
        shelfieComponent.setOnTileAction(onTileAction);
    }

    protected class NickLabel extends HBox {

        private final Label label;

        public NickLabel(boolean showScore) {
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(1)))));
            borderProperty().bind(widthProperty().map(width -> new Border(new BorderStroke(
                    Color.rgb(129, 33, 0),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    BorderStroke.THICK))));

            setAlignment(Pos.CENTER);

            final String nick = player.getNick();

            label = new Label();
            if (showScore)
                label.textProperty()
                        .bind(FxProperties.toFxProperty("score", this, player.score()).map(s -> nick + ": " + s + " pt"));
            else
                label.setText(nick);

            label.textFillProperty().bind(FxProperties.toFxProperty("connected", this, player.connected())
                    .map(connected -> connected ? Color.BLACK : Color.RED));

            Consumer<Boolean> obs;
            player.isCurrentTurn().registerWeakObserver(obs = isCurrentTurn -> Fonts.changeWeight(label.fontProperty(),
                    isCurrentTurn ? FontWeight.BOLD : FontWeight.NORMAL));
            obs.accept(player.isCurrentTurn().get());

            getChildren().add(label);
        }

        @Override
        protected void layoutChildren() {
            Fonts.changeSize(label.fontProperty(), 12d * getHeight() / 28d);
            super.layoutChildren();
        }
    }
}
