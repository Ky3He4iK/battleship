package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.graphics.g2d.Batch;

import org.jetbrains.annotations.NotNull;

public class StepBeginning extends BaseStep {
    StepBeginning(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
    }

    @Override
    public void stepBegin() {
        callback.setTurn(StepsDirector.TURN_LEFT);
        callback.setChildrenEnabled(false, false);
    }

    @Override
    public void act() {
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        batch.begin();
        font.draw(batch, "Press any key", getWidth() / 2, getHeight() / 2);
        batch.end();
    }
}
