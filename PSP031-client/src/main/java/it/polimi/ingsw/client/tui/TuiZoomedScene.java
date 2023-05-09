package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.awt.*;
import java.awt.event.KeyEvent;

abstract class TuiZoomedScene implements TuiScene {

    private boolean hasZoomed;

    @Override
    @MustBeInvokedByOverriders
    public void render(TuiPrintStream out) {
        if (!hasZoomed) {
            zoomIn(11);
            hasZoomed = true;
        }
    }

    public static void zoomIn(int n) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            //r.keyPress(KeyEvent.VK_META);
            for (int i = 0; i < n; i++) {
                r.keyPress(KeyEvent.VK_PLUS);
                //r.keyPress(KeyEvent.VK_EQUALS);
                r.keyRelease(KeyEvent.VK_PLUS);
                //r.keyRelease(KeyEvent.VK_EQUALS);
            }

            r.keyRelease(KeyEvent.VK_CONTROL);
            //r.keyRelease(KeyEvent.VK_META);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        zoomOut(11);
        hasZoomed = false;
    }

    public static void zoomOut(int n) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            //r.keyPress(KeyEvent.VK_META);
            for (int i = 0; i < n; i++) {
                r.keyPress(KeyEvent.VK_MINUS);
                r.keyRelease(KeyEvent.VK_MINUS);
            }
            r.keyRelease(KeyEvent.VK_CONTROL);
            //r.keyRelease(KeyEvent.VK_META);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}
