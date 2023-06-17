package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Implementation of {@link FxResourcesLoader} which employs heavy caching and keeps anything ever loaded
 * in memory.
 * <p>
 * This class is thread safe and resources can be loaded by different threads.
 */
class FxCachingResourcesLoader implements FxResourcesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FxCachingResourcesLoader.class);

    private final ConcurrentMap<String, Resource<Image>> images = new ConcurrentHashMap<>();
    private final ConcurrentMap<CroppedImageKey, Resource<Image>> croppedImages = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Resource<byte[]>> fxmls = new ConcurrentHashMap<>();

    public void populateCache() {
        // This is not ideal, but there's no other (easy) way to collect these automagically
        final List<Resource<?>> toLoad = List.of(
                getImageResource("fa/message.png"),
                getImageResource("fa/open-message.png"),
                getImageResource("fa/trophy.png"),
                getImageResource("fa/teamPicture.png"),
                getImageResource("assets/common goal cards/1.jpg"),
                getImageResource("assets/common goal cards/2.jpg"),
                getImageResource("assets/common goal cards/3.jpg"),
                getImageResource("assets/common goal cards/4.jpg"),
                getImageResource("assets/common goal cards/5.jpg"),
                getImageResource("assets/common goal cards/6.jpg"),
                getImageResource("assets/common goal cards/7.jpg"),
                getImageResource("assets/common goal cards/8.jpg"),
                getImageResource("assets/common goal cards/9.jpg"),
                getImageResource("assets/common goal cards/10.jpg"),
                getImageResource("assets/common goal cards/11.jpg"),
                getImageResource("assets/common goal cards/12.jpg"),
                getImageResource("assets/scoring tokens/end game.jpg"),
                getImageResource("assets/Publisher material/Icon 50x50px.png"),
                getImageResource("assets/Publisher material/Box 280x280px.png"),
                getImageResource("assets/misc/sfondo parquet.jpg"),
                getImageResource("assets/misc/base_pagina2.jpg"),
                getImageResource("assets/personal goal cards/Personal_Goals.png"),
                getImageResource("assets/personal goal cards/Personal_Goals2.png"),
                getImageResource("assets/personal goal cards/Personal_Goals3.png"),
                getImageResource("assets/personal goal cards/Personal_Goals4.png"),
                getImageResource("assets/personal goal cards/Personal_Goals5.png"),
                getImageResource("assets/personal goal cards/Personal_Goals6.png"),
                getImageResource("assets/personal goal cards/Personal_Goals7.png"),
                getImageResource("assets/personal goal cards/Personal_Goals8.png"),
                getImageResource("assets/personal goal cards/Personal_Goals9.png"),
                getImageResource("assets/personal goal cards/Personal_Goals10.png"),
                getImageResource("assets/personal goal cards/Personal_Goals11.png"),
                getImageResource("assets/personal goal cards/Personal_Goals12.png"),
                getImageResource("assets/misc/firstplayertoken.png"),
                getImageResource("assets/scoring tokens/scoring_2.jpg"),
                getImageResource("assets/scoring tokens/scoring_4.jpg"),
                getImageResource("assets/scoring tokens/scoring_6.jpg"),
                getImageResource("assets/scoring tokens/scoring_8.jpg"),
                getImageResource("assets/scoring tokens/scoring_back_EMPTY.jpg"),
                getImageResource("assets/scoring tokens/end game.jpg"),
                getImageResource("assets/item tiles/Gatti1.1.png"),
                getImageResource("assets/item tiles/Gatti1.2.png"),
                getImageResource("assets/item tiles/Gatti1.3.png"),
                getImageResource("assets/item tiles/Libri1.1.png"),
                getImageResource("assets/item tiles/Libri1.2.png"),
                getImageResource("assets/item tiles/Libri1.3.png"),
                getImageResource("assets/item tiles/Giochi1.1.png"),
                getImageResource("assets/item tiles/Giochi1.2.png"),
                getImageResource("assets/item tiles/Giochi1.3.png"),
                getImageResource("assets/item tiles/Cornici1.1.png"),
                getImageResource("assets/item tiles/Cornici1.2.png"),
                getImageResource("assets/item tiles/Cornici1.3.png"),
                getImageResource("assets/item tiles/Trofei1.1.png"),
                getImageResource("assets/item tiles/Trofei1.2.png"),
                getImageResource("assets/item tiles/Trofei1.3.png"),
                getImageResource("assets/item tiles/Piante1.1.png"),
                getImageResource("assets/item tiles/Piante1.2.png"),
                getImageResource("assets/item tiles/Piante1.3.png"),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 110, 105, 198, 198),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 1058, 334, 198, 198),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 347, 792, 198, 198),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 586, 105, 198, 198),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 822, 562, 198, 198),
                getCroppedImageResource("assets/personal goal cards/Personal_Goals.png", 586, 1249, 198, 198),
                getCroppedImageResource("assets/boards/livingroom.png", 1977, 2465, 889, 394),
                getFxmlResource("board.fxml"),
                getFxmlResource("shelfie.fxml"));

        LOGGER.info("Starting to pre-load resources");
        long startMillis = System.currentTimeMillis();
        for (Resource<?> resource : toLoad) {
            try {
                @SuppressWarnings("unused")
                var unused = resource.get();
            } catch (Throwable t) {
                LOGGER.error("Failed to pre-load resource {}", resource, t);
            }
        }
        LOGGER.info("Finished pre-loading resources, took {} ms", System.currentTimeMillis() - startMillis);
    }

    private Resource<Image> getImageResource(String path) {
        return images.computeIfAbsent(path, p -> new Resource<>(() -> new Image(FxResources.getResourceAsStream(p))));
    }

    @Override
    public Image loadImage(String path) {
        return getImageResource(path).get();
    }

    private Resource<Image> getCroppedImageResource(String path, int x, int y, int width, int height) {
        return croppedImages.computeIfAbsent(new CroppedImageKey(path, x, y, width, height), key -> new Resource<>(() -> {
            var img = loadImage(key.path());
            return new WritableImage(img.getPixelReader(), key.x(), key.y(), key.width(), key.height());
        }));
    }

    @Override
    public Image loadCroppedImage(String path, int x, int y, int width, int height) {
        return getCroppedImageResource(path, x, y, width, height).get();
    }

    private Resource<byte[]> getFxmlResource(String path) {
        return fxmls.computeIfAbsent(path, p -> new Resource<>(() -> {
            try (InputStream is = FxResources.getResourceAsStream(p)) {
                return is.readAllBytes();
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to read fxml '" + path + "'", ex);
            }
        }));
    }

    @Override
    public FXMLLoader getFxmlLoader(String path) {
        URL originalUrl = FxResources.getResource(path);

        URL url;
        try {
            var bytes = getFxmlResource(path).get();
            // Need to feed the original url as FXMLLoader resolves relative resources using it
            url = new URL(
                    originalUrl.getProtocol(), originalUrl.getHost(), originalUrl.getPort(), originalUrl.getFile(),
                    new InMemoryUrlStreamHandler(bytes));
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to create URL for fxml loader {}, falling back to file", path, e);
            url = originalUrl;
        }

        var fxmlLoader = new FXMLLoader(url);
        var builderFactory = new InjectingBuilderFactory(fxmlLoader.getBuilderFactory());
        builderFactory.inject(FxResourcesLoader.NAMED_ARG_NAME, this);
        fxmlLoader.setBuilderFactory(builderFactory);
        return fxmlLoader;
    }

    private static class Resource<T> {

        private volatile @Nullable T resource;
        private final Supplier<T> loader;
        private final Lock lock = new ReentrantLock();

        public Resource(Supplier<T> loader) {
            this.loader = loader;
        }

        public T get() {
            // double checked-locking
            var resource = this.resource;
            if (resource == null) {
                lock.lock();
                try {
                    resource = this.resource;
                    if (resource == null)
                        this.resource = resource = loader.get();
                    return resource;
                } finally {
                    lock.unlock();
                }
            }
            return resource;
        }
    }

    private record CroppedImageKey(String path, int x, int y, int width, int height) {
    }

    private static class InMemoryUrlStreamHandler extends URLStreamHandler {

        private final byte[] bytes;

        public InMemoryUrlStreamHandler(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        protected URLConnection openConnection(URL u) {
            return new InMemoryUrlConnection(u, bytes);
        }
    }

    private static class InMemoryUrlConnection extends URLConnection {

        private final byte[] bytes;
        private @Nullable InputStream in;

        protected InMemoryUrlConnection(URL url, byte[] bytes) {
            super(url);
            this.bytes = bytes;
        }

        @Override
        public void connect() {
            if (!connected) {
                in = new ByteArrayInputStream(bytes);
                connected = true;
            }
        }

        @Override
        public InputStream getInputStream() {
            connect();
            return Objects.requireNonNull(in, "in");
        }

        @Override
        public long getContentLengthLong() {
            return bytes.length;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }
    };
}
