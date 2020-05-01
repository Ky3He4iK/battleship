package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import org.jetbrains.annotations.NotNull;

public class RelayTouch extends InputListener {
    @NotNull
    final private GameStage callback;

    RelayTouch(@NotNull final GameStage gameStage) {
        callback = gameStage;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return callback.touchDown();
    }
}
