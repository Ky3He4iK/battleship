package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;

public class StepBeginning extends BaseStep {
    @NotNull
    private Label startLabel;
    @NotNull
    private Label joinLabel;

    StepBeginning(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        startLabel = new Label("Create new game", new Label.LabelStyle(callback.font, callback.font.getColor()));
        joinLabel = new Label("Join to online game", new Label.LabelStyle(callback.font, callback.font.getColor()));
    }

    @Override
    public void stepBegin() {
        callback.setTurn(StepsDirector.TURN_LEFT);
        callback.setChildrenEnabled(false, false);
        callback.readyCnt = 0;
        startLabel.setAlignment(Align.center);
        startLabel.setPosition((callback.getWidth() - startLabel.getWidth()) / 2, (callback.getHeight() / 2 * 3 - startLabel.getHeight()) / 2);
        joinLabel.setAlignment(Align.center);
        joinLabel.setPosition((callback.getWidth() - joinLabel.getWidth()) / 2, (callback.getHeight() / 2 - joinLabel.getHeight()) / 2);
        callback.p1Ready = false;
        callback.p2Ready = false;
        callback.isP2 = false;
        callback.gotConfig = false;
        callback.readyCnt = 0;
    }

    @Override
    public void act() {
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        batch.begin();
        startLabel.draw(batch, 1);
        joinLabel.draw(batch, 1);
        batch.end();
    }

    @Override
    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        if (y > Gdx.graphics.getHeight() / 2f) {
            callback.nextStep();
        } else {
            callback.gotConfig = false;
            callback.setStep(StepsDirector.STEP_CONNECTING_CLIENT);
        }
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Beginning";
    }
}
