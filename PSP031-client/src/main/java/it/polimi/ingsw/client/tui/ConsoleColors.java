package it.polimi.ingsw.client.tui;

/**
 * Ansi escapes strings of colors, to be printed in terminal.
 * 
 * @apiNote to use with: <code>System.out.println(ConsoleColors.RED + "RED COLORED" + ConsoleColors.RESET + " NORMAL");</code>
 */
class ConsoleColors {
    //if you use colors with RGB scheme, it becomes GRB ( remeber !!! )
    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m"; // BLACK
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String ORANGE = "\033[38;2;255;127;0m"; //ORANGE
    public static final String BROWN_DARK = "\033[38;102;51;1;0m"; //DARK BROWN
    public static final String GREEN_DARK = "\033[38;1;102;1;0m"; //DARK GREEN
    public static final String BROWN = "\033[38;153;76;1;0m"; // BROWN
    public static final String BROWN_LIGHT = "\033[38;204;102;1;0m"; //BRIGHT BROWN
    public static final String BLUE = "\033[0;34m"; // BLUE
    public static final String PURPLE = "\033[0;35m"; // PURPLE
    public static final String CYAN = "\033[0;36m"; // CYAN
    public static final String WHITE = "\033[0;37m"; // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
    public static final String RED_BOLD = "\033[1;31m"; // RED
    public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m"; // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m"; // CYAN
    public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m"; // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
    public static final String RED_BACKGROUND = "\033[41m"; // RED
    public static final String RED_VERY_DARK_BACKGROUND = "\033[48;2;102;2;0m"; // RED VERY DARK
    public static final String RED_DARK_BACKGROUND = "\033[48;2;204;2;0m"; // RED DARK
    public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
    public static final String GREEN_DARK_BACKGROUND = "\033[48;2;2;102;0m"; //DARK GREEN 0 102 0
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String YELLOW_LIGHT_BACKGROUND = "\033[48;204;254;228;0m"; // YELLOW LIGHT
    public static final String ORANGE_BACKGROUND = "\033[48;2;204;102;0m"; //ORANGE 204 102 2
    public static final String BROWN_BACKGROUND = "\033[48;2;154;76;0m"; // BROWN   154 76 2
    public static final String BROWN_DARK_BACKGROUND = "\033[48;2;102;50;0m"; //DARK BROWN 102 50 2

    public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
    public static final String PINK_BACKGROUND = "\033[48;2;255;222;0m"; // PINK
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK
    public static final String RED_BRIGHT = "\033[0;91m"; // RED
    public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW

    public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String ORANGE_BACKGROUND_BRIGHT = "\033[48;2;255;127;0m"; //ORANGE
    public static final String BROWN_BACKGROUND_BRIGHT = "\033[48;2;204;102;1m"; //BRIGHT BROWN
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

}
