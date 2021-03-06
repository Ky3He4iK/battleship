package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepPlacementLeft extends BaseStep {
    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    StepPlacementLeft(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);
    }

    @Override
    public void stepBegin() {
        callback.p1Ready = false;
        callback.p2Ready = false;
        callback.setChildrenEnabled(true, false, false, false, true, true);
        if (callback.config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            callback.shipPlacer.setVisible(false);
            callback.leftPlayer.setVisible(false);
            cTime = 0;
        } else {
            callback.shipPlacer.restart(callback.middleGap, callback.config.getShips());
            callback.shipPlacer.start(callback.leftPlayer);
        }
        callback.leftPlayer.setPlaceShips();
        callback.rightPlayer.setPlaceShips();
        if (callback.isP2) {
            callback.nextStep();
        }
    }

    @Override
    public void act() {
        if (callback.p1Ready)
            callback.nextStep();
    }

    @Override
    public void draw() {
        if (callback.config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            cTime += Gdx.graphics.getDeltaTime();
            Batch batch = getBatch();
            batch.begin();
            TextureRegion frame = animation.getKeyFrame(cTime);
            batch.draw(frame, callback.getWidth() / 2 - callback.middleGap * .45f, callback.getHeight() / 2 - callback.middleGap * .45f, callback.middleGap * .9f, callback.middleGap * .9f);
            batch.end();
        }
    }

    @Override
    public int stepEnd() {
        callback.leftPlayer.setShowShips((!callback.isP2 && callback.config.getGameType() != GameConfig.GameType.LOCAL_2P) || Constants.DEBUG_MODE);
        return super.stepEnd();
    }

    @NotNull
    @Override
    public String getName() {
        return "P1 placement";
    }
}
