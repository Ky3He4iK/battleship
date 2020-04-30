package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.ai.AIDummy;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.Helpers;

public class GameStage extends Stage {
    public final static int TURN_LEFT = 0;
    public final static int TURN_RIGHT = 1;

    public final static int STEP_CHOOSE_CONFIG = 1;
    public final static int STEP_PLACEMENT_L = 2;
    public final static int STEP_PLACEMENT_R = 3;
    public final static int STEP_GAME = 4;
    public final static int STEP_AFTERMATH = 5;


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

    private int step = STEP_PLACEMENT_L;

    GameStage(@NotNull final GameConfig config, @NotNull final World leftWorld, @NotNull final World rightWorld) {
        super(new ExtendViewport(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        this.config = config;
        float cellSize = Math.min(getWidth() / (config.getWidth() * 2 + 4), getHeight() / (config.getHeight() + 3));
        float redundantX = (getWidth() - cellSize * (config.getWidth() * 2 + 4)) / 2;
        float redundantY = (getHeight() - cellSize * (config.getHeight() + 3)) / 2;

        Gdx.app.debug("GameStage/init", "cellSize = " + cellSize);

        for (int i = 0; i < config.getShips().size(); i++) {
            GameConfig.Ship ship = config.getShips().get(i);
            Sprite sprite = SpriteManager.getInstance().initSprite(ship.name);
            sprite.setSize(1, ship.length);
            sprite.setOrigin(.5f, .5f);
            sprite.setRotation(0);
            Sprite sprite_rot = SpriteManager.getInstance().cloneSprite(ship.name, ship.name + Constants.ROTATED_SUFFIX);
            sprite_rot.setOrigin(.5f, .5f);
            sprite_rot.setSize(1, ship.length);
            sprite_rot.setRotation(-90);
            sprite_rot.setFlip(true, false);
        }

        leftPlayer = new Field(leftWorld, cellSize, config.getGameType() != GameConfig.GameType.LOCAL_2P, null, TURN_LEFT, this);
        leftPlayer.setPosition(redundantX + cellSize, redundantY + cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(true);
        Helpers.placeShipsRandom(leftWorld, config);
        addActor(leftPlayer);

        Communication rightComm = null;
        if (config.getGameType() == GameConfig.GameType.AI) {
            rightComm = new AIDummy(leftPlayer.getWorld(), rightWorld, config);
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
                rightPlayer.setTurn();
            else
                turn();
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
        if (isMyTurn(playerId)) {
            if (getOpponent(playerId).open(i, j))
                getPlayer(playerId).setTurn();
            else
                turn();
            if (getOpponent(playerId).getWorld().isDead())
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
        if (turn == TURN_LEFT) {
            turn = TURN_RIGHT;
            rightPlayer.setTurn();
        } else {
            turn = TURN_LEFT;
            leftPlayer.setTurn();
        }
    }

    private Field getPlayer(int playerId) {
        if (playerId == TURN_LEFT)
            return leftPlayer;
        else
            return rightPlayer;
    }

    private Field getOpponent(int playerId) {
        if (playerId == TURN_LEFT)
            return rightPlayer;
        else
            return leftPlayer;
    }

    public void cellPressed(int playerId, int idx, int idy) {
        if ((playerId == TURN_RIGHT || config.getGameType() == GameConfig.GameType.LOCAL_2P) && turn != playerId) {
            turnFinished(getOpponent(playerId).getPlayerId(), idx, idy);
        }
        //todo
    }

    public int getStep() {
        return step;
    }
}
