package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.graphics.g2d.Sprite;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;


public class StepGame extends BaseStep {
    @NotNull
    private Sprite arrowSprite;

    private int cachedTurn;

    StepGame(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        arrowSprite = SpriteManager.getInstance().getSprite(Constants.ARROW_TURN);
        arrowSprite.setFlip(false, false);
        getBatch().setColor(0, 1, 0, 1);
        cachedTurn = StepsDirector.TURN_LEFT;
    }

    @Override
    public void stepBegin() {
        callback.getLeftPlayer().setShowShips(callback.getConfig().getGameType() != GameConfig.GameType.LOCAL_2P);
        callback.getRightPlayer().setShowShips(Constants.DEBUG_MODE || callback.getConfig().getGameType() == GameConfig.GameType.AI_VS_AI);
        callback.setChildrenEnabled(true, true);
        callback.getRightPlayer().setPosition(callback.sideWidth + callback.middleGap + callback.redundantX + callback.cellSize * callback.getConfig().getWidth(), callback.redundantY + callback.footerHeight);
    }

    @Override
    public void act() {
        if (callback.aiReadyR && callback.turn == StepsDirector.TURN_RIGHT) {
            callback.aiReadyR = false;
            boolean res = callback.getLeftPlayer().open(callback.aiXR, callback.aiYR);
            if (callback.getLeftPlayer().getWorld().isDead()) {
                callback.nextStep();
                return;
            }
            if (res)
                callback.getRightPlayer().setTurn();
            else
                callback.nextTurn();
        } else if (callback.aiReadyL && callback.turn == StepsDirector.TURN_LEFT) {
            callback.aiReadyL = false;
            boolean res = callback.getRightPlayer().open(callback.aiXL, callback.aiYL);
            if (callback.getRightPlayer().getWorld().isDead()) {
                callback.nextStep();
                return;
            }
            if (res)
                callback.getLeftPlayer().setTurn();
            else
                callback.nextTurn();
        }
        if (callback.getRightPlayer().getWorld().isDead() || callback.getLeftPlayer().getWorld().isDead())
            callback.nextStep();
    }

    @Override
    public void draw() {
        getBatch().begin();
        if (callback.getTurn() != cachedTurn) {
            if (callback.getTurn() == StepsDirector.TURN_LEFT) {
                arrowSprite.setFlip(false, false);
                getBatch().setColor(0, 1, 0, 1);
            } else {
                arrowSprite.setFlip(true, false);
                getBatch().setColor(1, 0, 0, 1);
            }
            cachedTurn = callback.getTurn();
        }
        float shift = callback.getMiddleGap() * 0.1f;
        float arrowSize = callback.getMiddleGap() - shift * 2;
        getBatch().draw(arrowSprite, callback.redundantX + callback.sideWidth + callback.getConfig().getWidth() * callback.cellSize + shift,
                callback.redundantY + callback.footerHeight + callback.getConfig().getHeight() * callback.cellSize / 2 - arrowSize / 2,
                arrowSize, arrowSize);
        getBatch().setColor(1, 1, 1, 1);
        getBatch().end();
    }
}
