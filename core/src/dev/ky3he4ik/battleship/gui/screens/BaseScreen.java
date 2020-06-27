package dev.ky3he4ik.battleship.gui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.logic.StaticContent;

public abstract class BaseScreen extends Stage {
    final int screenId;

    @NotNull
    final protected ScreensDirector callback;

    @NotNull
    final protected StaticContent staticContent;

    BaseScreen(@NotNull final ScreensDirector callback, int screenId) {
        this.callback = callback;
        this.screenId = screenId;
        staticContent = StaticContent.getInstance();
    }

    abstract public void stepBegin();

    public int stepEnd() {
        return screenId + 1;
    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        super.draw();
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
