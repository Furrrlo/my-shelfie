package it.polimi.ingsw.client.javafx;

import javafx.beans.value.ObservableValue;

import java.util.Objects;

/**
 * Component representing the score tokens of common goals
 */
class ScoringTokenComponent extends ImageButton {

    public ScoringTokenComponent(FxResourcesLoader resources, ObservableValue<Integer> score) {
        visibleProperty().bind(imageProperty().map(Objects::nonNull));
        imageProperty().bind(score.map(ignored -> {
            int currScore = score.getValue();
            if (currScore == 0)
                return null;
            return resources.loadImage(switch (currScore) {
                case 2 -> "assets/scoring tokens/scoring_2.jpg";
                case 4 -> "assets/scoring tokens/scoring_4.jpg";
                case 6 -> "assets/scoring tokens/scoring_6.jpg";
                case 8 -> "assets/scoring tokens/scoring_8.jpg";
                default -> throw new IllegalStateException("Unexpected value: " + currScore);
            });
        }));
    }
}
