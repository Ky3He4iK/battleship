package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    public final static int STEP_AFTERMATH = 5;
    final static int STEP_CONNECTING = 6;
    final static int STEP_CONNECTING_CLIENT = 7;

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

    private int movedCurrentTurn;
    private int shootedCurrentTurn;

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

    @NotNull
    BitmapFont font;

    @NotNull
    Label stepLabel;

    boolean p1Ready;
    boolean p2Ready;

    @NotNull
    String name;
    long uuid;
    boolean isP2 = false;

    public boolean gotConfig = false;

    public StepsDirector(@NotNull String name, long uuid) {
        this.name = name;
        this.uuid = uuid;

        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 400f);
        font.setColor(Color.BLACK);

        config = GameConfig.getSampleMoving();

        stepLabel = new Label("", new Label.LabelStyle(font, font.getColor()));

        steps = new ArrayList<>();
        steps.add(new StepBeginning(this, STEP_BEGINNING));
        steps.add(new StepConfigure(this, STEP_CHOOSE_CONFIG));
        steps.add(new StepPlacementLeft(this, STEP_PLACEMENT_L));
        steps.add(new StepPlacementRight(this, STEP_PLACEMENT_R));
        steps.add(new StepGame(this, STEP_GAME));
        steps.add(new StepAftermath(this, STEP_AFTERMATH));
        steps.add(new StepConnecting(this, STEP_CONNECTING));
        steps.add(new StepConnectingClient(this, STEP_CONNECTING_CLIENT));

        manager = SpriteManager.getInstance();
        calcCellSize();

        World leftWorld = new World(config.getWidth(), config.getHeight());
        World rightWorld = new World(config.getWidth(), config.getHeight());

        leftPlayer = new Field(leftWorld, cellSize, null, TURN_LEFT, this);
        leftPlayer.setBounds(redundantX + sideWidth, redundantY + footerHeight, cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(false);
        addActor(leftPlayer);

        Communication rightComm = null;
        //todo: communication for different gamemodes
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

        rotateBtn = new ActorWithSprite(this, Constants.ARROW_ROTATE, Constants.ARROW_ROTATE_SELECTED, ROTATE_BTN_ID);
        addActor(rotateBtn);
        rotateBtn.setVisible(false);

        getStep().stepBegin();
        for (int i = 0; i < steps.size(); i++)
            if (steps.get(i).stepId != i)
                Gdx.app.error("StepsDirector", "invalid step #" + steps.get(i).stepId + " at pos " + i);
    }

    void nextStep() {
        setStep(steps.get(currentStep).stepEnd());
    }

    public void setStep(int step) {
        if (currentStep == STEP_BEGINNING)
            restart();
        steps.get(currentStep).stepEnd();
        currentStep = step;
        steps.get(currentStep).stepBegin();
        stepLabel.setText(steps.get(currentStep).getName());
    }

    void setTurn(int turn) {
        shootedCurrentTurn = 0;
        movedCurrentTurn = 0;
        if (this.turn != turn)
            nextTurn();
    }

    void nextTurn() {
        shootedCurrentTurn = 0;
        movedCurrentTurn = 0;
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
        try {
            super.act(delta);
            steps.get(currentStep).act();
        } catch (Exception e) {
            Gdx.app.error("StepsDirector/" + steps.get(currentStep).getName(), e.getMessage(), e);
        }
    }

    @Override
    public void draw() {
        try {
            super.draw();
            steps.get(currentStep).draw();
        } catch (Exception e) {
            Gdx.app.error("StepsDirector/" + steps.get(currentStep).getName(), e.getMessage(), e);
        }
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

    void calcCellSize() {
        middleGap = getWidth() * Constants.MIDDLE_GAP_PART;
        sideWidth = getWidth() * Constants.SIDE_PART;
        headerHeight = getHeight() * Constants.HEADER_PART;
        footerHeight = getHeight() * Constants.FOOTER_PART;
        float w = getWidth() - middleGap - sideWidth * 2, h = getHeight() - headerHeight - footerHeight;
        cellSize = Math.min(w / (config.getWidth() * 2), h / config.getHeight());
        redundantX = (w - cellSize * (config.getWidth() * 2)) / 2;
        redundantY = (h - cellSize * config.getHeight()) / 2;
        if (redundantY + footerHeight < cellSize || redundantY + headerHeight < cellSize) {
            w = getWidth() - middleGap - sideWidth * 2;
            h = getHeight();
            cellSize = Math.min(w / (config.getWidth() * 2), h / (config.getHeight() + 2));
            redundantY = 0;
            footerHeight = cellSize;
            headerHeight = cellSize;
            redundantX = (w - cellSize * (config.getWidth() * 2)) / 2;
        }
        Gdx.app.debug("StepsDirector", "cellSize: " + cellSize);
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        resize();
    }

    public void resize() {
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
//            sprite.setOrigin(cellSize / 2, cellSize / 2);
            sprite.setRotation(0);
//            if (!manager.contains(ship.rotatedName()))
//                manager.cloneSprite(ship.name, ship.rotatedName());
            sprite = manager.getSprite(ship.rotatedName());
            sprite.setSize(ship.length * cellSize, cellSize);
//            sprite.setOrigin(cellSize / 2, cellSize / 2);
//            sprite.setRotation(-90);
            sprite.setFlip(true, false);
        }

        getStep().resize();

        font.getData().setScale(Gdx.graphics.getHeight() / 400f);
    }

    @NotNull
    public Field getOpponent(int playerId) {
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

        if (canShoot(1 - playerId) && turn != playerId)
            if (config.getGameType() == GameConfig.GameType.GLOBAL_INET && ((playerId == TURN_LEFT) == isP2)
                    || (playerId == TURN_RIGHT && config.getGameType() != GameConfig.GameType.GLOBAL_INET)
                    || config.getGameType() == GameConfig.GameType.LOCAL_2P)
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
        font.dispose();
    }

    public void shipsPlaced(int playerId) {
        readyCnt++;
        if (playerId == TURN_LEFT) {
            p1Ready = true;
            if (rightPlayer.getCommunication() != null)
                rightPlayer.getCommunication().enemyShipsPlaced();
        } else {
            p2Ready = true;
            if (leftPlayer.getCommunication() != null)
                leftPlayer.getCommunication().enemyShipsPlaced();
        }
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

    public float getCellSize() {
        return cellSize;
    }

    public float getRedundantX() {
        return redundantX;
    }

    public float getRedundantY() {
        return redundantY;
    }

    public float getMiddleGap() {
        return middleGap;
    }

    public float getHeaderHeight() {
        return headerHeight;
    }

    public float getFooterHeight() {
        return footerHeight;
    }

    public float getSideWidth() {
        return sideWidth;
    }

    @NotNull
    public GameConfig getConfig() {
        return config;
    }

    public boolean canShoot(int playerId) {
        return turn == playerId && shootedCurrentTurn < config.getShotsPerTurn();
    }

    public boolean canMove(int playerId) {
        return turn == playerId && config.isMovingEnabled() && (movedCurrentTurn < config.getMovingPerTurn() || config.getMovingPerTurn() == -1);
    }

    public void registerMove(int playerId) {
        if (canMove(playerId))
            movedCurrentTurn++;
    }

    public void registerShoot(int playerId, boolean itWasShip) {
        if (config.isAdditionalShots() && canShoot(playerId) && !itWasShip)
            shootedCurrentTurn++;
        else if (!config.isAdditionalShots() && canShoot(playerId))
            shootedCurrentTurn++;
    }
}
