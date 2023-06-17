package it.polimi.ingsw.client.javafx;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Utility class to load resources from within the jar itself
 * <p>
 * Resources must be placed inside the {@code it.polimi.ingsw.client.javafx} package or subpackages of it
 * in order to be properly loaded.
 * Path names used as arguments to the methods of this class will be relative to that package.
 */
class FxResources {

    private FxResources() {
    }

    public static InputStream getResourceAsStream(String path) {
        return Objects.requireNonNull(FxResources.class.getResourceAsStream(path), "Couldn't find resource " + path);
    }

    public static URL getResource(String path) {
        return Objects.requireNonNull(FxResources.class.getResource(path), "Couldn't find resource " + path);
    }
}
