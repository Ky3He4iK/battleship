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
        textLabel = new Label(text, new Label.LabelStyle(callback.font, callback.font.getColor()));
    }

    @Override
    public void stepBegin() {
        callback.leftPlayer.setShowShips(true);
        callback.rightPlayer.setShowShips(true);
        callback.leftPlayer.setTouchable(Touchable.disabled);
        callback.rightPlayer.setTouchable(Touchable.disabled);
        if (callback.leftPlayer.getWorld().isDead()) {
            callback.rightScore++;
            text = "Second" + " player won!\n";
        } else if (callback.rightPlayer.getWorld().isDead()) {
            callback.leftScore++;
            text = "First" + " player won!\n";
        } else
            text = "There are no winner\n";
        text = text + callback.leftScore + " : " + callback.rightScore;
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

    @NotNull
    @Override
    public String getName() {
        return "Aftermath";
    }

    @Override
    public void resize() {
        super.resize();
        textLabel.setPosition((callback.getWidth() - textLabel.getWidth()) / 2, (callback.getHeight() - textLabel.getHeight()) / 2);
    }
}
