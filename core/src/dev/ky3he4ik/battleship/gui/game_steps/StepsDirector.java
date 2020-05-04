package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.RelayTouch;
import dev.ky3he4ik.battleship.gui.placing.ShipPlacer;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepsDirector extends Stage {
    final static int TURN_LEFT = 0;
    final static int TURN_RIGHT = 1;

    final static int STEP_BEGINNING = 0;
    final static int STEP_CHOOSE_CONFIG = 1;
    final static int STEP_PLACEMENT_L = 2;
    final static int STEP_PLACEMENT_R = 3;
    final static int STEP_GAME = 4;
    final static int STEP_AFTERMATH = 5;

    @NotNull
    private ArrayList<BaseStep> steps;

    private int currentStep = 0;
    int turn = 0;

    float cellSize;
    float redundantX;
    float redundantY;
    float middleGap;
    float headerHeight;
    float footerHeight;
    float sideWidth;

    private int readyCnt = 0;
    boolean aiReadyR = false;
    int aiXR = -1;
    int aiYR = -1;
    boolean aiReadyL = false;
    int aiXL = -1;
    int aiYL = -1;

    int leftScore = 0;
    int rightScore = 0;

    @NotNull
    private Field leftPlayer;
    @NotNull
    private Field rightPlayer;
    @NotNull
    private GameConfig config;
    @NotNull
    private ShipPlacer shipPlacer;
    @NotNull
    private RelayTouch touchListener;

    public StepsDirector() {
        calcCellSize();
        steps = new ArrayList<>();
        steps.add(new StepBeginning(this, STEP_BEGINNING));
        steps.add(new StepConfigure(this, STEP_CHOOSE_CONFIG));
        steps.add(new StepPlacementLeft(this, STEP_PLACEMENT_L));
        steps.add(new StepPlacementRight(this, STEP_PLACEMENT_R));
        steps.add(new StepGame(this, STEP_GAME));
        steps.add(new StepAftermath(this, STEP_AFTERMATH));

        //todo
        for (int i = 0; i < steps.size(); i++)
            if (steps.get(i).stepId != i)
                Gdx.app.error("StepsDirector", "invalid step #" + steps.get(i).stepId + " at pos " + i);
    }

    void nextStep() {
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

    void restart() {
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

    @NotNull
    public Field getLeftPlayer() {
        return leftPlayer;
    }

    @NotNull
    public Field getRightPlayer() {
        return rightPlayer;
    }

    @NotNull
    public ShipPlacer getShipPlacer() {
        return shipPlacer;
    }
}
