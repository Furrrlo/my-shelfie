package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.GameView;

import java.util.List;

class TuiZoomedBoardPrinter extends TuiZoomedScene {
    private final GameView game;

    public TuiZoomedBoardPrinter(GameView game) {
        this.game = game;
    }

    @Override
    public void render(TuiPrintStream out) {
        super.render(out);

        new TuiDetailedBoardPrinter(game.getBoard(),
                List.of(game.getCommonGoals().get(0).getType(),
                        game.getCommonGoals().get(1).getType()))
                .print(out);
    }
}
