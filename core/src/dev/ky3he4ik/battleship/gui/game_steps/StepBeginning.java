package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import org.jetbrains.annotations.NotNull;

public class StepBeginning extends BaseStep {
    StepBeginning(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
    }

    @Override
    public void stepBegin() {
        callback.setTurn(StepsDirector.TURN_LEFT);
        callback.setChildrenEnabled(false, false);
        callback.readyCnt = 0;
    }

    @Override
    public void act() {
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        batch.begin();
        font.draw(batch, "Press any key", callback.getWidth() / 2, callback.getHeight() / 2);
        batch.end();
    }

    @Override
    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        callback.nextStep();
        return true;
    }
}
