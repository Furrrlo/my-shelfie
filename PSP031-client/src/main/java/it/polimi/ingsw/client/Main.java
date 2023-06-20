package it.polimi.ingsw.client;

import it.polimi.ingsw.client.javafx.JfxMain;
import it.polimi.ingsw.client.tui.TuiMain;

import java.util.Arrays;

/**
 * Main class which starts the client
 * <p>
 * This class will attempt to start the GUI in case no console is detected, otherwise
 * it will prompt the user whether to use the GUI or the TUI in the console itself.
 * The choice can also be overridden using command line args:
 * - {@code '--gui'} or {@code '-g'} will start the GUI
 * - {@code '--tui'}, {@code '-t'}, {@code '--cli'} or {@code '-c'} will start the CLI
 *
 * @implNote it is important that this class does not declare any static logger fields,
 *           as it might trigger log4j initialization before {@link TuiMain#main(String[])} or
 *           {@link JfxMain#main(String[])} has the possibility to set the {@code log4j.configurationFile}
 *           system property to the correct file
 */
public class Main {

    public static void main(String[] args) {
        boolean hasTuiArg = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--tui")
                || arg.equalsIgnoreCase("--cli")
                || arg.equalsIgnoreCase("-t")
                || arg.equalsIgnoreCase("-c"));
        boolean hasGuiArg = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--gui")
                || arg.equalsIgnoreCase("-g"));

        if (hasTuiArg && hasGuiArg) {
            System.err.println("Only one between --gui and --tui can be specified");
            System.exit(-1);
            return;
        }

        var console = System.console();
        if (hasTuiArg) {
            TuiMain.main(args);
            return;
        }

        if (hasGuiArg || console == null) {
            JfxMain.main(args);
            return;
        }

        System.out.println("Which UI do you want to use?");
        System.out.println("1. Textual User Interface");
        System.out.println("2. Graphical User Interface");
        do {
            var read = console.readLine();

            int choice;
            try {
                choice = Integer.parseInt(read);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid choice " + read);
                continue;
            }

            if (choice == 1) {
                TuiMain.main(args);
                return;
            }

            if (choice == 2) {
                JfxMain.main(args);
                return;
            }

            System.out.println("Invalid choice " + read);
        } while (true);
    }
}
