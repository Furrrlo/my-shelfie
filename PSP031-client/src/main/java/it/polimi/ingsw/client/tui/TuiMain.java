package it.polimi.ingsw.client.tui;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Main class which starts the client-side TUI
 *
 * @implNote it is important that this class does not declare any static logger fields,
 *           as it might trigger log4j initialization before {@link #main(String[])} has the
 *           possibility to set the {@code log4j.configurationFile} system property to the
 *           correct file
 */
class TuiMain {

    public static void main(String[] args) {
        //If the client has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        //@see https://bugs.openjdk.org/browse/JDK-8042232
        // To work around this, run JVM with the parameter -Djava.rmi.server.hostname=<client address> or uncomment the following line.
        //System.setProperty("java.rmi.server.hostname", "<client address>");

        // Configure log4j file if none is already set
        if (System.getProperty("log4j.configurationFile") == null)
            System.setProperty("log4j.configurationFile", "log4j2-tui.xml");

        // Make System.in interruptible
        System.setIn(InterruptibleInputStream.wrap(System.in, Thread.ofPlatform()
                .name("stdin-read-th")
                .factory()));

        new TuiRenderer(
                TuiPrintStream.installToStdOut(),
                System.console() != null
                        ? System.console().reader()
                        : new InputStreamReader(System.in, Charset.defaultCharset()),
                TuiPrompts.initialPrompt(),
                TuiPrompts.initialScene());
    }
}
