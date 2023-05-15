package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class JfxApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        var lobbyAndController = ((ClientNetManager) new SocketClientNetManager(new InetSocketAddress(1234))).joinGame("jfx");
        lobbyAndController.controller().ready(true);

        GameAndController<?> gameAndController;
        if ((gameAndController = lobbyAndController.lobby().game().get()) == null) {
            final CompletableFuture<GameAndController<?>> gameAndControllerFuture = new CompletableFuture<>();
            lobbyAndController.lobby().game().registerObserver(gameAndControllerFuture::complete);
            gameAndController = gameAndControllerFuture.get();
        }

        var gamePane = new GamePane(gameAndController.game(), gameAndController.controller());

        AnchorPane.setLeftAnchor(gamePane, 0.0D);
        AnchorPane.setRightAnchor(gamePane, 0.0D);
        AnchorPane.setTopAnchor(gamePane, 0.0D);
        AnchorPane.setBottomAnchor(gamePane, 0.0D);

        Scene scene = new Scene(gamePane);

        stage.setTitle("My Shelfie");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setWidth(1000);
        stage.setMinHeight(500);
        stage.setHeight(500);
        stage.show();
        stage.setOnCloseRequest(e -> {
            // TODO: exit more gracefully
            Platform.exit();
            System.exit(-1);
        });
    }
}
