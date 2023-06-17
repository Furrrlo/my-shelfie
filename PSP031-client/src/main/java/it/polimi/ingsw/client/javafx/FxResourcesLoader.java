package it.polimi.ingsw.client.javafx;

import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

/**
 * An implementation of this interface is in charge of loading the requested resources
 * from within the jar itself.
 * <p>
 * Implementations of this might employ caching strategies in order to speed up loading.
 * <p>
 * An instance of this can be injected using {@link NamedArg} with a value of {@link #NAMED_ARG_NAME}
 */
interface FxResourcesLoader {

    /**
     * Value to use as {@link NamedArg#value()} in order to request the instance of
     * the current {@link FxResourcesLoader} as a constructor param
     */
    String NAMED_ARG_NAME = "my-shelfie-resources-loader";

    /**
     * Loads an image from the given path
     *
     * @param path path to load from, as accepted by {@link FxResources}
     * @return image loaded from the given path
     */
    Image loadImage(String path);

    /**
     *
     * Loads an image from the given path and crops it to the requested coords
     *
     * @param path path to load from, as accepted by {@link FxResources}
     * @param x the X coordinate of the upper left corner of the region to
     *        read from the original image
     * @param y the Y coordinate of the upper left corner of the region to
     *        read from the original image
     * @param width the width of the region to be read from the original image
     * @param height the height of the region to be read from the original image
     * @return image loaded from the given path, cropped as requested
     */
    Image loadCroppedImage(String path, int x, int y, int width, int height);

    /**
     * Creates a new FXMLLoader to load a fxml file from the given path
     *
     * @param path path to load from, as accepted by {@link FxResources}
     * @return loader which loads from the given path
     */
    FXMLLoader getFxmlLoader(String path);
}
