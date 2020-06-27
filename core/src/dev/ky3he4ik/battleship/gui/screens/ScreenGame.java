package dev.ky3he4ik.battleship.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

/**
 * Непосредственно игра
 * todo: simplify
 */
public class ScreenGame extends BaseScreen {
    @NotNull
    private Sprite arrowSprite;

    private int cachedTurn;

    ScreenGame(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
        arrowSprite = SpriteManager.getInstance().getSprite(Constants.ARROW_TURN);
        arrowSprite.setFlip(false, false);
        cachedTurn = ScreensDirector.TURN_LEFT;
    }

    @Override
    public void stepBegin() {
        callback.rightPlayer.setPosition(callback.sideWidth + callback.middleGap + callback.redundantX + callback.cellSize * staticContent.config.getWidth(), callback.redundantY + callback.footerHeight);
        callback.setChildrenEnabled(true, true);
        callback.leftPlayer.start();
        callback.rightPlayer.start();
        callback.rotateBtn.setVisible(true);
        resize();
    }

    @Override
    public void act() {
        super.act();
        if (!callback.canShoot(callback.turn))
            callback.nextTurn();
        if (callback.turn == ScreensDirector.TURN_RIGHT && callback.aiReadyR) {
            callback.aiReadyR = false;
            boolean res = callback.leftPlayer.open(callback.aiXR, callback.aiYR);
            Communication communication = callback.leftPlayer.getCommunication();
            if (communication != null)
                communication.enemyTurned(callback.aiXR, callback.aiYR);
            callback.registerShoot(ScreensDirector.TURN_RIGHT, res);
        } else if (callback.turn == ScreensDirector.TURN_LEFT && callback.aiReadyL) {
            callback.aiReadyL = false;
            boolean res = callback.rightPlayer.open(callback.aiXL, callback.aiYL);
            Communication communication = callback.rightPlayer.getCommunication();
            if (communication != null)
                communication.enemyTurned(callback.aiXL, callback.aiYL);
            callback.registerShoot(ScreensDirector.TURN_LEFT, res);
        }
        if (callback.rightPlayer.getWorld().isDead() || callback.leftPlayer.getWorld().isDead())
            callback.nextStep();
        else if (callback.canShoot(callback.turn))
            callback.getPlayer(callback.turn).setTurn();
        else
            callback.nextTurn();

        if (callback.getTurn() != cachedTurn) {
            cachedTurn = callback.getTurn();
            arrowSprite.setFlip(!arrowSprite.isFlipX(), false);
        }
        if (callback.getTurn() == ScreensDirector.TURN_LEFT)
            arrowSprite.setColor(0, 1, 0, 1);
        else
            arrowSprite.setColor(1, 0, 0, 1);
    }

    @Override
    public void draw() {
        super.draw();
        Batch batch = getBatch();
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
        Gdx.app.debug("StepGame", "turn finished: " + playerId + "; " + i + 'x' + j);
        if (playerId == callback.turn && callback.canShoot(playerId)) {
            Field opponent = callback.getOpponent(playerId);
            boolean res = opponent.open(i, j);
            if (opponent.getCommunication() != null)
                opponent.getCommunication().enemyTurned(i, j);
            callback.registerShoot(playerId, res);
            if (opponent.getWorld().isDead())
                callback.nextStep();
            else if (callback.canShoot(playerId))
                callback.getPlayer(playerId).setTurn();
            else
                callback.nextTurn();
        } else if (playerId == ScreensDirector.TURN_RIGHT && !callback.aiReadyR
                && (staticContent.config.getGameType() == GameConfig.GameType.AI
                || staticContent.config.getGameType() == GameConfig.GameType.AI_VS_AI)) {
            Gdx.app.debug("StepGame", "set right turn");
            callback.aiReadyR = true;
            callback.aiXR = i;
            callback.aiYR = j;
        } else if (playerId == ScreensDirector.TURN_LEFT && staticContent.config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            callback.aiReadyL = true;
            callback.aiXL = i;
            callback.aiYL = j;
        } else
            Gdx.app.log("StepGame", "wrong turn!");
    }

    @Override
    public int stepEnd() {
        callback.rotateBtn.setVisible(false);
        if (callback.leftPlayer.getCommunication() != null)
            callback.leftPlayer.getCommunication().finish();
        if (callback.rightPlayer.getCommunication() != null)
            callback.rightPlayer.getCommunication().finish();
        callback.leftPlayer.removeCommunication();
        callback.rightPlayer.removeCommunication();
        return super.stepEnd();
    }

    @Override
    public void resize() {
        super.resize();
        float shift = callback.middleGap * 0.1f;
        float arrowSize = callback.middleGap - shift * 2;
        arrowSprite.setBounds(callback.redundantX + callback.sideWidth + staticContent.config.getWidth() * callback.cellSize + shift,
                callback.redundantY + callback.footerHeight + staticContent.config.getHeight() * callback.cellSize / 2 - arrowSize / 2,
                arrowSize, arrowSize);
        callback.rotateBtn.setBounds(callback.redundantX + callback.sideWidth + staticContent.config.getWidth() * callback.cellSize + callback.middleGap / 4,
                callback.redundantY + callback.footerHeight + staticContent.config.getHeight() * callback.cellSize / 2 - arrowSize / 2 - callback.middleGap / 2,
                callback.middleGap / 2, callback.middleGap / 2);
    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenGame";
    }
}
