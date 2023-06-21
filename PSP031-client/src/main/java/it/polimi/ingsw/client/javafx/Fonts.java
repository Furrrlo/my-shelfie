package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.lang.ref.WeakReference;
import java.util.*;

/** Utilities for working with JFX fonts */
class Fonts {

    private static final Map<WritableObjectValue<Font>, EnforceChangesListener> LISTENERS = new WeakHashMap<>();

    private Fonts() {
    }

    @SuppressWarnings("SameParameterValue")
    private static void setProperty(WritableObjectValue<Font> property,
                                    @Nullable String family,
                                    @Nullable FontWeight weight,
                                    @Nullable FontPosture posture,
                                    @Nullable Double size) {
        // If it's a css property, we want to support inheriting the rest of
        // the properties we did not set ourselves from parents n stuff.
        // When we set it directly from the prop, CSS will only use CSS props from
        // the component style itself, without inheriting the rest.
        // To avoid this, we will set it as if it was from an inline style, and
        // then register a listener to update it every time something changes
        if (property instanceof StyleableObjectProperty<Font> cssProp) {
            var listener = LISTENERS.get(cssProp);
            if (listener == null) {
                cssProp.addListener(listener = new EnforceChangesListener(cssProp));
                LISTENERS.put(cssProp, listener);
            }

            listener.family = family != null ? family : listener.family;
            listener.weight = weight != null ? weight : listener.weight;
            listener.posture = posture != null ? posture : listener.posture;
            listener.size = size != null ? size : listener.size;

            cssProp.applyStyle(StyleOrigin.INLINE, derive(cssProp.get(), family, weight, posture, size));
            return;
        }

        property.set(derive(property.get(), family, weight, posture, size));
    }

    public static Font derive(@Nullable Font font,
                              @Nullable String family,
                              @Nullable FontWeight weight,
                              @Nullable FontPosture posture,
                              @Nullable Double size) {
        return Font.font(
                family != null ? family : font != null ? font.getFamily() : null,
                weight != null ? weight : getFontWeight(font != null ? font : Font.getDefault()),
                posture != null ? posture : getFontPosture(font != null ? font : Font.getDefault()),
                size != null ? size : font != null ? font.getSize() : Font.getDefault().getSize());
    }

    public static void changeSize(WritableObjectValue<Font> font, double size) {
        setProperty(font, null, null, null, size);
    }

    public static void changeWeight(WritableObjectValue<Font> font, FontWeight weight) {
        setProperty(font, null, weight, null, null);
    }

    public static FontWeight getFontWeight(Font font) {
        var familyAndStyle = font.getFamily() + " " + font.getStyle().toUpperCase(Locale.ROOT);
        return Arrays.stream(FontWeight.values())
                .filter(w -> familyAndStyle.contains(w.name().replace("_", "")) ||
                        familyAndStyle.contains(w.name().replace("_", " ")))
                .findFirst()
                .orElse(FontWeight.NORMAL);
    }

    public static FontPosture getFontPosture(Font font) {
        var familyAndStyle = font.getFamily() + " " + font.getStyle().toUpperCase(Locale.ROOT);
        return Arrays.stream(FontPosture.values())
                .filter(w -> familyAndStyle.contains(w.name().replace("_", "")) ||
                        familyAndStyle.contains(w.name().replace("_", " ")))
                .findFirst()
                .orElse(FontPosture.REGULAR);
    }

    private static class EnforceChangesListener implements ChangeListener<Font> {

        private final WeakReference<StyleableObjectProperty<Font>> cssProp;
        //@formatter:off
        @Nullable String family;
        @Nullable FontWeight weight;
        @Nullable FontPosture posture;
        @Nullable Double size;
        //@formatter:on

        public EnforceChangesListener(StyleableObjectProperty<Font> cssProp) {
            this.cssProp = new WeakReference<>(cssProp);
        }

        @Override
        public void changed(ObservableValue<? extends Font> observable, @Nullable Font oldValue, @Nullable Font newValue) {
            var cssProp = this.cssProp.get();
            if (cssProp == null) {
                observable.removeListener(this);
                return;
            }

            var modifiedVal = derive(newValue, family, weight, posture, size);
            if (!Objects.equals(newValue, modifiedVal))
                cssProp.applyStyle(StyleOrigin.INLINE, modifiedVal);
        }
    }
}
