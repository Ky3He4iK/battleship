package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.jetbrains.annotations.NotNull;

public abstract class BaseStep extends Stage {
    final int stepId;
    @NotNull
    protected StepsDirector callback;

    @NotNull
    private ShapeRenderer renderer;

    BaseStep(@NotNull StepsDirector callback, int stepId) {
        this.callback = callback;
        this.stepId = stepId;
        renderer = new ShapeRenderer();
    }

    abstract public void stepBegin();

    public int stepEnd() {
        return stepId + 1;
    }

    public void act() {
    }

    public void draw() {
        super.draw();
    }

    @NotNull
    public StepsDirector getCallback() {
        return callback;
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
    }

    public void turnFinished(int playerId, int i, int j) {
    }

    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        return false;
    }

    boolean relayKeyDown(InputEvent event, int keycode) {
        return false;
    }

    public void resize() {
    }

    @NotNull
    abstract public String getName();
}
