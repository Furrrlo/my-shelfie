package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;

public class JfxMainMenuScene extends Scene {
    private static final Logger LOGGER = LoggerFactory.getLogger(JfxMainMenuScene.class);

    public JfxMainMenuScene(Stage stage) {
        super(createRootNode(stage));
    }

    private static Parent createRootNode(Stage stage) {
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
        connectionTypeChoice.getSelectionModel().selectFirst();

        // Create text fields
        TextField ipTextField = new TextField();
        TextField portTextField = new TextField();
        TextField usernameTextField = new TextField();

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

        //ip validation
        Label errorLabel = new Label("");
        EventHandler<ActionEvent> eventIpCHeck = e -> {
            String ipText = ipTextField.getText();
            String portText = portTextField.getText();
            String usernameText = usernameTextField.getText();
            String connectionType = connectionTypeChoice.getValue();

            try {
                ClientNetManager netManager;
                if (connectionType.equals("RMI")) {
                    if (ipText.equals("")) {
                        ipText = "localhost";
                    }
                    if (portText.equals("")) {
                        portText = "1099";
                    }
                    netManager = RmiClientNetManager.connect(ipText, Integer.parseInt(portText), usernameText);
                } else if (connectionType.equals("Socket")) {
                    if (ipText.equals("")) {
                        ipText = "localhost";
                    }
                    if (portText.equals("")) {
                        portText = "1234";
                    }
                    netManager = SocketClientNetManager.connect(new InetSocketAddress(ipText, Integer.parseInt(portText)),
                            usernameText);
                } else {
                    throw new RuntimeException();
                }
                var lobbyAndController = netManager.joinGame();

                Scene scene;
                var game = lobbyAndController.lobby().game().get();
                if (game != null) {
                    scene = new JfxGameScene(stage, game.game(), game.controller(), netManager);
                } else {
                    scene = new JfxLobbyScene(stage, lobbyAndController, netManager);
                }
                stage.setScene(scene);

                stage.setOnCloseRequest(exit -> {
                    int exitCode = 0;
                    try {
                        netManager.close();
                    } catch (IOException ex) {
                        LOGGER.error("Failed to disconnect from the server while closing", ex);
                        exitCode = -1;
                    }

                    Platform.exit();
                    System.exit(exitCode);
                });

            } catch (NickNotValidException ex) {
                errorLabel.setText(ex.getMessage());

            } catch (Exception ex) {
                errorLabel.setText("Failed to connect to the server. Check IpP and port");
                LOGGER.error("Failed to connect", ex);
            }
        };

        // Create start button
        Button startButton = new Button("Connect");
        startButton.setOnAction(eventIpCHeck);

        //vbox
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);
        vbox.getChildren().addAll(titleView, mainPane, startButton, errorLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10d);

        AnchorPane anchorPane = new AnchorPane();

        AnchorPane.setTopAnchor(vbox, 1d);
        AnchorPane.setBottomAnchor(vbox, 10d);
        AnchorPane.setLeftAnchor(vbox, 10d);
        AnchorPane.setRightAnchor(vbox, 10d);
        anchorPane.getChildren().add(vbox);
        //anchorPane.prefWidthProperty().bind(scene.widthProperty());
        //anchorPane.prefHeightProperty().bind(scene.heightProperty());

        anchorPane.setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/sfondo parquet.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        return anchorPane;
    }
}
