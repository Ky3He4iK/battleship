package dev.ky3he4ik.battleship.gui.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

public interface RelayTouchListener {
    boolean relayTouch(InputEvent event, float x, float y, int pointer, int button);

    boolean relayKeyDown(InputEvent event, int keycode);
}
