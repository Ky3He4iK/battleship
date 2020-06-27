package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

/**
 * Выбор режима игры (классический/с движениями, против ИИ, онлайн, турнир и т.д.)
 * todo: do
 */
public class ScreenChoose extends BaseScreen {
    ScreenChoose(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {

    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenChoose";
    }
}
