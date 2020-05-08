package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.jetbrains.annotations.NotNull;

public abstract class BaseStep extends Stage {
    final int stepId;
    @NotNull
    protected StepsDirector callback;

    @NotNull
    protected ShapeRenderer renderer;

    @NotNull
    BitmapFont font;

    BaseStep(@NotNull StepsDirector callback, int stepId) {
        this.callback = callback;
        this.stepId = stepId;
        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 400f);
        font.setColor(Color.BLACK);
        renderer = new ShapeRenderer();
    }

    abstract public void stepBegin();

    public int stepEnd() {
        return stepId + 1;
    }

    abstract public void act();

    public void draw() {
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        renderer.dispose();
    }

    public void turnFinished(int playerId, int i, int j) {
    }

    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        return false;
    }

    public void resize() {
    }
}
