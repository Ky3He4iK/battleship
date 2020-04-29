package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.ai.AIDummy;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class GameStage extends Stage {
    public final static int TURN_LEFT = 0;
    public final static int TURN_RIGHT = 1;

    @NotNull
    private Field leftPlayer;
    @NotNull
    private Field rightPlayer;
    @NotNull
    private final GameConfig config;

    private int turn = TURN_LEFT;
    private int readyCnt = 0;
    private boolean aiReady = false;
    private int aiX = -1;
    private int aiY = -1;

    GameStage(@NotNull final GameConfig config, @NotNull final World leftWorld, @NotNull final World rightWorld) {
        super(new ExtendViewport(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        this.config = config;
        float cellSize = Math.min(getWidth() / (config.getWidth() * 2 + 4), getHeight() / (config.getHeight() + 3));
        float redundantX = (getWidth() - cellSize * (config.getWidth() * 2 + 4)) / 2;
        float redundantY = (getHeight() - cellSize * (config.getHeight() + 3)) / 2;

        Gdx.app.debug("GameStage/init", "cellSize = " + cellSize);

        leftPlayer = new Field(leftWorld, cellSize, config.getGameType() != GameConfig.GameType.LOCAL_2P, null, TURN_LEFT, this);
        leftPlayer.setPosition(redundantX + cellSize, redundantY + cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(true);
        addActor(leftPlayer);

        Communication rightComm = null;
        if (config.getGameType() == GameConfig.GameType.AI) {
            rightComm = new AIDummy(leftPlayer.getWorld(), rightWorld);
            rightComm.init();
            rightComm.setPlaceShips();
        }
        rightPlayer = new Field(rightWorld, cellSize, Constants.DEBUG_MODE, rightComm, TURN_RIGHT, this);
        rightPlayer.setPosition(redundantX + cellSize * (config.getWidth() + 3), redundantY + cellSize);
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setVisible(true);
        addActor(rightPlayer);
    }

    @Override
    public void draw() {
        super.draw();
        if (aiReady && turn == TURN_RIGHT) {
            aiReady = false;
            if (leftPlayer.open(aiX, aiY))
                turn();
            rightPlayer.setTurn();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        leftPlayer.dispose();
        leftPlayer.clearActions();
        leftPlayer.clearListeners();
        rightPlayer.clearActions();
        rightPlayer.clearListeners();
        rightPlayer.dispose();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        float cellSize = Math.min(getWidth() / (config.getWidth() * 2 + 4), getHeight() / (config.getHeight() + 3));
        float redundantX = (getWidth() - cellSize * (config.getWidth() * 2 + 4)) / 2;
        float redundantY = (getHeight() - cellSize * (config.getHeight() + 3)) / 2;

        leftPlayer.setPosition(redundantX + cellSize, redundantY + cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setPosition(redundantX + cellSize * (config.getWidth() + 3), redundantY + cellSize);
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
    }

    public void turnFinished(int playerId, int i, int j) {
//        switch (config.getGameType())
        if (isMyTurn(playerId)) {
            if (getOpponent(playerId).open(i, j))
                turn();
            if (!getOpponent(playerId).getWorld().isAlive())
                Gdx.app.error("GameStage", "P" + (playerId + 1) + " won");
        } else if (playerId == TURN_RIGHT && config.getGameType() == GameConfig.GameType.AI) {
            aiReady = true;
            aiX = i;
            aiY = j;
        }
    }

    public void shipsPlaced(int playerId) {
        readyCnt++;
    }

    public boolean isMyTurn(int playerId) {
        return playerId == turn;
    }

    private void turn() {
        if (turn == TURN_LEFT)
            turn = TURN_RIGHT;
        else
            turn = TURN_LEFT;
    }

    private Field getPlayer(int playerId) {
        if (playerId == TURN_LEFT)
            return rightPlayer;
        else
            return leftPlayer;
    }

    private Field getOpponent(int playerId) {
        if (playerId == TURN_LEFT)
            return leftPlayer;
        else
            return rightPlayer;
    }

    public void cellPressed(int playerId, int idx, int idy) {
        if ((playerId == TURN_RIGHT || config.getGameType() == GameConfig.GameType.LOCAL_2P) && turn != playerId) {
            turnFinished(getOpponent(playerId).getPlayerId(), idx, idy);
        }
        //todo
    }
}
