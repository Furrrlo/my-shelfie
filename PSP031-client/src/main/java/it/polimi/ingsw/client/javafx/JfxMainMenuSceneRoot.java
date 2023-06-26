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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Root node for the main menu scene
 * <p>
 * Allows the player to choose the connection mode, the server address and the username
 */
class JfxMainMenuSceneRoot extends Pane {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxMainMenuSceneRoot.class);

    private final TextField ipTextField;
    private final TextField portTextField;
    private final TextField usernameTextField;
    private final MainMenuPane mainMenuPane;
    private final ChoiceBox<String> connectionTypeChoice;
    private final PlayButton playButton;
    private final QuitGameButton quitGameButton;
    private final ImageView gameLogo;
    private final RulesPane rulesPane;

    public JfxMainMenuSceneRoot(FxResourcesLoader resources, ExecutorService threadPool, Stage stage) {
        //var mainPane = new CenteringFitPane();
        //mainPane.getChildren().add(new MainMenuPane());
        //Pattern ipPattern = Pattern
        //                .compile(" (\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3} ");
        gameLogo = new ImageView(resources.loadCroppedImage(
                "assets/Publisher material/Title 2000x618px.png",
                167, 77, 1667, 444));
        gameLogo.setPreserveRatio(true);
        getChildren().add(gameLogo);

        ipTextField = new TextField();
        portTextField = new TextField();
        usernameTextField = new TextField();
        playButton = new PlayButton("Connect");
        quitGameButton = new QuitGameButton();
        rulesPane = new RulesPane();

        connectionTypeChoice = new MenuChoiceBox<>();
        connectionTypeChoice.getItems().addAll("RMI", "Socket");
        connectionTypeChoice.setOnAction(e -> portTextField
                .setPromptText(String.valueOf(switch (connectionTypeChoice.getValue().toLowerCase(Locale.ROOT)) {
                    case "rmi" -> Registry.REGISTRY_PORT;
                    case "socket" -> SocketClientNetManager.DEFAULT_PORT;
                    default -> throw new IllegalStateException("Unexpected value: " + connectionTypeChoice.getValue());
                })));
        connectionTypeChoice.getSelectionModel().selectFirst();
        mainMenuPane = new MainMenuPane();

        stage.setOnShown(e -> rulesPane.scrollPane.lookup(".viewport").setStyle("-fx-background-color: transparent;"));

        // Create labels
        /*
         * Label connectionTypeLabel = new Label("Connection Type:");
         * Label ipLabel = new Label("IP:");
         * Label portLabel = new Label("Port:");
         * Label usernameLabel = new Label("Username:");
         * 
         * // Create choice dialog for connection type
         * ChoiceBox<String> connectionTypeChoice = new MenuChoiceBox<>();
         * connectionTypeChoice.getItems().addAll("RMI", "Socket");
         * 
         * // Create text fields
         * TextField ipTextField = new TextField();
         * TextField portTextField = new TextField();
         * TextField usernameTextField = new TextField();
         * 
         * // Set prompt values
         * ipTextField.setPromptText("localhost");
         * portTextField.setTextFormatter(new TextFormatter<>(
         * c -> c.getControlNewText().matches("([1-9][0-9]*)?") ? c : null));
         * connectionTypeChoice.setOnAction(e -> portTextField
         * .setPromptText(String.valueOf(switch (connectionTypeChoice.getValue().toLowerCase(Locale.ROOT)) {
         * case "rmi" -> Registry.REGISTRY_PORT;
         * case "socket" -> SocketClientNetManager.DEFAULT_PORT;
         * default -> throw new IllegalStateException("Unexpected value: " + connectionTypeChoice.getValue());
         * })));
         * connectionTypeChoice.getSelectionModel().selectFirst();
         * 
         * 
         * 
         * // Create grid pane for layout
         * GridPane mainPane = new GridPane();
         * mainPane.setHgap(10);
         * mainPane.setVgap(10);
         * mainPane.setPadding(new Insets(10));
         * 
         * // Add components to grid pane
         * mainPane.add(connectionTypeLabel, 0, 0);
         * mainPane.add(connectionTypeChoice, 1, 0);
         * mainPane.add(ipLabel, 0, 1);
         * mainPane.add(ipTextField, 1, 1);
         * mainPane.add(portLabel, 0, 2);
         * mainPane.add(portTextField, 1, 2);
         * mainPane.add(usernameLabel, 0, 3);
         * mainPane.add(usernameTextField, 1, 3);
         * mainPane.setAlignment(Pos.CENTER);
         * 
         */
        //create tile image
        Image titleImage = new Image(FxResources.getResourceAsStream("assets/Publisher material/Title 2000x618px.png"), 400,
                124, true, false);
        ImageView titleView = new ImageView(titleImage);

        // ip validation
        Label errorLabel = new Label("");
        BooleanProperty isConnecting = new SimpleBooleanProperty(this, "isConnecting", false);
        EventHandler<ActionEvent> eventIpCHeck = e -> {
            String host = ipTextField.getText().isBlank()
                    ? ipTextField.getPromptText().strip()
                    : ipTextField.getText().strip();
            int port = Integer.parseInt(portTextField.getText().isBlank()
                    ? portTextField.getPromptText().strip()
                    : portTextField.getText().strip());
            String username = usernameTextField.getText();
            String networkProtocol = connectionTypeChoice.getValue();

            isConnecting.set(true);
            threadPool.execute(() -> {
                try {
                    connectAndJoinGame(
                            resources,
                            threadPool,
                            stage,
                            () -> switch (networkProtocol.toLowerCase(Locale.ROOT)) {
                                case "rmi" -> RmiClientNetManager.connect(host, port, username);
                                case "socket" -> SocketClientNetManager.connect(new InetSocketAddress(host, port), username);
                                default -> throw new IllegalStateException("Unexpected value: " + networkProtocol);
                            });
                } catch (NickNotValidException ex) {
                    Platform.runLater(() -> errorLabel.setText(ex.getMessage()));
                } catch (Throwable ex) {
                    LOGGER.error("Failed to connect", ex);
                    Platform.runLater(() -> errorLabel.setText("Failed to connect to the server. Check ip and port"));
                } finally {
                    Platform.runLater(() -> isConnecting.set(false));
                }
            });
        };

        playButton.setOnAction(eventIpCHeck);
        playButton.setDefaultButton(true);
        playButton.disableProperty().bind(isConnecting
                .or(BooleanExpression.booleanExpression(usernameTextField.textProperty().map(String::isBlank))));
        quitGameButton.setOnMouseClicked(event -> getScene().getWindow().hide());

        VBox menuVbox = new VBox();
        menuVbox.getChildren().addAll(mainMenuPane, playButton, errorLabel);

        menuVbox.setPadding(new Insets(10));
        menuVbox.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        //vbox
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(titleView, menuVbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15);

        /*
         * AnchorPane.setTopAnchor(vbox, 1d);
         * AnchorPane.setBottomAnchor(vbox, 10d);
         * AnchorPane.setLeftAnchor(vbox, 10d);
         * AnchorPane.setRightAnchor(vbox, 10d);
         */
        getChildren().addAll(mainMenuPane, playButton, quitGameButton, rulesPane);
        //prefWidthProperty().bind(scene.widthProperty());
        //prefHeightProperty().bind(scene.heightProperty());

        setStyle(getStyle() + "-fx-font-family: \"Inter\";");
        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));
    }

    @Override
    protected void layoutChildren() {
        final double scale = Math.min(getWidth() / 1040d, getHeight() / 585d);
        final double border = 20 * scale;

        this.gameLogo.resizeRelocate(375.0 * scale, border, 350 * scale, 93.22 * scale);
        this.gameLogo.setFitWidth(350 * scale);
        this.gameLogo.setFitHeight(93.22 * scale);

        this.mainMenuPane.resizeRelocate(
                border,
                2 * border + 150 * scale,
                320 * scale,
                130 * scale);

        this.playButton.resizeRelocate(
                border,
                getHeight() - border - 200 * scale,
                220 * scale,
                60 * scale);
        Fonts.changeSize(this.playButton.fontProperty(), 30 * scale);

        this.quitGameButton.resizeRelocate(
                border,
                getHeight() - border - 100 * scale,
                220 * scale,
                60 * scale);
        Fonts.changeSize(this.quitGameButton.fontProperty(), 30 * scale);

        this.rulesPane.resizeRelocate(
                2 * border + 350 * scale,
                2 * border + 110 * scale,
                getWidth() - 3 * border - 350 * scale,
                getHeight() - 3 * border - 100 * scale);

    }

    private class MainMenuPane extends GridPane {

        public MainMenuPane() {
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(0)))));
            setPadding(new Insets(10));
            setHgap(10d);

            Label connectionTypeLabel = new Label("Connection Type:");
            Label ipLabel = new Label("IP:");
            Label portLabel = new Label("Port:");
            Label usernameLabel = new Label("Username:");

            // Set prompt values
            ipTextField.setPromptText("localhost");
            portTextField.setTextFormatter(new TextFormatter<>(
                    c -> c.getControlNewText().matches("([1-9][0-9]*)?") ? c : null));
            connectionTypeLabel.prefWidthProperty().bind(widthProperty());
            this.add(connectionTypeLabel, 0, 0);
            connectionTypeChoice.prefWidthProperty().bind(widthProperty());
            this.add(connectionTypeChoice, 1, 0);
            this.add(ipLabel, 0, 1);
            ipTextField.prefWidthProperty().bind(widthProperty());
            this.add(ipTextField, 1, 1);
            this.add(portLabel, 0, 2);
            portTextField.prefWidthProperty().bind(widthProperty());
            this.add(portTextField, 1, 2);
            this.add(usernameLabel, 0, 3);
            usernameTextField.prefWidthProperty().bind(widthProperty());
            this.add(usernameTextField, 1, 3);
            this.setAlignment(Pos.CENTER);

        }
    }

    private static class RulesPane extends Pane {
        public ScrollPane scrollPane;

        public RulesPane() {
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(0)))));

            //textArea.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

            String text = """
                    **Rules:**

                    You’ve just taken home your new bookshelf and now it’s time to put your favorite items in the display: books, boardgames, portraits... Who will show the best organized shelfie?

                    During **your turn**, you must **take 1, 2, or 3 item tiles** from the living room board (shared by all the players), following these rules:

                    • The **tiles** you take must be **adjacent** to each other and form a **straight line**.
                    • All the tiles you take must have at least **one side free** at the beginning of your turn.

                    Then, you must **place** all the tiles you’ve picked into **1 column** of your bookshelf (a 3D display) to meet the personal goal cards, which grant points if you **match** the highlighted spaces with the corresponding item tiles, or the common goal cards, which grant points if you achieve the illustrated **pattern**. You also score points if you connect item tiles of the same type.

                    The first player who fills all the spaces of their bookshelf triggers the end game and takes the end game token that grants additional points. The game continues until the end of the turn of the player sitting on the right of the player holding the first player token.

                    The player who **scores the most points wins** the game.

                    A game of strategy and glance, different every time thanks to the variety of common and personal goals. The beautiful images of the item tiles will really give you the feeling of tidying up your precious shelf.""";

            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setBackground(Background.fill(Color.TRANSPARENT));
            textArea.setText(text);
            setStyle(getStyle() + "-fx-font-family: \"Inter\";");
            textArea.prefWidthProperty().bind(this.widthProperty());
            textArea.prefHeightProperty().bind(this.heightProperty());

            //prefWidthProperty().bind(this.widthProperty());
            //prefHeightProperty().bind(this.heightProperty());
            setPadding(new Insets(15));
            //setStyle("-fx-padding: 15px;");
            TextFlow textFlow = new TextFlow();
            String[] parts = text.split("\\*\\*"); // Split the text at '**' to identify the important parts
            for (int i = 0; i < parts.length; i++) {
                Text textNode = new Text(parts[i]);
                if (i % 2 != 0) { // Apply bold formatting to the important parts
                    textNode.setFont(Font.font("Inter", FontWeight.BOLD, 14));
                } else {
                    textNode.setFont(Font.font("Inter", 14));
                }
                textFlow.getChildren().add(textNode);
            }
            scrollPane = new ScrollPane();
            //lookup(".viewport").setStyle("-fx-background-color: red;");
            scrollPane.prefWidthProperty().bind(this.widthProperty());
            scrollPane.prefHeightProperty().bind(this.heightProperty());
            // getStylesheets().add(
            //        getClass().getResource("style.css").toString());

            setStyle("-fx-background-color: transparent;");
            scrollPane.setContent(textFlow);
            scrollPane.setFitToWidth(true);
            getChildren().add(scrollPane);

        }

    }

    public static void connectAndJoinGame(FxResourcesLoader resources,
                                          ExecutorService threadPool,
                                          Stage stage,
                                          Callable<ClientNetManager> netManagerFactory)
            throws Exception {
        ClientNetManager netManager = null;
        try {
            netManager = netManagerFactory.call();
            var lobbyAndController = netManager.joinGame();

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
                stage.getScene().setRoot(JfxLobbySceneRoot
                        .getSceneRootFor(resources, threadPool, stage, lobbyAndController, netManager0));
                stage.setOnCloseRequest(onClose);
                stage.setOnHiding(onClose);
            });
        } catch (NickNotValidException ex) {
            throw ex;
        } catch (Throwable ex) {
            if (netManager != null) {
                try {
                    netManager.close();
                } catch (IOException exc) {
                    ex.addSuppressed(ex);
                }
            }

            throw ex;
        }
    }
}
