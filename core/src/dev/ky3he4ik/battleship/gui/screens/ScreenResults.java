package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

public class ScreenResults extends BaseScreen {
    ScreenResults(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenResults";
    }
}
