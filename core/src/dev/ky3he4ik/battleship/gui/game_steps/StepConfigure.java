package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

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
        if (callback.getConfig().getGameType() == GameConfig.GameType.AI_VS_AI)
            return StepsDirector.STEP_GAME;
        return super.stepEnd();
    }
}
