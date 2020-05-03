package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.ai.AIDummy;
import dev.ky3he4ik.battleship.gui.placing.ShipPlacer;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.Constants;

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
    private dev.ky3he4ik.battleship.gui.placing.ShipPlacer shipPlacer;
    @NotNull
    private BitmapFont font;
    @NotNull
    private RelayTouch touchListener;

    @NotNull
    private SpriteManager manager;

    private int turn = TURN_LEFT;
    private int readyCnt = 0;
    private boolean aiReadyR = false;
    private int aiXR = -1;
    private int aiYR = -1;
    private boolean aiReadyL = false;
    private int aiXL = -1;
    private int aiYL = -1;

    private float cellSize;
    private float redundantX;
    private float redundantY;
    private float middleGap;
    private float headerHeight;
    private float footerHeight;
    private float sideWidth;

    private int step;

    private int leftScore = 0;
    private int rightScore = 0;

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
        manager = SpriteManager.getInstance();
        this.config = config;
        this.font = new BitmapFont();
        font.setColor(.2f, .2f, .2f, 1);
        calcCellSize();

        Gdx.app.debug("GameStage/init", "cellSize = " + cellSize);

        Communication leftComm = null;
        if (config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            leftComm = new AIDummy(rightWorld, leftWorld, config);
            leftComm.init();
            leftComm.setPlaceShips();
        }
        leftPlayer = new Field(leftWorld, cellSize, config.getGameType() != GameConfig.GameType.LOCAL_2P, leftComm, TURN_LEFT, this);
        leftPlayer.setBounds(redundantX + sideWidth, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(false);
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

        shipPlacer = new ShipPlacer(this, config.getShips(), cellSize);
        shipPlacer.setVisible(false);
        shipPlacer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        addActor(shipPlacer);

        step = STEP_BEGINNING;

        AnimationManager.getInstance().initAnimation(Constants.BLOW_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_BLOW_ANIMATION);

        touchListener = new RelayTouch(this);
        addListener(touchListener);

        initStep();
    }

    @Override
    public void draw() {
        super.draw();
        switch (step) {
            case STEP_BEGINNING:
                getBatch().begin();
                font.draw(getBatch(), "Press any key", getWidth() / 2, getHeight() / 2);
                getBatch().end();
                break;
            case STEP_CHOOSE_CONFIG:
                //todo: config screen
                nextStep();
                break;
            case STEP_PLACEMENT_L:
            case STEP_PLACEMENT_R:
                break;
            case STEP_GAME:
                if (aiReadyR && turn == TURN_RIGHT) {
                    aiReadyR = false;
                    boolean res = leftPlayer.open(aiXR, aiYR);
                    if (leftPlayer.getWorld().isDead())
                        nextStep();
                    if (res)
                        rightPlayer.setTurn();
                    else
                        turn();
                } else if (aiReadyL && turn == TURN_LEFT) {
                    aiReadyL = false;
                    boolean res = leftPlayer.open(aiXL, aiYL);
                    if (rightPlayer.getWorld().isDead())
                        nextStep();
                    if (res)
                        leftPlayer.setTurn();
                    else
                        turn();
                }
                if ((turn == TURN_LEFT && rightPlayer.getWorld().isDead()) || (turn == TURN_RIGHT && leftPlayer.getWorld().isDead())) {
                    nextStep();
                }
                break;
            case STEP_AFTERMATH:
                getBatch().begin();
                font.draw(getBatch(), (leftPlayer.getWorld().isDead() ? "Second" : "First") + " player won!\n" + leftScore + " : " + rightScore, getWidth() / 2, getHeight() / 2);
                getBatch().end();
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
        font.dispose();
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
        shipPlacer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());


        for (GameConfig.Ship ship : config.getShips()) {
            Sprite sprite = manager.getSprite(ship.name);
            sprite.setSize(cellSize, ship.length * cellSize);
            sprite.setOrigin(cellSize / 2, cellSize / 2);
            sprite.setRotation(0);
            if (!manager.contains(ship.name + Constants.ROTATED_SUFFIX))
                manager.cloneSprite(ship.name, ship.name + Constants.ROTATED_SUFFIX);
            sprite = manager.getSprite(ship.name + Constants.ROTATED_SUFFIX);
            sprite.setSize(cellSize, ship.length * cellSize);
            sprite.setOrigin(cellSize / 2, cellSize / 2);
            sprite.setRotation(-90);
            sprite.setFlip(true, false);
        }
    }

    public void turnFinished(int playerId, int i, int j) {
        if (isMyTurn(playerId)) {
            boolean res = getOpponent(playerId).open(i, j);

            if (getOpponent(playerId).getWorld().isDead()) {
                Gdx.app.error("GameStage", "P" + (playerId + 1) + " won");
                nextStep();
            } else if (res)
                getPlayer(playerId).setTurn();
            else
                turn();
        } else if (playerId == TURN_RIGHT && (config.getGameType() == GameConfig.GameType.AI || config.getGameType() == GameConfig.GameType.AI_VS_AI)) {
            aiReadyR = true;
            aiXR = i;
            aiYR = j;
        } else if (playerId == TURN_LEFT && (config.getGameType() == GameConfig.GameType.AI_VS_AI)) {
            aiReadyL = true;
            aiXL = i;
            aiYL = j;
        }
    }

    public void shipsPlaced(int playerId) {
        readyCnt++;
        if ((playerId == TURN_LEFT && (config.getGameType() != GameConfig.GameType.LOCAL_2P && config.getGameType() != GameConfig.GameType.AI_VS_AI)) || readyCnt == 2)
            nextStep();
        Gdx.app.debug("GameStage", "" + playerId + " is ready");
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
        if ((playerId == TURN_RIGHT || config.getGameType() == GameConfig.GameType.LOCAL_2P) && turn != playerId)
            turnFinished(getOpponent(playerId).getPlayerId(), idx, idy);
    }

    private void nextStep() {
        switch (step) {
            case STEP_BEGINNING:
                break;
            case STEP_CHOOSE_CONFIG:
                if (config.getGameType() == GameConfig.GameType.AI_VS_AI)
                    step = STEP_GAME - 1;
                break;
            case STEP_PLACEMENT_L:
                if (config.getGameType() != GameConfig.GameType.LOCAL_2P)
                    step = STEP_GAME - 1;
                break;
            case STEP_PLACEMENT_R:
            case STEP_GAME:
                break;
            case STEP_AFTERMATH:
                restart();
                step--;
                break;
            default:
                Gdx.app.error("GameStage", "Unknown step " + step);
        }
        step++;
        initStep();
    }

    private void initStep() {
        switch (step) {
            case STEP_BEGINNING:
                turn = TURN_LEFT;
                leftPlayer.setVisible(false);
                rightPlayer.setVisible(false);
                restart();
                break;
            case STEP_CHOOSE_CONFIG:
                //todo: config screen
                break;
            case STEP_PLACEMENT_L:
                leftPlayer.setVisible(true);
                rightPlayer.setVisible(false);
                shipPlacer.setVisible(true);
                rightPlayer.setTouchable(Touchable.enabled);
                leftPlayer.setTouchable(Touchable.disabled);
                shipPlacer.setTouchable(Touchable.enabled);
                shipPlacer.restart();
                shipPlacer.start(leftPlayer);
                break;
            case STEP_PLACEMENT_R:
                leftPlayer.setVisible(false);
                rightPlayer.setVisible(true);
                shipPlacer.setVisible(true);
                rightPlayer.setTouchable(Touchable.disabled);
                leftPlayer.setTouchable(Touchable.disabled);
                shipPlacer.setTouchable(Touchable.enabled);
                shipPlacer.restart();
                shipPlacer.start(rightPlayer);
                rightPlayer.setPosition(redundantX + sideWidth, redundantY + footerHeight);
                break;
            case STEP_GAME:
                rightPlayer.setPosition(sideWidth + middleGap + redundantX + cellSize * config.getWidth(), redundantY + footerHeight);
                leftPlayer.setTouchable(Touchable.enabled);
                rightPlayer.setTouchable(Touchable.enabled);
                shipPlacer.setTouchable(Touchable.disabled);
                leftPlayer.setVisible(true);
                rightPlayer.setVisible(true);
                shipPlacer.setVisible(false);
                break;
            case STEP_AFTERMATH:
                //todo: aftermath screen
                if (leftPlayer.getWorld().isDead())
                    rightScore++;
                else if (rightPlayer.getWorld().isDead())
                    leftScore++;
                break;
            default:
                Gdx.app.error("GameStage", "Unknown step " + step);
        }
    }

    private void restart() {
        if (rightPlayer.getCommunication() != null)
            rightPlayer.getCommunication().restart();
        if (leftPlayer.getCommunication() != null)
            leftPlayer.getCommunication().restart();
        leftPlayer.restart();
        rightPlayer.restart();
        step = STEP_BEGINNING;
    }

    public int getStep() {
        return step;
    }

    public boolean touchDown() {
        if (step == STEP_BEGINNING || step == STEP_AFTERMATH) {
            nextStep();
            return true;
        }
        return false;
    }
}
