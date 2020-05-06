package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.ai.AIDummy;
import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.RelayTouch;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.gui.placing.ShipPlacer;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepsDirector extends Stage implements ActorWithSpriteListener {
    final static int TURN_LEFT = 0;
    final static int TURN_RIGHT = 1;

    final static int STEP_BEGINNING = 0;
    final static int STEP_CHOOSE_CONFIG = 1;
    final static int STEP_PLACEMENT_L = 2;
    final static int STEP_PLACEMENT_R = 3;
    final static int STEP_GAME = 4;
    final static int STEP_AFTERMATH = 5;

    private static final int ROTATE_BTN_ID = 1;

    @NotNull
    private ArrayList<BaseStep> steps;

    @NotNull
    private SpriteManager manager;

    private int currentStep = STEP_BEGINNING;
    int turn = 0;

    float cellSize;
    float redundantX;
    float redundantY;
    float middleGap;
    float headerHeight;
    float footerHeight;
    float sideWidth;

    int readyCnt = 0;
    boolean aiReadyR = false;
    int aiXR = -1;
    int aiYR = -1;
    boolean aiReadyL = false;
    int aiXL = -1;
    int aiYL = -1;

    int leftScore = 0;
    int rightScore = 0;

    @NotNull
    Field leftPlayer;
    @NotNull
    Field rightPlayer;
    @NotNull
    GameConfig config;
    @NotNull
    ShipPlacer shipPlacer;

    @NotNull
    RelayTouch touchListener;

    @NotNull
    ActorWithSprite rotateBtn;

    public StepsDirector() {
        steps = new ArrayList<>();
        steps.add(new StepBeginning(this, STEP_BEGINNING));
        steps.add(new StepConfigure(this, STEP_CHOOSE_CONFIG));
        steps.add(new StepPlacementLeft(this, STEP_PLACEMENT_L));
        steps.add(new StepPlacementRight(this, STEP_PLACEMENT_R));
        steps.add(new StepGame(this, STEP_GAME));
        steps.add(new StepAftermath(this, STEP_AFTERMATH));

        manager = SpriteManager.getInstance();
        config = GameConfig.getSampleConfigEast();
        calcCellSize();

        World leftWorld = new World(config.getWidth(), config.getHeight());
        World rightWorld = new World(config.getWidth(), config.getHeight());

        Communication leftComm = null;
        if (config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            leftComm = new AIDummy(rightWorld, leftWorld, config);
            leftComm.init();
        }
        leftPlayer = new Field(leftWorld, cellSize, leftComm, TURN_LEFT, this);
        leftPlayer.setBounds(redundantX + sideWidth, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(false);
        addActor(leftPlayer);

        Communication rightComm = null;
        if (config.getGameType() == GameConfig.GameType.AI || config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            rightComm = new AIDummy(leftPlayer.getWorld(), rightWorld, config);
            rightComm.init();
        }
        rightPlayer = new Field(rightWorld, cellSize, rightComm, TURN_RIGHT, this);
        rightPlayer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setVisible(false);
        addActor(rightPlayer);

        shipPlacer = new ShipPlacer(this, config.getShips(), cellSize);
        shipPlacer.setVisible(false);
        shipPlacer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        addActor(shipPlacer);

        AnimationManager.getInstance().initAnimation(Constants.BLOW_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_ANIMATION);
        AnimationManager.getInstance().initAnimation(Constants.WATER_BLOW_ANIMATION);

        touchListener = new RelayTouch(this);
        addListener(touchListener);

        rotateBtn = new ActorWithSprite(this, Constants.ARROW_ROTATE, ROTATE_BTN_ID);
        addActor(rotateBtn);
        rotateBtn.setVisible(false);

        getStep().stepBegin();
        for (int i = 0; i < steps.size(); i++)
            if (steps.get(i).stepId != i)
                Gdx.app.error("StepsDirector", "invalid step #" + steps.get(i).stepId + " at pos " + i);
    }

    void nextStep() {
        if (currentStep == STEP_BEGINNING)
            restart();
        currentStep = steps.get(currentStep).stepEnd();
        steps.get(currentStep).stepBegin();
    }

    void setTurn(int turn) {
        if (this.turn != turn)
            nextTurn();
    }

    void nextTurn() {
        if (turn == TURN_LEFT) {
            turn = TURN_RIGHT;
            rightPlayer.setTurn();
        } else {
            turn = TURN_LEFT;
            leftPlayer.setTurn();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        steps.get(currentStep).act();
    }

    @Override
    public void draw() {
        super.draw();
        steps.get(currentStep).draw();
    }

    public int getTurn() {
        return turn;
    }

    void setChildrenEnabled(boolean left, boolean right) {
        setChildrenEnabled(left, left, right, right, false, false);
    }

    void setChildrenEnabled(boolean left, boolean leftTouch, boolean right, boolean rightTouch,
                            boolean placer, boolean placerTouch) {
        leftPlayer.setVisible(left);
        if (leftTouch)
            leftPlayer.setTouchable(Touchable.enabled);
        else
            leftPlayer.setTouchable(Touchable.disabled);

        rightPlayer.setVisible(right);
        if (rightTouch)
            rightPlayer.setTouchable(Touchable.enabled);
        else
            rightPlayer.setTouchable(Touchable.disabled);

        shipPlacer.setVisible(placer);
        if (placerTouch)
            shipPlacer.setTouchable(Touchable.enabled);
        else
            shipPlacer.setTouchable(Touchable.disabled);
    }

    private void restart() {
        if (rightPlayer.getCommunication() != null)
            rightPlayer.getCommunication().restart();
        if (leftPlayer.getCommunication() != null)
            leftPlayer.getCommunication().restart();
        leftPlayer.restart();
        rightPlayer.restart();
    }

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

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        calcCellSize();

        leftPlayer.setBounds(sideWidth + redundantX, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setY(redundantY + footerHeight);
        if (currentStep == STEP_PLACEMENT_R)
            rightPlayer.setX(sideWidth + redundantX);
        else
            rightPlayer.setX(sideWidth + redundantX + middleGap + cellSize * config.getWidth());
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        shipPlacer.setBounds(sideWidth + redundantX + middleGap + cellSize * config.getWidth(), redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        shipPlacer.setCellSize(cellSize);

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

        getStep().resize();
    }

    @NotNull
    Field getOpponent(int playerId) {
        if (playerId == TURN_LEFT)
            return rightPlayer;
        else
            return leftPlayer;
    }

    @NotNull
    Field getPlayer(int playerId) {
        if (playerId == TURN_LEFT)
            return leftPlayer;
        else
            return rightPlayer;
    }

    @NotNull
    private BaseStep getStep() {
        return steps.get(currentStep);
    }

    public void cellPressed(int playerId, int idx, int idy) {
        if ((playerId == TURN_RIGHT || config.getGameType() == GameConfig.GameType.LOCAL_2P) && turn != playerId)
            turnFinished(getOpponent(playerId).getPlayerId(), idx, idy);
    }

    public void turnFinished(int playerId, int i, int j) {
        getStep().turnFinished(playerId, i, j);
    }

    public boolean relayTouch(InputEvent event, float x, float y, int pointer, int button) {
        return getStep().relayTouch(event, x, y, pointer, button);
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
        for (BaseStep step : steps)
            step.dispose();
    }

    public void shipsPlaced(int playerId) {
        readyCnt++;
        if (readyCnt == 2)
            nextStep();
        Gdx.app.debug("GameStage", "" + playerId + " is ready");
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == ROTATE_BTN_ID)
            return getPlayer(turn).rotateButtonPressed();
        return false;
    }

    @Override
    public void buttonReleased(int buttonId) {

    }

    @Override
    public void buttonMoved(int buttonId) {

    }
}
