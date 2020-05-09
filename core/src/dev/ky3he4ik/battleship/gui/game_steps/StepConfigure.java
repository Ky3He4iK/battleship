package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.utils.viewport.Viewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.config.ConfigGroup;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class StepConfigure extends BaseStep {
    private ConfigGroup configGroup;

    StepConfigure(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        configGroup = new ConfigGroup(this);
//        configGroup.setVisible(false);
    }

    @Override
    public void stepBegin() {
//        callback.nextStep();
        configGroup.init();
    }

    @Override
    public Viewport getViewport() {
        return callback.getViewport();
    }

    @Override
    public void act() {
        configGroup.act();
    }

    @Override
    public void draw() {
        configGroup.draw();
    }

    @Override
    public int stepEnd() {
        configGroup.finish();
        configGroup.getConfig().duplicate(callback.config);

        callback.leftPlayer.getWorld().reset(callback.config.getWidth(), callback.config.getHeight());
        Communication communication = callback.leftPlayer.getCommunication();
        if (communication != null)
            communication.restart();
        callback.leftPlayer.init();

        callback.rightPlayer.getWorld().reset(callback.config.getWidth(), callback.config.getHeight());
        communication = callback.rightPlayer.getCommunication();
        if (communication != null)
            communication.restart();
        callback.rightPlayer.init();

        if (callback.config.getGameType() == GameConfig.GameType.AI_VS_AI)
            return StepsDirector.STEP_GAME;
        else
            return super.stepEnd();
    }

    @NotNull
    @Override
    public String getName() {
        return "Configure";
    }

    @NotNull
    public GameConfig getConfig() {
        return callback.config;
    }

    @NotNull
    public StepsDirector getCallback() {
        return callback;
    }

    public void ConfigIsDone() {
        callback.nextStep();
    }
}
