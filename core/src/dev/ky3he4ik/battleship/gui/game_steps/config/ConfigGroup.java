package dev.ky3he4ik.battleship.gui.game_steps.config;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.game_steps.StepConfigure;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ConfigGroup extends Stage implements ActorWithSpriteListener {
    private int DONE_BUTTON_ID = 1;
    @NotNull
    private StepConfigure callback;

    @NotNull
    private GameConfig config;

    @NotNull
    private Table scrollTable;

    @NotNull
    private Table outerTable;

    @NotNull
    private ScrollPane scroller;

    @Nullable
    private InputProcessor inputProcessor = null;

    @NotNull
    private ActorWithSprite doneButton;

    public ConfigGroup(@NotNull StepConfigure callback) {
        this.callback = callback;
        config = callback.getConfig();
        // warning: config may be null!
        // yes, it is null and annotated as @NotNull
        // good old java

        scrollTable = new Table();
        scrollTable.align(Align.center);
        setDebugAll(Constants.DEBUG_MODE);
//        scrollTable.add(text);
//        scrollTable.row();

        scroller = new ScrollPane(scrollTable);

        outerTable = new Table();
        outerTable.setFillParent(true);

//        setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        addActor(outerTable);

        doneButton = new ActorWithSprite(this, Constants.BUTTON_DONE, Constants.BUTTON_DONE_SELECTED, DONE_BUTTON_ID);
        doneButton.setVisible(false);
        doneButton.setName("Config/Done");
        addActor(doneButton);
    }

    public void init() {
        StepsDirector callbackCallback = callback.getCallback();
        config = callback.getConfig();

        inputProcessor = Gdx.input.getInputProcessor();
        setVisible(true);
        Gdx.input.setInputProcessor(this);

        outerTable.setBounds(callbackCallback.getRedundantX(), callbackCallback.getRedundantY(),
                Gdx.graphics.getWidth() - callbackCallback.getRedundantX() * 2,
                Gdx.graphics.getHeight() - callbackCallback.getRedundantY() * 2);
        outerTable.row().height(getHeight() - callbackCallback.getCellSize());
        outerTable.add(scroller).fill().expand();
        doneButton.setBounds(Gdx.graphics.getWidth() - callbackCallback.getRedundantX() - callbackCallback.getCellSize(),
                callbackCallback.getRedundantY(), callbackCallback.getCellSize(), callbackCallback.getCellSize());
    }

    @NotNull
    public GameConfig getConfig() {
        return config;
    }

    @NotNull
    public GameConfig finish() {
        Gdx.input.setInputProcessor(inputProcessor);


        outerTable.clearChildren();
        //todo

        setVisible(false);
        return config;
    }

    public void setVisible(boolean visible) {
        outerTable.setVisible(visible);
        doneButton.setVisible(visible);
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == DONE_BUTTON_ID) {
            //todo: check for correct config
            callback.ConfigIsDone();
            return true;
        }
        return false;
    }

    @Override
    public void buttonReleased(int buttonId) {

    }

    @Override
    public void buttonMoved(int buttonId) {

    }
}
