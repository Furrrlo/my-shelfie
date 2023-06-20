package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Locale;

/**
 * Abstract scene that zoom in the console before rendering
 */
@SuppressWarnings("SameParameterValue")
abstract class TuiZoomedScene implements TuiScene {

    private static final boolean IS_ROBOT_SUPPORTED;
    private static final boolean IS_MAC_OS;
    static {
        var os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        IS_MAC_OS = os.contains("mac") || os.contains("darwin");

        boolean isRobotSupported = false;
        try {
            new Robot();
            isRobotSupported = true;
        } catch (AWTException ignored) {
            // Not supported
        }
        IS_ROBOT_SUPPORTED = isRobotSupported;
    }

    public static boolean isSupported() {
        return IS_ROBOT_SUPPORTED;
    }

    private final Robot r;
    private boolean hasZoomed;

    public TuiZoomedScene() {
        try {
            r = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void render(TuiPrintStream out) {
        if (!hasZoomed) {
            zoomIn(10);
            hasZoomed = true;
        }
    }

    private void zoomIn(int n) {
        r.keyPress(IS_MAC_OS ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
        for (int i = 0; i < n; i++) {
            r.keyPress(KeyEvent.VK_MINUS);
            r.keyRelease(KeyEvent.VK_MINUS);
        }
        r.keyRelease(IS_MAC_OS ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        zoomOut(10);
        hasZoomed = false;
    }

    private void zoomOut(int n) {
        r.keyPress(IS_MAC_OS ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
        /*
         * for (int i = 0; i < n; i++) {
         * r.keyPress(KeyEvent.VK_PLUS);
         * r.keyRelease(KeyEvent.VK_PLUS);
         * }
         */
        r.keyPress(KeyEvent.VK_NUMPAD0);
        r.keyRelease(KeyEvent.VK_NUMPAD0);
        r.keyRelease(IS_MAC_OS ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
    }
}
