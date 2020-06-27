package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

/**
 * Экран, на котором будут настраиваться глобальные параметры: язык и т.д.
 * todo: do, copy StepConfigure
 */
public class ScreenSettings extends BaseScreen {
    ScreenSettings(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenSettings";
    }
}
