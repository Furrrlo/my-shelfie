package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;

/**
 * Converter from {@link Color} to {@link ConsoleColors}
 */
class TuiColorConverter {

    private TuiColorConverter() {
    }

    /**
     * Converts a tile color to a console color
     * 
     * @param color color of a tile
     * @param highlight boolean
     * @return string representing the console color
     * @see ConsoleColors
     */
    public static String color(Color color, boolean highlight) {
        if (!highlight) {
            return switch (color) {
                case BLUE -> ConsoleColors.BLUE_BACKGROUND;
                case GREEN -> ConsoleColors.GREEN_BACKGROUND;
                case YELLOW -> ConsoleColors.ORANGE_BACKGROUND;
                case PINK -> ConsoleColors.PURPLE_BACKGROUND;
                case WHITE -> ConsoleColors.YELLOW_BACKGROUND;
                case LIGHTBLUE -> ConsoleColors.CYAN_BACKGROUND;
            };
        }

        return switch (color) {
            case BLUE -> ConsoleColors.BLUE_BACKGROUND_BRIGHT;
            case GREEN -> ConsoleColors.GREEN_BACKGROUND_BRIGHT;
            case YELLOW -> ConsoleColors.ORANGE_BACKGROUND_BRIGHT;
            case PINK -> ConsoleColors.PURPLE_BACKGROUND_BRIGHT;
            case WHITE -> ConsoleColors.YELLOW_BACKGROUND_BRIGHT;
            case LIGHTBLUE -> ConsoleColors.CYAN_BACKGROUND_BRIGHT;
        };
    }
}
