package dev.ky3he4ik.battleship.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.utils.Constants;

/**
 * Подключение к сопернику. При офлайн игре пропускается
 */
public class ScreenConnecting extends BaseScreen {
    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    ScreenConnecting(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);
    }

    @Override
    public void stepBegin() {
        switch (staticContent.config.getGameType()) {
            case AI_VS_AI:
            case LOCAL_2P:
            case AI:
                callback.nextStep();
                return;
            default:
                cTime = 0;
                break;
        }
    }

    @Override
    public void draw() {
        super.draw();
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
        return "ScreenConnecting";
    }
}
