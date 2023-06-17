package it.polimi.ingsw.client.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ScoringTokenComponent extends ImageButton {
    private final ObjectProperty<Integer> score = new SimpleObjectProperty<>(this, "score");

    public ScoringTokenComponent(FxResourcesLoader resources) {
        score.set(score.get());
        imageProperty().bind(scoreProperty().map(ignored -> {
            var currScore = score.get();
            return resources.loadImage(switch (currScore) {
                case 2 -> "assets/scoring tokens/scoring_2.jpg";
                case 4 -> "assets/scoring tokens/scoring_4.jpg";
                case 6 -> "assets/scoring tokens/scoring_6.jpg";
                case 8 -> "assets/scoring tokens/scoring_8.jpg";
                case 0 -> "assets/scoring tokens/scoring_back_EMPTY.jpg";
                case 1 -> "assets/scoring tokens/end game.jpg";
                default -> throw new IllegalStateException("Unexpected value: " + score.get());
            });
        }));
    }

    public Integer getScore() {
        return score.get();
    }

    public ObjectProperty<Integer> scoreProperty() {
        return score;
    }

    public void setScore(Integer score) {
        this.score.set(score);
    }
}
