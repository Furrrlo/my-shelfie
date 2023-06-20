package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.utils.ThreadPools;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JfxApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxApplication.class);

    private final ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private final AtomicInteger n = new AtomicInteger();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            var th = new Thread(r);
            th.setName("jfx-executor-" + n.getAndIncrement());
            th.setDaemon(true);
            th.setUncaughtExceptionHandler((th0, t) -> LOGGER.error("Uncaught exception in JFX worker thread {}", th0, t));
            return th;
        }
    });

    @Override
    public void start(Stage stage) throws Exception {
        var resources = new FxCachingResourcesLoader();
        // Load fonts from ttf files
        Font.loadFonts(FxResources.getResourceAsStream("Inter-VariableFont_slnt,wght.ttf"), 0);
        threadPool.execute(ThreadPools.giveNameToTask("JFX-resources-preload-thread", resources::populateCache));

        Scene scene = new Scene(new JfxMainMenuSceneRoot(resources, threadPool, stage));

        stage.setTitle("My Shelfie");

        // Let jfx pick the best fit
        stage.getIcons().add(resources.loadImage("assets/Publisher material/Icon 50x50px.png"));
        stage.getIcons().add(resources.loadImage("assets/Publisher material/Box 280x280px.png"));

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setWidth(1080);
        stage.setMinHeight(500);
        stage.setHeight(720);
        stage.show();
    }
}
