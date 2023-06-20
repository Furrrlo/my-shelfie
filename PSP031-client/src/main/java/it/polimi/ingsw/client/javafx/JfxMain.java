package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.client.tui.TuiMain;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main class which starts the client
 *
 * @implNote it is important that this class does not declare any static logger fields,
 *           as it might trigger log4j initialization before {@link TuiMain#main(String[])} or
 *           {@link JfxMain#main(String[])} has the possibility to set the {@code log4j.configurationFile}
 *           system property to the correct file
 */
public class JfxMain {

    public static void main(String[] args) {
        // Configure log4j file if none is already set
        if (System.getProperty("log4j.configurationFile") == null)
            System.setProperty("log4j.configurationFile", "log4j2-jfx.xml");

        // add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        JfxApplication.launch(JfxApplication.class, args);
    }
}
