package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

class DisconnectedDialog extends DialogVbox {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectedDialog.class);

    private final Button cancel;
    private final Button quitGame;

    private final Label errorLabel;

    public DisconnectedDialog(FxResourcesLoader resources,
                              ExecutorService threadPool,
                              Stage stage,
                              ClientNetManager closedNetManager) {
        super("Connection Lost",
                Color.LIGHTGRAY, Color.INDIANRED,
                "The connection with the server was lost.\nIf you reconnect " +
                        "you might be able to rejoin the previous game.");

        this.errorLabel = new Label("");
        errorLabel.setTextFill(Color.INDIANRED);

        HBox buttonsHbox = new HBox();
        buttonsHbox.setSpacing(15);
        buttonsHbox.prefWidthProperty().bind(widthProperty());
        buttonsHbox.setAlignment(Pos.CENTER_RIGHT);

        BooleanProperty isReConnecting = new SimpleBooleanProperty(this, "isReConnecting", false);

        this.cancel = new DialogButton("Reconnect", Color.LIGHTSEAGREEN);
        cancel.disableProperty().bind(isReConnecting);
        cancel.setOnMouseClicked(e -> {
            isReConnecting.set(true);
            threadPool.execute(() -> {
                try {
                    JfxMainMenuSceneRoot.connectAndJoinGame(
                            resources,
                            threadPool,
                            stage,
                            closedNetManager::recreateAndReconnect);
                } catch (NickNotValidException ex) {
                    Platform.runLater(() -> errorLabel.setText(ex.getMessage()));
                } catch (Throwable ex) {
                    LOGGER.error("Failed to reconnect", ex);
                    Platform.runLater(() -> errorLabel.setText("Failed to reconnect to the server"));
                } finally {
                    Platform.runLater(() -> isReConnecting.set(false));
                }
            });
        });
        buttonsHbox.getChildren().add(cancel);

        this.quitGame = new DialogButton("Quit", Color.INDIANRED);
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        this.getChildren().add(quitGame);
        this.getChildren().add(cancel);
        this.getChildren().add(errorLabel);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);

        double border = 5 * scale;
        double width = getWidth() / 2 - 2 * border;
        double text_height = 14 * scale;
        double error_text_height = 12 * scale;
        double button_height = 28 * scale;

        Fonts.changeSize(cancel.fontProperty(), text_height);
        Fonts.changeSize(quitGame.fontProperty(), text_height);
        Fonts.changeSize(errorLabel.fontProperty(), error_text_height);
        Fonts.changeWeight(quitGame.fontProperty(), FontWeight.BOLD);
        Fonts.changeWeight(errorLabel.fontProperty(), FontWeight.BOLD);

        this.setBorder(new Border(new BorderStroke(Color.INDIANRED,
                BorderStrokeStyle.SOLID, new CornerRadii(Math.min(10, 10 * (getWidth() / 230d))), BorderWidths.DEFAULT)));

        this.cancel.resizeRelocate(border, getHeight() - button_height - border, width, button_height);
        this.quitGame.resizeRelocate(3 * border + width, getHeight() - button_height - border, getWidth() / 2 - 2 * border,
                button_height);
        this.errorLabel.resizeRelocate(2 * border, getHeight() - 4 * text_height, getWidth() - 4 * border, text_height);
    }
}
