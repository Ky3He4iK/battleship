package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.scene2d.GetInfo;

public class StepGetInfo extends BaseStep {
    private GetInfo getInfo;

    StepGetInfo(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        getInfo = new GetInfo(this);
    }

    @Override
    public void stepBegin() {
        getInfo.init();
    }

    @Override
    public void act() {
        getInfo.act();
    }

    @Override
    public void draw() {
        getInfo.draw();
    }

    @Override
    public int stepEnd() {
        if (callback.isInetClient)
            return StepsDirector.STEP_CONNECTING_CLIENT;
        else
            return StepsDirector.STEP_PLACEMENT_L;
    }

    @NotNull
    @Override
    public String getName() {
        return "Get info";
    }

    public void onOk() {
        callback.nextStep();
    }
}
