package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.CommonGoalView;
import it.polimi.ingsw.model.PersonalGoalView;

import javafx.scene.layout.Pane;

/**
 * Component representing a goal pattern.
 * <p>
 * Colored tiles are printed on the local player's shelfie
 */
class GoalPatternComponent extends Pane {
    public ColorTileComponent t0x0;
    public ColorTileComponent t0x1;
    public ColorTileComponent t0x2;
    public ColorTileComponent t0x3;
    public ColorTileComponent t0x4;
    public ColorTileComponent t1x0;
    public ColorTileComponent t1x1;
    public ColorTileComponent t1x2;
    public ColorTileComponent t1x3;
    public ColorTileComponent t1x4;
    public ColorTileComponent t2x0;
    public ColorTileComponent t2x2;
    public ColorTileComponent t2x1;
    public ColorTileComponent t2x3;
    public ColorTileComponent t2x4;
    public ColorTileComponent t3x0;
    public ColorTileComponent t3x1;
    public ColorTileComponent t3x2;
    public ColorTileComponent t3x3;
    public ColorTileComponent t3x4;
    public ColorTileComponent t4x0;
    public ColorTileComponent t4x1;
    public ColorTileComponent t4x2;
    public ColorTileComponent t4x3;
    public ColorTileComponent t4x4;
    public ColorTileComponent t5x0;
    public ColorTileComponent t5x1;
    public ColorTileComponent t5x2;
    public ColorTileComponent t5x3;
    public ColorTileComponent t5x4;

    public GoalPatternComponent(FxResourcesLoader resources, PersonalGoalView personalGoal) {
        getChildren().add(t0x0 = new ColorTileComponent(resources, personalGoal.get(0, 0)));
        getChildren().add(t0x1 = new ColorTileComponent(resources, personalGoal.get(0, 1)));
        getChildren().add(t0x2 = new ColorTileComponent(resources, personalGoal.get(0, 2)));
        getChildren().add(t0x3 = new ColorTileComponent(resources, personalGoal.get(0, 3)));
        getChildren().add(t0x4 = new ColorTileComponent(resources, personalGoal.get(0, 4)));
        getChildren().add(t1x0 = new ColorTileComponent(resources, personalGoal.get(1, 0)));
        getChildren().add(t1x1 = new ColorTileComponent(resources, personalGoal.get(1, 1)));
        getChildren().add(t1x2 = new ColorTileComponent(resources, personalGoal.get(1, 2)));
        getChildren().add(t1x3 = new ColorTileComponent(resources, personalGoal.get(1, 3)));
        getChildren().add(t1x4 = new ColorTileComponent(resources, personalGoal.get(1, 4)));
        getChildren().add(t2x0 = new ColorTileComponent(resources, personalGoal.get(2, 0)));
        getChildren().add(t2x2 = new ColorTileComponent(resources, personalGoal.get(2, 2)));
        getChildren().add(t2x1 = new ColorTileComponent(resources, personalGoal.get(2, 1)));
        getChildren().add(t2x3 = new ColorTileComponent(resources, personalGoal.get(2, 3)));
        getChildren().add(t2x4 = new ColorTileComponent(resources, personalGoal.get(2, 4)));
        getChildren().add(t3x0 = new ColorTileComponent(resources, personalGoal.get(3, 0)));
        getChildren().add(t3x1 = new ColorTileComponent(resources, personalGoal.get(3, 1)));
        getChildren().add(t3x2 = new ColorTileComponent(resources, personalGoal.get(3, 2)));
        getChildren().add(t3x3 = new ColorTileComponent(resources, personalGoal.get(3, 3)));
        getChildren().add(t3x4 = new ColorTileComponent(resources, personalGoal.get(3, 4)));
        getChildren().add(t4x0 = new ColorTileComponent(resources, personalGoal.get(4, 0)));
        getChildren().add(t4x1 = new ColorTileComponent(resources, personalGoal.get(4, 1)));
        getChildren().add(t4x2 = new ColorTileComponent(resources, personalGoal.get(4, 2)));
        getChildren().add(t4x3 = new ColorTileComponent(resources, personalGoal.get(4, 3)));
        getChildren().add(t4x4 = new ColorTileComponent(resources, personalGoal.get(4, 4)));
        getChildren().add(t5x0 = new ColorTileComponent(resources, personalGoal.get(5, 0)));
        getChildren().add(t5x1 = new ColorTileComponent(resources, personalGoal.get(5, 1)));
        getChildren().add(t5x2 = new ColorTileComponent(resources, personalGoal.get(5, 2)));
        getChildren().add(t5x3 = new ColorTileComponent(resources, personalGoal.get(5, 3)));
        getChildren().add(t5x4 = new ColorTileComponent(resources, personalGoal.get(5, 4)));
    }

