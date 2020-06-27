package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

/**
 * Выбор соперника и подключение к нему. При офлайн игре пропускается
 * todo: do
 */
public class ScreenConnecting extends BaseScreen {
    ScreenConnecting(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenConnecting";
    }
}
