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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // Sadly JavaFX does not support variable fonts, so we need to load each combination by hand
        var loadedFonts = Stream.of(
                "Inter-Black.otf",
                "Inter-BlackItalic.otf",
                "Inter-Bold.otf",
                "Inter-BoldItalic.otf",
                "Inter-ExtraBold.otf",
                "Inter-ExtraBoldItalic.otf",
                "Inter-ExtraLight.otf",
                "Inter-ExtraLightItalic.otf",
                "Inter-Italic.otf",
                "Inter-Light.otf",
                "Inter-LightItalic.otf",
                "Inter-Medium.otf",
                "Inter-MediumItalic.otf",
                "Inter-Regular.otf",
                "Inter-SemiBold.otf",
                "Inter-SemiBoldItalic.otf",
                "Inter-Thin.otf",
                "Inter-ThinItalic.otf")
                .map(f -> Font.loadFont(FxResources.getResourceAsStream("fonts/" + f), 0))
                .collect(Collectors.toUnmodifiableSet());
        LOGGER.info("Loaded fonts {}", loadedFonts);

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
