package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

/**
 * Выбор свойств игры и подключение к сопернику
 * todo: do
 */
public class ScreenSetup extends BaseScreen {
    ScreenSetup(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenSetup";
    }
}
