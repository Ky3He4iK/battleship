package dev.ky3he4ik.battleship.gui.game_steps.config;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.ai.AILevel;
import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.gui.game_steps.StepConfigure;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ConfigGroup extends Stage implements ActorWithSpriteListener, ProxyListenerInterface {
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

    @NotNull
    private BitmapFont font;

    @NotNull
    private ButtonGroup<TextButton> gameTypeGroup;
    @NotNull
    private ButtonGroup<TextButton> aiLevelGroup;

    public ConfigGroup(@NotNull StepConfigure callback) {
        this.callback = callback;
        config = callback.getConfig();
        // warning: config may be null!
        // yes, it is null and annotated as @NotNull
        // good old java

        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 800f);
        font.setColor(Color.BLACK);

        setDebugAll(true);

        scrollTable = new Table();
        scrollTable.align(Align.center);
        scrollTable.setFillParent(true);

        gameTypeGroup = new ButtonGroup<>();
        GameConfig.GameType[] gameTypes = GameConfig.GameType.values();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(new SpriteDrawable(SpriteManager.getInstance().getSprite(Constants.BUTTON_FRAME)),
                new SpriteDrawable(SpriteManager.getInstance().getSprite(Constants.BUTTON_FRAME_SELECTED)),
                new SpriteDrawable(SpriteManager.getInstance().getSprite(Constants.BUTTON_FRAME_SELECTED)), font);
        for (GameConfig.GameType gameType : gameTypes) {
            TextButton btn = new TextButton(gameType.name(), style);
            btn.setName(gameType.name());
            gameTypeGroup.add(btn);
            scrollTable.add(btn);
        }
        gameTypeGroup.setChecked(config.getGameType().name());
        scrollTable.row();

        aiLevelGroup = new ButtonGroup<>();
        AILevel[] aiLevels = AILevel.values();
        for (AILevel aiLevel: aiLevels) {
            TextButton btn = new TextButton(aiLevel.name, style);
            btn.setName(aiLevel.name);
            aiLevelGroup.add(btn);
            scrollTable.add(btn);
        }
        aiLevelGroup.setChecked(config.getGameType().name());
        scrollTable.row();

        scroller = new ScrollPane(scrollTable);
        outerTable = new Table();
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

        outerTable.setBounds(callbackCallback.getRedundantX() + callbackCallback.getSideWidth(), callbackCallback.getRedundantY(),
                Gdx.graphics.getWidth() - callbackCallback.getRedundantX() * 2 - callbackCallback.getSideWidth(),
                Gdx.graphics.getHeight() - callbackCallback.getRedundantY() * 2);
        outerTable.row().height(getHeight() - callbackCallback.getCellSize());
        outerTable.add(scroller).fill().expand();//.colspan()

        scroller.invalidate();
        scrollTable.invalidate();

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
        AILevel level = AILevel.getByName(aiLevelGroup.getChecked().getName());
        if (level != null) {
            config.setAiLevel(level.id);
            config.setAiLevel2(level.id);
        }
        config.setGameType(GameConfig.GameType.valueOf(gameTypeGroup.getChecked().getName()));

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

    @Override
    public boolean proxyPressed(@NotNull InputEvent event, @Nullable String actorName) {
        return false;
    }

    @Override
    public void proxyReleased(@NotNull InputEvent event, @Nullable String actorName) {

    }

    @Override
    public void proxyDragged(@NotNull InputEvent event, @Nullable String actorName) {

    }

    @Override
    public void proxyScrolled(@NotNull InputEvent event, @Nullable String actorName) {

    }
}
