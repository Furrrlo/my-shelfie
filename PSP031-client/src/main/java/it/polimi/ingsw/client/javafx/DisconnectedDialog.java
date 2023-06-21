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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

class DisconnectedDialog extends DialogVbox {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectedDialog.class);

    public DisconnectedDialog(FxResourcesLoader resources,
                              ExecutorService threadPool,
                              Stage stage,
                              ClientNetManager closedNetManager) {
        super("Connection Lost",
                Color.LIGHTGRAY, Color.INDIANRED,
                "The connection with the server was lost.\nIf you reconnect " +
                        "you might be able to rejoin the previous game.");

        HBox errorHbox = new HBox();
        errorHbox.setSpacing(15);
        errorHbox.prefWidthProperty().bind(widthProperty());
        errorHbox.setAlignment(Pos.CENTER_RIGHT);

        Label errorLabel = new Label("");
        errorHbox.getChildren().add(errorLabel);
        this.getChildren().add(errorHbox);

        HBox buttonsHbox = new HBox();
        buttonsHbox.setSpacing(15);
        buttonsHbox.prefWidthProperty().bind(widthProperty());
        buttonsHbox.setAlignment(Pos.CENTER_RIGHT);

        BooleanProperty isReConnecting = new SimpleBooleanProperty(this, "isReConnecting", false);

        Button cancel = new DialogButton("Reconnect", Color.LIGHTSEAGREEN);
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

        Button quitGame = new DialogButton("Quit", Color.INDIANRED);
        // The stage has a onHidden handler which will handle the closing
        quitGame.setOnMouseClicked(event -> getScene().getWindow().hide());

        buttonsHbox.getChildren().add(quitGame);
        this.getChildren().add(buttonsHbox);
    }
}
