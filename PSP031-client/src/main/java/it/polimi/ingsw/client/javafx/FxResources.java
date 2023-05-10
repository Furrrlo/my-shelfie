package it.polimi.ingsw.client.javafx;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

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
