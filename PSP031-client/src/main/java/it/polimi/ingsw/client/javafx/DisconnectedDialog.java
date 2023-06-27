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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

class DisconnectedDialog extends DialogVbox {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectedDialog.class);

    private final Label errorLabel;

    public DisconnectedDialog(FxResourcesLoader resources,
                              ExecutorService threadPool,
                              Stage stage,
                              ClientNetManager closedNetManager) {
        super("Connection Lost",
                Color.LIGHTGRAY, Color.INDIANRED, Color.INDIANRED,
                "The connection with the server was lost.\nIf you reconnect " +
                        "you might be able to rejoin the previous game.");

        HBox errorHbox = new HBox();
        errorHbox.prefWidthProperty().bind(widthProperty());
        errorHbox.setAlignment(Pos.CENTER);

        errorLabel = new Label("");
        errorLabel.setTextFill(Color.INDIANRED);
        errorHbox.getChildren().add(errorLabel);
        this.getChildren().add(errorHbox);

        HBox buttonsHbox = new HBox();
        buttonsHbox.spacingProperty().bind(borderSize());
        buttonsHbox.prefWidthProperty().bind(widthProperty());
        buttonsHbox.setAlignment(Pos.CENTER_RIGHT);

        BooleanProperty isReConnecting = new SimpleBooleanProperty(this, "isReConnecting", false);

        Button cancel = new DialogButton("Reconnect", Color.LIGHTSEAGREEN);
        cancel.prefWidthProperty().bind(widthProperty());
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
        HBox.setHgrow(cancel, Priority.SOMETIMES);

        Button quitGame = new DialogButton("Quit", Color.INDIANRED);
        quitGame.prefWidthProperty().bind(widthProperty());
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        buttonsHbox.getChildren().add(quitGame);
        HBox.setHgrow(quitGame, Priority.SOMETIMES);

        this.getChildren().add(buttonsHbox);
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);
        Fonts.changeSize(errorLabel.fontProperty(), 12 * scale);

        super.layoutChildren();
    }
}
