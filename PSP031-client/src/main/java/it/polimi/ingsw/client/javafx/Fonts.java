package it.polimi.ingsw.client.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Utilities for working with JFX fonts */
class Fonts {

    @SuppressWarnings("unchecked")
    private static final Map<FontWeight, ChangeListener<? super Font>> CHANGE_WIDTH_LISTENERS = Arrays
            .stream(FontWeight.values())
            .collect(Collectors.toMap(Function.identity(), w -> (obs, oldV, newV) -> {
                if (obs instanceof WritableObjectValue<?> wr && !getFontWeight(newV).equals(w))
                    ((WritableObjectValue<Font>) wr).set(deriveWithWeight(newV, w));
            }));

    private Fonts() {
    }

    public static Font deriveWithSize(Font font, double size) {
        return Font.font(font.getFamily(), getFontWeight(font), getFontPosture(font), size);
    }

    public static void changeSize(WritableObjectValue<Font> font, double size) {
        font.set(deriveWithSize(font.get(), size));
    }

    public static Font deriveWithWeight(Font font, FontWeight weight) {
        return Font.font(font.getFamily(), weight, getFontPosture(font), font.getSize());
    }

    public static void enforceWeight(ObjectProperty<Font> font, FontWeight weight) {
        font.set(deriveWithWeight(font.get(), weight));
        // Force weight change to persist even across CSS changes
        CHANGE_WIDTH_LISTENERS.values().forEach(font::removeListener);
        font.addListener(Objects.requireNonNull(CHANGE_WIDTH_LISTENERS.get(weight)));
    }

    public static Font deriveWithSizeAndWeight(Font font, FontWeight weight, double size) {
        return Font.font(font.getFamily(), weight, getFontPosture(font), size);
    }

    public static void changeSizeAndWeight(WritableObjectValue<Font> font, FontWeight weight, double size) {
        font.set(deriveWithSizeAndWeight(font.get(), weight, size));
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
}
