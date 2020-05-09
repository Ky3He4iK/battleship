package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.config.ConfigGroup;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class StepConfigure extends BaseStep {
    private ConfigGroup configGroup;

    StepConfigure(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        configGroup = new ConfigGroup(this);
        configGroup.setVisible(false);
        //todo: implement
    }

    @Override
    public void stepBegin() {
        callback.nextStep();
        configGroup.setVisible(true);
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {

    }

    @Override
    public int stepEnd() {
        configGroup.setVisible(false);

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

    @Override
    public void resize() {
        configGroup.setSize(callback.getWidth(), callback.getHeight());
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
}
