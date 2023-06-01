package it.polimi.ingsw.client.javafx;

import org.slf4j.bridge.SLF4JBridgeHandler;

class JfxMain {
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
