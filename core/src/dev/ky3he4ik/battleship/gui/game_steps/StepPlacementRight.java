package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepPlacementRight extends BaseStep {
    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    StepPlacementRight(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);
    }

    @Override
    public void stepBegin() {
        if (callback.config.getGameType() == GameConfig.GameType.LOCAL_2P) {
            callback.setChildrenEnabled(false, false, true, false, true, true);
            callback.shipPlacer.restart(callback.middleGap);
            callback.shipPlacer.start(callback.leftPlayer);
            callback.rightPlayer.setPosition(callback.redundantX + callback.sideWidth, callback.redundantY + callback.footerHeight);
        } else {
            callback.shipPlacer.setVisible(false);
            cTime = 0;
        }
    }

    @Override
    public void act() {

    }

    @Override
    public void draw() {
        if (callback.config.getGameType() != GameConfig.GameType.LOCAL_2P) {
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
        callback.rightPlayer.setShowShips(Constants.DEBUG_MODE || callback.config.getGameType() == GameConfig.GameType.AI_VS_AI);
        callback.rightPlayer.start();
        return super.stepEnd();
    }
}
