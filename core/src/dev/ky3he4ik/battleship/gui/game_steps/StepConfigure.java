package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class StepConfigure extends BaseStep {
    StepConfigure(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        //todo: implement
    }

    @Override
    public void stepBegin() {
        callback.nextStep();
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {

    }

    @Override
    public int stepEnd() {
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
}
