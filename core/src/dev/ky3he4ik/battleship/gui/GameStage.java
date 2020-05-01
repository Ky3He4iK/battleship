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
import dev.ky3he4ik.battleship.utils.H;

public class GameStage extends Stage {
    public final static int TURN_LEFT = 0;
    public final static int TURN_RIGHT = 1;

    public final static int STEP_BEGINNING = 0;
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
    @NotNull
    ShipPlacer shipPlacer;

    private int turn = TURN_LEFT;
    private int readyCnt = 0;
    private boolean aiReady = false;
    private int aiX = -1;
    private int aiY = -1;

    private float cellSize;
    private float redundantX;
    private float redundantY;
    private float middleGap;
    private float headerHeight;
    private float footerHeight;
    private float sideWidth;

    private int step;

    private void calcCellSize() {
        middleGap = getWidth() * Constants.MIDDLE_GAP_PART;
        sideWidth = getWidth() * Constants.SIDE_PART;
        headerHeight = getHeight() * Constants.HEADER_PART;
        footerHeight = getHeight() * Constants.FOOTER_PART;
        float w = getWidth() - middleGap - sideWidth * 2, h = getHeight() - headerHeight - footerHeight;
        cellSize = Math.min(w / (config.getWidth() * 2), h / config.getHeight());
        redundantX = (w - cellSize * (config.getWidth() * 2)) / 2;
        redundantY = (h - cellSize * config.getHeight()) / 2;
    }

    GameStage(@NotNull final GameConfig config, @NotNull final World leftWorld, @NotNull final World rightWorld) {
        super(new ExtendViewport(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        this.config = config;
        calcCellSize();

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
        leftPlayer.setBounds(redundantX + sideWidth, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(true);
        H.placeShipsRandom(leftWorld, config);
        addActor(leftPlayer);

        Communication rightComm = null;
        if (config.getGameType() == GameConfig.GameType.AI || config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            rightComm = new AIDummy(leftPlayer.getWorld(), rightWorld, config);
            rightComm.init();
            rightComm.setPlaceShips();
        }
        rightPlayer = new Field(rightWorld, cellSize, Constants.DEBUG_MODE || config.getGameType() == GameConfig.GameType.AI_VS_AI, rightComm, TURN_RIGHT, this);
        rightPlayer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setVisible(false);
        addActor(rightPlayer);

        shipPlacer = new ShipPlacer(config.getShips());
        shipPlacer.setVisible(false);
        shipPlacer.setBounds(redundantX + sideWidth, redundantY + footerHeight, middleGap + cellSize * config.getWidth() * 2, cellSize * config.getHeight());
        addActor(shipPlacer);

        step = STEP_BEGINNING;

        AnimationManager.getInstance().initAnimation(Constants.BLOW_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_BLOW_ANIMATION);
    }

    @Override
    public void draw() {
        super.draw();
        switch (step) {
            case STEP_BEGINNING:
                //todo
                nextStep();
                break;
            case STEP_CHOOSE_CONFIG:
                //todo
                nextStep();
                break;
            case STEP_PLACEMENT_L:
                //todo
                nextStep();
                break;
            case STEP_PLACEMENT_R:
                //todo
                nextStep();
                break;
            case STEP_GAME:
                if (aiReady && turn == TURN_RIGHT) {
                    aiReady = false;
                    boolean res = leftPlayer.open(aiX, aiY);
                    if (leftPlayer.getWorld().isDead())
                        nextStep();
                    if (res)
                        rightPlayer.setTurn();
                    else
                        turn();
                }
                if (turn == TURN_LEFT && rightPlayer.getWorld().isDead())
                    nextStep();
                break;
            case STEP_AFTERMATH:
                //todo
                break;
            default:
                Gdx.app.error("GameStage", "Unknown step: " + step);
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
        AnimationManager.getInstance().dispose(Constants.BLOW_ANIMATION.name);
        AnimationManager.getInstance().dispose(Constants.WATER_ANIMATION.name);
        AnimationManager.getInstance().dispose(Constants.WATER_BLOW_ANIMATION.name);
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        calcCellSize();

        leftPlayer.setBounds(sideWidth + redundantX, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setY(redundantY + footerHeight);
        if (step == STEP_PLACEMENT_R)
            rightPlayer.setX(sideWidth + redundantX);
        else
            rightPlayer.setX(sideWidth + redundantX + middleGap + cellSize * config.getWidth());
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        shipPlacer.setBounds(sideWidth + redundantX, redundantY + footerHeight, middleGap + cellSize * config.getWidth() * 2, cellSize * config.getHeight());
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

    private void nextStep() {
        switch (step) {
            case STEP_BEGINNING:
                //todo: config screen
                shipPlacer.setVisible(true);
                shipPlacer.start();
                break;
            case STEP_CHOOSE_CONFIG:
                if (config.getGameType() == GameConfig.GameType.AI_VS_AI)
                    step += 2;
                else
                    rightPlayer.setVisible(false);
                break;
            case STEP_PLACEMENT_L:
                if (config.getGameType() != GameConfig.GameType.LOCAL_2P) {
                    step++;
                    rightPlayer.setVisible(true);
                } else {
                    leftPlayer.setVisible(false);
                    rightPlayer.setVisible(true);
                    rightPlayer.setPosition(sideWidth + redundantX, redundantY);
                }
                break;
            case STEP_PLACEMENT_R:
                rightPlayer.setPosition(sideWidth + middleGap + redundantX + cellSize * config.getWidth(), redundantY);
                leftPlayer.setVisible(true);
                rightPlayer.setVisible(true);
                break;
            case STEP_GAME:
                //todo: aftermath screen
                break;
            case STEP_AFTERMATH:
                restart();
                break;
            default:
                Gdx.app.error("GameStage", "Unknown step " + step);
        }
        step++;
    }

    private void restart() {
        leftPlayer.restart();
        H.placeShipsRandom(leftPlayer.getWorld(), config);
        if (rightPlayer.getCommunication() != null && (config.getGameType() == GameConfig.GameType.AI || config.getGameType() == GameConfig.GameType.AI_VS_AI))
            rightPlayer.getCommunication().restart();

        rightPlayer.restart();
        step = STEP_BEGINNING;
    }

    public int getStep() {
        return step;
    }

}
