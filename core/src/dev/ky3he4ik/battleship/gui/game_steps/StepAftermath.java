package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;

public class StepAftermath extends BaseStep {
    @NotNull
    private String text;

    @NotNull
    private Label textLabel;

    private long startTime;

    StepAftermath(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        text = "";
        textLabel = new Label(text, new Label.LabelStyle(font, font.getColor()));
    }

    @Override
    public void stepBegin() {
        callback.leftPlayer.setShowShips(true);
        callback.rightPlayer.setShowShips(true);
        callback.leftPlayer.setTouchable(Touchable.disabled);
        callback.rightPlayer.setTouchable(Touchable.disabled);
        if (callback.leftPlayer.getWorld().isDead())
            callback.rightScore++;
        else if (callback.rightPlayer.getWorld().isDead())
            callback.leftScore++;
        text = (callback.leftPlayer.getWorld().isDead() ? "Second" : "First") + " player won!\n" + callback.leftScore + " : " + callback.rightScore;
        textLabel.setText(text);
        textLabel.setAlignment(Align.center);
        textLabel.setPosition((callback.getWidth() - textLabel.getWidth()) / 2, (callback.getHeight() - textLabel.getHeight()) / 2);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {
        getBatch().begin();
        textLabel.draw(getBatch(), 1);
//        font.draw(getBatch(), text, callback.getWidth() / 2, callback.getHeight() / 2);
        getBatch().end();
    }

    @Override
    public int stepEnd() {
        return StepsDirector.STEP_BEGINNING;
    }

    @Override
    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        if (System.currentTimeMillis() - startTime > 3000) {
            callback.nextStep();
            return true;
        }
        return false;
    }
}
