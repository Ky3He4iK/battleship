package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.logic.GameConfig;

public class StepPlacementLeft extends  BaseStep {
    StepPlacementLeft(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
    }

    @Override
    public void stepBegin() {
        callback.setChildrenEnabled(true, false, false, false, true, true);
        callback.getShipPlacer().restart(callback.middleGap);
        callback.getShipPlacer().start(callback.getLeftPlayer());
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {

    }

    @Override
    public int stepEnd() {
        if (callback.getConfig().getGameType() != GameConfig.GameType.LOCAL_2P)
            return StepsDirector.STEP_GAME;
        return super.stepEnd();
    }
}
