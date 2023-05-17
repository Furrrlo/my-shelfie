package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class JfxApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxApplication.class);

    @Override
    public void start(Stage stage) throws Exception {
        var netManager = SocketClientNetManager.connect(new InetSocketAddress(1234), "jfx");
        var lobbyAndController = netManager.joinGame();
        lobbyAndController.controller().ready(true);

        GameAndController<?> gameAndController;
        if ((gameAndController = lobbyAndController.lobby().game().get()) == null) {
            final CompletableFuture<GameAndController<?>> gameAndControllerFuture = new CompletableFuture<>();
            lobbyAndController.lobby().game().registerObserver(gameAndControllerFuture::complete);
            gameAndController = gameAndControllerFuture.get();
        }

        Scene scene = new JfxGameScene(gameAndController.game(), gameAndController.controller());

        stage.setTitle("My Shelfie");

        // Let jfx pick the best fit
        stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Icon 50x50px.png")));
        stage.getIcons().add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Box 280x280px.png")));

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setWidth(800);
        stage.setMinHeight(500);
        stage.setHeight(500);
        stage.show();
        stage.setOnCloseRequest(e -> {
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
    }
}
