package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

public class StepAftermath extends BaseStep {
    @NotNull
    private String text;

    StepAftermath(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        text = "";
    }

    @Override
    public void stepBegin() {
        text = (callback.getLeftPlayer().getWorld().isDead() ? "Second" : "First") + " player won!\n" + callback.leftScore + " : " + callback.rightScore;
        callback.getLeftPlayer().setShowShips(true);
        callback.getRightPlayer().setShowShips(true);
        if (callback.getLeftPlayer().getWorld().isDead())
            callback.rightScore++;
        else if (callback.getRightPlayer().getWorld().isDead())
            callback.leftScore++;
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {
        getBatch().begin();
        font.draw(getBatch(), text, getWidth() / 2, getHeight() / 2);
        getBatch().end();
    }

    @Override
    public int stepEnd() {
        return StepsDirector.STEP_BEGINNING;
    }
}
