package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

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
        callback.leftPlayer.setShowShips(true);
        callback.rightPlayer.setShowShips(true);
        if (callback.leftPlayer.getWorld().isDead())
            callback.rightScore++;
        else if (callback.rightPlayer.getWorld().isDead())
            callback.leftScore++;
        text = (callback.leftPlayer.getWorld().isDead() ? "Second" : "First") + " player won!\n" + callback.leftScore + " : " + callback.rightScore;
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {
        getBatch().begin();
        font.draw(getBatch(), text, callback.getWidth() / 2, callback.getHeight() / 2);
        getBatch().end();
    }

    @Override
    public int stepEnd() {
        return StepsDirector.STEP_BEGINNING;
    }

    @Override
    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        callback.nextStep();
        return true;
    }
}
