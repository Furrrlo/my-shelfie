package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.CommonGoalView;
import it.polimi.ingsw.model.PersonalGoalView;
import it.polimi.ingsw.model.PlayerView;

/**
 * Component containing the shelfie and goal patterns of the local player.
 *
 * @see PlayerShelfieComponent
 * @see GoalPatternComponent
 */
class ThePlayerShelfieComponent extends PlayerShelfieComponent {

    private final GoalPatternComponent personalGoalPattern;
    private final GoalPatternComponent commonGoalPattern1;
    private final GoalPatternComponent commonGoalPattern2;

    public ThePlayerShelfieComponent(FxResourcesLoader resources,
                                     PlayerView player,
                                     PersonalGoalView personalGoal,
                                     CommonGoalView cg1,
                                     CommonGoalView cg2) {
        super(resources, player, false, false);
        getChildren().add(this.personalGoalPattern = new GoalPatternComponent(resources, personalGoal));
        this.personalGoalPattern.setVisible(false);
        getChildren().add(this.commonGoalPattern1 = new GoalPatternComponent(resources, cg1));
        getChildren().add(this.commonGoalPattern2 = new GoalPatternComponent(resources, cg2));
        this.commonGoalPattern1.setVisible(false);
        this.commonGoalPattern2.setVisible(false);

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double scale = Math.min(getWidth() / 180d, getHeight() / 194d);

        double shelfieWidth = 180d * scale;
        double shelfieOffsetY = Math.min(20, 14 * scale);

        this.personalGoalPattern.resizeRelocate((getWidth() - shelfieWidth) / 2, shelfieOffsetY, shelfieWidth,
                getHeight() - shelfieOffsetY);
        this.commonGoalPattern1.resizeRelocate((getWidth() - shelfieWidth) / 2, shelfieOffsetY, shelfieWidth,
                getHeight() - shelfieOffsetY);
        this.commonGoalPattern2.resizeRelocate((getWidth() - shelfieWidth) / 2, shelfieOffsetY, shelfieWidth,
                getHeight() - shelfieOffsetY);
    }

    public void setPersonalGoalVisible(boolean visible) {
        this.personalGoalPattern.setVisible(visible);
    }

    public boolean getPersonalGoalVisible() {
        return this.personalGoalPattern.isVisible();
    }

    public void setCommonGoal1Visible(boolean visible) {
        this.commonGoalPattern1.setVisible(visible);
    }

    public boolean getCommonGoal1Visible() {
        return this.commonGoalPattern1.isVisible();
    }

    public void setCommonGoal2Visible(boolean visible) {
        this.commonGoalPattern2.setVisible(visible);
    }

    public boolean getCommonGoal2Visible() {
        return this.commonGoalPattern2.isVisible();
    }

}
