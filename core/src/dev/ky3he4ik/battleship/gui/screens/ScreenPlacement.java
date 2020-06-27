package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

/**
 * Расстановка
 */
public class ScreenPlacement extends BaseScreen {
    ScreenPlacement(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenPlacement";
    }
}
