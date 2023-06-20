package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class JfxMainMenuSceneRoot extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxMainMenuSceneRoot.class);

    public JfxMainMenuSceneRoot(FxResourcesLoader resources, ExecutorService threadPool, Stage stage) {
        //var mainPane = new CenteringFitPane();
        //mainPane.getChildren().add(new MainMenuPane());
        //Pattern ipPattern = Pattern
        //                .compile(" (\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3} ");

        // Create labels
        Label connectionTypeLabel = new Label("Connection Type:");
        Label ipLabel = new Label("IP:");
        Label portLabel = new Label("Port:");
        Label usernameLabel = new Label("Username:");

        // Create choice dialog for connection type
        ChoiceBox<String> connectionTypeChoice = new ChoiceBox<>();
        connectionTypeChoice.getItems().addAll("RMI", "Socket");

        // Create text fields
        TextField ipTextField = new TextField();
        TextField portTextField = new TextField();
        TextField usernameTextField = new TextField();

        // Set prompt values
        ipTextField.setPromptText("localhost");
        portTextField.setTextFormatter(new TextFormatter<>(
                c -> c.getControlNewText().matches("([1-9][0-9]*)?") ? c : null));
        connectionTypeChoice.setOnAction(e -> portTextField
                .setPromptText(String.valueOf(switch (connectionTypeChoice.getValue().toLowerCase(Locale.ROOT)) {
                    case "rmi" -> Registry.REGISTRY_PORT;
                    case "socket" -> SocketClientNetManager.DEFAULT_PORT;
                    default -> throw new IllegalStateException("Unexpected value: " + connectionTypeChoice.getValue());
                })));
        connectionTypeChoice.getSelectionModel().selectFirst();

        //create tile image
        Image titleImage = new Image(FxResources.getResourceAsStream("assets/Publisher material/Title 2000x618px.png"), 400,
                124, true, false);
        ImageView titleView = new ImageView(titleImage);

        // Create grid pane for layout
        GridPane mainPane = new GridPane();
        mainPane.setHgap(10);
        mainPane.setVgap(10);
        mainPane.setPadding(new Insets(10));

        // Add components to grid pane
        mainPane.add(connectionTypeLabel, 0, 0);
        mainPane.add(connectionTypeChoice, 1, 0);
        mainPane.add(ipLabel, 0, 1);
        mainPane.add(ipTextField, 1, 1);
        mainPane.add(portLabel, 0, 2);
        mainPane.add(portTextField, 1, 2);
        mainPane.add(usernameLabel, 0, 3);
        mainPane.add(usernameTextField, 1, 3);
        mainPane.setAlignment(Pos.CENTER);

        // ip validation
        Label errorLabel = new Label("");
        BooleanProperty isConnecting = new SimpleBooleanProperty(false);
        EventHandler<ActionEvent> eventIpCHeck = e -> {
            String host = ipTextField.getText().isBlank()
                    ? ipTextField.getPromptText().strip()
                    : ipTextField.getText().strip();
            int port = Integer.parseInt(portTextField.getText().isBlank()
                    ? portTextField.getPromptText().strip()
                    : portTextField.getText().strip());
            String username = usernameTextField.getText();

            isConnecting.set(true);
            threadPool.execute(() -> {
                ClientNetManager netManager = null;
                try {
                    netManager = switch (connectionTypeChoice.getValue().toLowerCase(Locale.ROOT)) {
                        case "rmi" -> RmiClientNetManager.connect(host, port, username);
                        case "socket" -> SocketClientNetManager.connect(new InetSocketAddress(host, port), username);
                        default -> throw new IllegalStateException("Unexpected value: " + connectionTypeChoice.getValue());
                    };
                    var lobbyAndController = netManager.joinGame();

                    Parent sceneRoot = new JfxLobbySceneRoot(resources, threadPool, stage, lobbyAndController, netManager);

                    final var netManager0 = netManager;
                    Platform.runLater(() -> {
                        EventHandler<WindowEvent> onClose = evt -> {
                            int exitCode = 0;
                            try {
                                netManager0.close();
                            } catch (IOException ex) {
                                LOGGER.error("Failed to disconnect from the server while closing", ex);
                                exitCode = -1;
                            }

                            Platform.exit();
                            System.exit(exitCode);
                        };
                        stage.getScene().setRoot(sceneRoot);
                        stage.setOnCloseRequest(onClose);
                        stage.setOnHiding(onClose);
                    });
                } catch (NickNotValidException ex) {
                    Platform.runLater(() -> errorLabel.setText(ex.getMessage()));
                } catch (Throwable ex) {
                    if (netManager != null) {
                        try {
                            netManager.close();
                        } catch (IOException exc) {
                            ex.addSuppressed(ex);
                        }
                    }

                    LOGGER.error("Failed to connect", ex);
                    Platform.runLater(() -> errorLabel.setText("Failed to connect to the server. Check ip and port"));
                } finally {
                    Platform.runLater(() -> isConnecting.set(false));
                }
            });
        };

        // Create start button
        Button startButton = new Button("Connect");
        startButton.setOnAction(eventIpCHeck);
        startButton.setDefaultButton(true);
        startButton.disableProperty().bind(isConnecting
                .or(BooleanExpression.booleanExpression(usernameTextField.textProperty().map(String::isBlank))));

        //vbox
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);
        vbox.getChildren().addAll(titleView, mainPane, startButton, errorLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10d);

        AnchorPane.setTopAnchor(vbox, 1d);
        AnchorPane.setBottomAnchor(vbox, 10d);
        AnchorPane.setLeftAnchor(vbox, 10d);
        AnchorPane.setRightAnchor(vbox, 10d);
        getChildren().add(vbox);
        //prefWidthProperty().bind(scene.widthProperty());
        //prefHeightProperty().bind(scene.heightProperty());

        setStyle(getStyle() + "-fx-font-family: \"Inter Regular\";");
        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));
    }
}
