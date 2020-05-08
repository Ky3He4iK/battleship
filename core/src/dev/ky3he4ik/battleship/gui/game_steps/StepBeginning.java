package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;

public class StepBeginning extends BaseStep {
    @NotNull
    private Label textLabel;

    StepBeginning(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        textLabel = new Label("Press any key", new Label.LabelStyle(callback.font, callback.font.getColor()));
    }

    @Override
    public void stepBegin() {
        callback.setTurn(StepsDirector.TURN_LEFT);
        callback.setChildrenEnabled(false, false);
        callback.readyCnt = 0;
        textLabel.setAlignment(Align.center);
        textLabel.setPosition((callback.getWidth() - textLabel.getWidth()) / 2, (callback.getHeight() - textLabel.getHeight()) / 2);
    }

    @Override
    public void act() {
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        batch.begin();
        textLabel.draw(batch, 1);
        batch.end();
    }

    @Override
    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        callback.nextStep();
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Beginning";
    }
}
