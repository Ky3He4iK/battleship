package dev.ky3he4ik.battleship.gui.game_steps.config;


import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.StepConfigure;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class ConfigGroup extends Group {
    @NotNull
    private StepConfigure callback;

    @NotNull
    private GameConfig config;

    public ConfigGroup(@NotNull StepConfigure callback) {
        this.callback = callback;
        config = GameConfig.getSampleConfigEast();
    }
}
