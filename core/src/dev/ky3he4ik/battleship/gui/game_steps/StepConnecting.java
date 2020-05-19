package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepConnecting extends BaseStep {
    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    StepConnecting(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);
    }

    @Override
    public void stepBegin() {
        callback.setChildrenEnabled(false, false);
        cTime = 0;
    }

    @Override
    public void act() {
        if (callback.readyCnt == 2)
            callback.nextStep();
    }

    @Override
    public void draw() {
        cTime += Gdx.graphics.getDeltaTime();
        Batch batch = getBatch();
        batch.begin();
        TextureRegion frame = animation.getKeyFrame(cTime);
        batch.draw(frame, callback.getWidth() / 2 - callback.middleGap * .5f, callback.getHeight() / 2 - callback.middleGap * .5f, callback.middleGap, callback.middleGap);
        batch.end();
    }


    @NotNull
    @Override
    public String getName() {
        return "StepConnecting";
    }

    @Override
    public int stepEnd() {
        return StepsDirector.STEP_GAME;
    }
}
