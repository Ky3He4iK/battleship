package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.logic.inet.MultiplayerInet;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepConnectingClient extends BaseStep {
    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    StepConnectingClient(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);
    }

    @Override
    public void stepBegin() {
        cTime = 0;
    }

    @Override
    public void act() {
        if (callback.gotConfig)
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
        return "StepConnectingClient";
    }

    @Override
    public int stepEnd() {
        callback.resize();
        callback.leftPlayer.getWorld().reset(callback.config.getWidth(), callback.config.getHeight());
        callback.rightPlayer.getWorld().reset(callback.config.getWidth(), callback.config.getHeight());
        callback.leftPlayer.init();
        callback.rightPlayer.init();
        return StepsDirector.STEP_PLACEMENT_R;
    }
}
