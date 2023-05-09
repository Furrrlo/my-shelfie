package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.GameView;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class TuiPrinter {

    //TODO : define design of COMMON GOAL, implement them as @Unmodifiable List<String> and add them as the first two
    //  printed tiles in board, so that can be always seen during the game

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

    static class TuiBoardPrinter implements TuiScene {
        private final GameView game;

        public TuiBoardPrinter(GameView game) {
            this.game = game;
            zoomOut(11);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void render(TuiPrintStream out) {
            new TuiDetailedBoardPrinter(game.getBoard(),
                    List.of(game.getCommonGoals().get(0).getType(),
                            game.getCommonGoals().get(1).getType()))
                    .print(out);
        }

        @Override
        public void close() {
            zoomIn(11);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class TuiShelfiePrinter implements TuiScene {
        private final GameView game;

        public TuiShelfiePrinter(GameView game) {
            this.game = game;
            zoomOut(11);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void render(TuiPrintStream out) {
            new TuiDetailedBoardPrinter(game.getBoard(),
                    List.of(game.getCommonGoals().get(0).getType(), game.getCommonGoals().get(1).getType())).print(out);
            try (var ignored = out.translateCursor(0, new TuiDetailedBoardPrinter(game.getBoard(),
                    List.of(game.getCommonGoals().get(0).getType(), game.getCommonGoals().get(1).getType())).getSize().cols()
                    + 10)) {
                new TuiDetailedShelfiePrinter(game.thePlayer().getShelfie()).print(out);
                try (var ignored1 = out.translateCursor(30,
                        new TuiDetailedShelfiePrinter(game.thePlayer().getShelfie()).getSize().cols() + 10)) {
                    new TuiDetailedPersonalGoalPrinter(game.getPersonalGoal()).print(out);
                }
            }
        }

        @Override
        public void close() {
            zoomIn(11);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
