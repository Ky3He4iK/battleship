package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.logic.GameConfig;

public class StepPlacementRight extends BaseStep {
    StepPlacementRight(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
    }

    @Override
    public void stepBegin() {
        if (callback.config.getGameType() != GameConfig.GameType.LOCAL_2P) {
            callback.nextStep();
            return;
        }
        callback.setChildrenEnabled(false, false, true, false, true, true);
        callback.shipPlacer.restart(callback.middleGap);
        callback.shipPlacer.start(callback.leftPlayer);
        callback.rightPlayer.setPosition(callback.redundantX + callback.sideWidth, callback.redundantY + callback.footerHeight);
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {

    }
}
