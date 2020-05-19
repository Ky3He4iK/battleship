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
        callback.setChildrenEnabled(false, false);
    }

    @Override
    public void stepBegin() {
        cTime = 0;
    }

    @Override
    public void act() {
        if (callback.config.getGameType() != GameConfig.GameType.GLOBAL_INET || callback.readyCnt == 2
                || ((callback.leftPlayer.getCommunication() == null || callback.leftPlayer.getCommunication().isConnected())
                && (callback.rightPlayer.getCommunication() == null || callback.rightPlayer.getCommunication().isConnected())))
            callback.nextStep();
    }

    @Override
    public void draw() {
        cTime += Gdx.graphics.getDeltaTime();
        Batch batch = getBatch();
        batch.begin();
        TextureRegion frame = animation.getKeyFrame(cTime);
        batch.draw(frame, callback.getWidth() / 2 - callback.middleGap * .45f, callback.getHeight() / 2 - callback.middleGap * .45f, callback.middleGap * .9f, callback.middleGap * .9f);
        batch.end();
    }


    @NotNull
    @Override
    public String getName() {
        return "StepConnecting";
    }

    @Override
    public int stepEnd() {
        return StepsDirector.STEP_PLACEMENT_L;
    }
}
