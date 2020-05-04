package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.jetbrains.annotations.NotNull;

public abstract class BaseStep extends Stage {
    final int stepId;
    @NotNull
    protected StepsDirector callback;

    @NotNull
    BitmapFont font;

    BaseStep(@NotNull StepsDirector callback, int stepId) {
        this.callback = callback;
        this.stepId = stepId;
        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 400f);
    }

    abstract public void stepBegin();

    public int stepEnd() {
        return stepId + 1;
    }

    abstract public void act();

    abstract public void draw();
}
