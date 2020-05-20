package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;

public class RelayTouch extends InputListener {
    @NotNull
    final private StepsDirector callback;

    public RelayTouch(@NotNull final StepsDirector gameStage) {
        callback = gameStage;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return callback.relayTouch(event, x, y, pointer, button);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        return callback.relayKeyDown(event, keycode);
    }
}