    public GoalPatternComponent(FxResourcesLoader resources, CommonGoalView commonGoal) {
        var shelfie = commonGoal.getType().getExample();
        getChildren().add(t0x0 = new ColorTileComponent(resources, shelfie.get(0, 0)));
        getChildren().add(t0x1 = new ColorTileComponent(resources, shelfie.get(0, 1)));
        getChildren().add(t0x2 = new ColorTileComponent(resources, shelfie.get(0, 2)));
        getChildren().add(t0x3 = new ColorTileComponent(resources, shelfie.get(0, 3)));
        getChildren().add(t0x4 = new ColorTileComponent(resources, shelfie.get(0, 4)));
        getChildren().add(t1x0 = new ColorTileComponent(resources, shelfie.get(1, 0)));
        getChildren().add(t1x1 = new ColorTileComponent(resources, shelfie.get(1, 1)));
        getChildren().add(t1x2 = new ColorTileComponent(resources, shelfie.get(1, 2)));
        getChildren().add(t1x3 = new ColorTileComponent(resources, shelfie.get(1, 3)));
        getChildren().add(t1x4 = new ColorTileComponent(resources, shelfie.get(1, 4)));
        getChildren().add(t2x0 = new ColorTileComponent(resources, shelfie.get(2, 0)));
        getChildren().add(t2x2 = new ColorTileComponent(resources, shelfie.get(2, 2)));
        getChildren().add(t2x1 = new ColorTileComponent(resources, shelfie.get(2, 1)));
        getChildren().add(t2x3 = new ColorTileComponent(resources, shelfie.get(2, 3)));
        getChildren().add(t2x4 = new ColorTileComponent(resources, shelfie.get(2, 4)));
        getChildren().add(t3x0 = new ColorTileComponent(resources, shelfie.get(3, 0)));
        getChildren().add(t3x1 = new ColorTileComponent(resources, shelfie.get(3, 1)));
        getChildren().add(t3x2 = new ColorTileComponent(resources, shelfie.get(3, 2)));
        getChildren().add(t3x3 = new ColorTileComponent(resources, shelfie.get(3, 3)));
        getChildren().add(t3x4 = new ColorTileComponent(resources, shelfie.get(3, 4)));
        getChildren().add(t4x0 = new ColorTileComponent(resources, shelfie.get(4, 0)));
        getChildren().add(t4x1 = new ColorTileComponent(resources, shelfie.get(4, 1)));
        getChildren().add(t4x2 = new ColorTileComponent(resources, shelfie.get(4, 2)));
        getChildren().add(t4x3 = new ColorTileComponent(resources, shelfie.get(4, 3)));
        getChildren().add(t4x4 = new ColorTileComponent(resources, shelfie.get(4, 4)));
        getChildren().add(t5x0 = new ColorTileComponent(resources, shelfie.get(5, 0)));
        getChildren().add(t5x1 = new ColorTileComponent(resources, shelfie.get(5, 1)));
        getChildren().add(t5x2 = new ColorTileComponent(resources, shelfie.get(5, 2)));
        getChildren().add(t5x3 = new ColorTileComponent(resources, shelfie.get(5, 3)));
        getChildren().add(t5x4 = new ColorTileComponent(resources, shelfie.get(5, 4)));
    }

    protected void layoutChildren() {
        final double _widthScale = getWidth() / 1218d;
        final double _heightScale = getHeight() / 1235d;
        final double scale = Math.min(_widthScale, _heightScale);

        final double widthScale = scale;
        final double heightScale = scale;
        t0x0.resizeRelocate(146.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x1.resizeRelocate(342.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x2.resizeRelocate(537.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x3.resizeRelocate(732.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t0x4.resizeRelocate(930.0 * widthScale, 89.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x0.resizeRelocate(148.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x1.resizeRelocate(342.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x2.resizeRelocate(537.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x3.resizeRelocate(730.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t1x4.resizeRelocate(928.0 * widthScale, 259.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x0.resizeRelocate(150.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x1.resizeRelocate(342.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x2.resizeRelocate(537.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x3.resizeRelocate(730.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t2x4.resizeRelocate(924.0 * widthScale, 426.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x0.resizeRelocate(154.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x1.resizeRelocate(344.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x2.resizeRelocate(537.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x3.resizeRelocate(730.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t3x4.resizeRelocate(922.0 * widthScale, 592.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x0.resizeRelocate(156.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x1.resizeRelocate(346.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x2.resizeRelocate(537.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x3.resizeRelocate(730.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t4x4.resizeRelocate(915.0 * widthScale, 758.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x0.resizeRelocate(156.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x1.resizeRelocate(346.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x2.resizeRelocate(537.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x3.resizeRelocate(730.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
        t5x4.resizeRelocate(915.0 * widthScale, 917.0 * heightScale, 144.0 * widthScale, 144.0 * heightScale);
    }
}
