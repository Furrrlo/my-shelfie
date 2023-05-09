package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.GameView;

import java.util.List;

class TuiZoomedShelfiePrinter extends TuiZoomedScene {
    private final GameView game;

    public TuiZoomedShelfiePrinter(GameView game) {
        this.game = game;
    }

    @Override
    public void render(TuiPrintStream out) {
        super.render(out);

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
}
