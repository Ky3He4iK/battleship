package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.Field;
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
        cachedTurn = StepsDirector.TURN_LEFT;
    }

    @Override
    public void stepBegin() {
        callback.rightPlayer.setPosition(callback.sideWidth + callback.middleGap + callback.redundantX + callback.cellSize * callback.config.getWidth(), callback.redundantY + callback.footerHeight);
        callback.setChildrenEnabled(true, true);
        callback.leftPlayer.start();
        callback.rightPlayer.start();
        callback.rotateBtn.setVisible(true);
        resize();
    }

    @Override
    public void act() {
        if (callback.aiReadyR && callback.turn == StepsDirector.TURN_RIGHT && callback.canShoot(StepsDirector.TURN_RIGHT)) {
            callback.aiReadyR = false;
            boolean res = callback.leftPlayer.open(callback.aiXR, callback.aiYR);
            callback.registerShoot(StepsDirector.TURN_RIGHT, res);
            if (callback.leftPlayer.getWorld().isDead()) {
                callback.nextStep();
                return;
            }
            if (callback.canShoot(StepsDirector.TURN_RIGHT))
                callback.rightPlayer.setTurn();
            else
                callback.nextTurn();
        } else if (callback.aiReadyL && callback.turn == StepsDirector.TURN_LEFT && callback.canShoot(StepsDirector.TURN_LEFT)) {
            callback.aiReadyL = false;
            boolean res = callback.rightPlayer.open(callback.aiXL, callback.aiYL);
            callback.registerShoot(StepsDirector.TURN_LEFT, res);
            if (callback.rightPlayer.getWorld().isDead()) {
                callback.nextStep();
                return;
            }
            if (callback.canShoot(StepsDirector.TURN_LEFT))
                callback.leftPlayer.setTurn();
            else
                callback.nextTurn();
        }
        if (callback.rightPlayer.getWorld().isDead() || callback.leftPlayer.getWorld().isDead())
            callback.nextStep();
    }

    @Override
    public void draw() {
        super.draw();
        Batch batch = getBatch();

        if (callback.getTurn() != cachedTurn) {
            if (callback.getTurn() == StepsDirector.TURN_LEFT)
                arrowSprite.setFlip(false, false);
            else
                arrowSprite.setFlip(true, false);
            cachedTurn = callback.getTurn();
        }
        if (callback.getTurn() == StepsDirector.TURN_LEFT)
            arrowSprite.setColor(0, 1, 0, 1);
        else
            arrowSprite.setColor(1, 0, 0, 1);

        batch.begin();
        arrowSprite.draw(batch);
        batch.setColor(1, 1, 1, 1);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        arrowSprite.getTexture().dispose();
    }

    @Override
    public void turnFinished(int playerId, int i, int j) {
        if (playerId == callback.turn && callback.canShoot(playerId)) {
            Field opponent = callback.getOpponent(playerId);
            boolean res = opponent.open(i, j);
            callback.registerShoot(playerId, res);
            if (opponent.getWorld().isDead()) {
                Gdx.app.error("GameStage", "P" + (playerId + 1) + " won");
                callback.nextStep();
            } else if (callback.canShoot(playerId))
                callback.getPlayer(playerId).setTurn();
            else
                callback.nextTurn();
        } else if (playerId == StepsDirector.TURN_RIGHT
                && (callback.config.getGameType() == GameConfig.GameType.AI || callback.config.getGameType() == GameConfig.GameType.AI_VS_AI)) {
            callback.aiReadyR = true;
            callback.aiXR = i;
            callback.aiYR = j;
        } else if (playerId == StepsDirector.TURN_LEFT && callback.config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            callback.aiReadyL = true;
            callback.aiXL = i;
            callback.aiYL = j;
        }
    }

    @Override
    public int stepEnd() {
        callback.rotateBtn.setVisible(false);
        return super.stepEnd();
    }

    @Override
    public void resize() {
        super.resize();
        float shift = callback.middleGap * 0.1f;
        float arrowSize = callback.middleGap - shift * 2;
        arrowSprite.setBounds(callback.redundantX + callback.sideWidth + callback.config.getWidth() * callback.cellSize + shift,
                callback.redundantY + callback.footerHeight + callback.config.getHeight() * callback.cellSize / 2 - arrowSize / 2,
                arrowSize, arrowSize);
        callback.rotateBtn.setBounds(callback.redundantX + callback.sideWidth + callback.config.getWidth() * callback.cellSize + callback.middleGap / 4,
                callback.redundantY + callback.footerHeight + callback.config.getHeight() * callback.cellSize / 2 - arrowSize / 2 - callback.middleGap / 2,
                callback.middleGap / 2, callback.middleGap / 2);
    }

    @NotNull
    @Override
    public String getName() {
        return "Game";
    }
}
