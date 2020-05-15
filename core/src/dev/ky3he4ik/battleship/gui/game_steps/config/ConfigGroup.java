package dev.ky3he4ik.battleship.gui.game_steps.config;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import dev.ky3he4ik.battleship.ai.AILevel;
import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.game_steps.StepConfigure;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class ConfigGroup extends Stage implements ActorWithSpriteListener, ProxyListenerInterface {
    private int DONE_BUTTON_ID = 1;
    @NotNull
    private StepConfigure callback;

    @NotNull
    private GameConfig config;

    @NotNull
    private Table scrollTable;

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

    @NotNull
    private Label.LabelStyle labelStyle;

    @NotNull
    private Slider widthSlider;

    @NotNull
    private Slider heightSlider;

    @NotNull
    private Label widthSliderLabel;

    @NotNull
    private Label heightSliderLabel;

    @NotNull
    private Slider shootsSlider;

    @NotNull
    private Label shootsSliderLabel;

    @NotNull
    private Slider movesSlider;

    @NotNull
    private Label movesSliderLabel;

    @NotNull
    private CheckBox addShots;

    @NotNull
    private Label addShotsLabel;

    @NotNull
    private ScrollPane scrollPane;

    @NotNull
    private Container<ScrollPane> tableContainer;

    public ConfigGroup(@NotNull StepConfigure callback) {
        this.callback = callback;
        config = callback.getConfig();
        // warning: config may be null!
        // yes, it is null and annotated as @NotNull
        // good old java

        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 800f);
        font.setColor(Color.BLACK);

        labelStyle = new Label.LabelStyle(font, font.getColor());
        setDebugAll(true);

        gameTypeGroup = new ButtonGroup<>();
        GameConfig.GameType[] gameTypes = GameConfig.GameType.values();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(H.getSpriteDrawable(Constants.BUTTON_FRAME_SELECTED_2), H.getSpriteDrawable(Constants.BUTTON_FRAME), H.getSpriteDrawable(Constants.BUTTON_FRAME_SELECTED), font);
        for (GameConfig.GameType gameType : gameTypes) {
            TextButton btn = new TextButton(gameType.name(), style);
            btn.setName(gameType.name());
            gameTypeGroup.add(btn);
        }
        gameTypeGroup.setChecked(config.getGameType().name());

        aiLevelGroup = new ButtonGroup<>();
        AILevel[] aiLevels = AILevel.values();
        for (AILevel aiLevel : aiLevels) {
            TextButton btn = new TextButton(aiLevel.name, style);
            btn.setName(aiLevel.name);
            aiLevelGroup.add(btn);
        }
        aiLevelGroup.setChecked(Objects.requireNonNull(AILevel.getById(config.getAiLevel())).name);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(H.getSpriteDrawable(Constants.SLIDER_BACKGROUND), H.getSpriteDrawable(Constants.SLIDER_KNOB));
        widthSlider = new Slider(3, 20, 1, false, sliderStyle);
        widthSliderLabel = new Label(null, labelStyle);
        heightSlider = new Slider(3, 20, 1, false, sliderStyle);
        heightSliderLabel = new Label(null, labelStyle);

        //todo:
        //      decreasingField
        //      aiLevel2
        //      ships
        shootsSlider = new Slider(1, 10, 1, false, sliderStyle);
        shootsSliderLabel = new Label(null, labelStyle);

        movesSlider = new Slider(0, 11, 1, false, sliderStyle);
        movesSliderLabel = new Label(null, labelStyle);

        addShots = new CheckBox(null, new CheckBox.CheckBoxStyle(H.getSpriteDrawable(Constants.BUTTON_DONE_FRAME), H.getSpriteDrawable(Constants.BUTTON_DONE_SELECTED), font, font.getColor()));
        addShotsLabel = new Label("Additional shots", labelStyle);

        scrollTable = new Table();
        scrollTable.align(Align.center);
        scrollTable.setFillParent(true);
        scrollTable.defaults().uniform();

        scrollPane = new ScrollPane(scrollTable);

        tableContainer = new Container<>(scrollPane);
        tableContainer.setFillParent(true);
        addActor(tableContainer);

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

        tableContainer.setBounds(callbackCallback.getRedundantX(),
                callbackCallback.getRedundantY(),
                Gdx.graphics.getWidth() - callbackCallback.getRedundantX() * 2 - callbackCallback.getSideWidth() * 2,
                Gdx.graphics.getHeight() - callbackCallback.getRedundantY() * 2 - callbackCallback.getCellSize() - callbackCallback.getFooterHeight() - callbackCallback.getHeaderHeight());

//        scrollTable.setFillParent(true);

        scrollTable.add(new Label("Game mode:", labelStyle)).uniform();
        Array<TextButton> btns = gameTypeGroup.getButtons();
        for (TextButton btn : new Array.ArrayIterator<>(btns))
            scrollTable.add(btn).prefWidth(scrollTable.getWidth() / btns.size).fill().uniform();
        scrollTable.row();

        scrollTable.add(new Label("AI difficulty:", labelStyle)).uniform();
        btns = aiLevelGroup.getButtons();
        for (TextButton btn : new Array.ArrayIterator<>(btns))
            scrollTable.add(btn).prefWidth(scrollTable.getWidth() / btns.size).fill().uniform();
        scrollTable.row();

        int colspan = scrollTable.getColumns() - 1;
        float width = getWidth() / (colspan + 1) * colspan;
        scrollTable.add(widthSliderLabel);
        scrollTable.add(widthSlider).colspan(colspan).width(width);
        widthSlider.setValue(config.getWidth());
        scrollTable.row();
        scrollTable.add(heightSliderLabel);
        scrollTable.add(heightSlider).colspan(colspan).width(width);
        heightSlider.setValue(config.getHeight());
        scrollTable.row();

        scrollTable.add(shootsSliderLabel);
        scrollTable.add(shootsSlider).colspan(colspan).width(width);
        shootsSlider.setValue(config.getShotsPerTurn());
        scrollTable.row();
        scrollTable.add(movesSliderLabel);
        scrollTable.add(movesSlider).colspan(colspan).width(width);
        movesSlider.setValue(config.getMovingPerTurn() == -1 ? movesSlider.getMaxValue() : config.getMovingPerTurn());
        scrollTable.row();
        scrollTable.add(addShotsLabel);
        scrollTable.add(addShots).colspan(colspan).width(width).align(Align.right);
        addShots.setChecked(config.isAdditionalShots());
        scrollTable.row();

        doneButton.setBounds(Gdx.graphics.getWidth() - callbackCallback.getRedundantX() - callbackCallback.getCellSize() - callbackCallback.getSideWidth(),
                callbackCallback.getRedundantY() + callbackCallback.getFooterHeight() - callbackCallback.getCellSize(), callbackCallback.getCellSize(), callbackCallback.getCellSize());

//        scrollTable.invalidate();
    }

    @NotNull
    public GameConfig getConfig() {
        return config;
    }

    @NotNull
    public GameConfig finish() {
        Gdx.input.setInputProcessor(inputProcessor);

        scrollTable.clearChildren();
        TextButton checked = aiLevelGroup.getChecked();
        if (checked != null) {
            AILevel level = AILevel.getByName(checked.getName());
            if (level != null) {
                config.setAiLevel(level.id);
                config.setAiLevel2(level.id);
            }
        }
        checked = gameTypeGroup.getChecked();
        if (checked != null)
            config.setGameType(GameConfig.GameType.valueOf(checked.getName()));
        config.setWidth(Math.round(widthSlider.getValue()));
        config.setHeight(Math.round(heightSlider.getValue()));
        config.setShotsPerTurn(Math.round(shootsSlider.getValue()));
        if (movesSlider.getValue() == movesSlider.getMaxValue())
            config.setMovingPerTurn(-1);
        else {
            config.setMovingPerTurn(Math.round(movesSlider.getValue()));
            if (config.getMovingPerTurn() == 0)
                config.setMovingEnabled(false);
            else
                config.setMovingEnabled(true);
        }
        config.setAdditionalShots(addShots.isChecked());
        //todo

        setVisible(false);
        Gdx.app.debug("ConfigGroup/finish", config.toJSON());
        return config;
    }

    public void setVisible(boolean visible) {
        scrollTable.setVisible(visible);
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

    @Override
    public void act() {
        super.act();
        widthSliderLabel.setText("Field width: " + Math.round(widthSlider.getValue()));
        heightSliderLabel.setText("Field height: " + Math.round(heightSlider.getValue()));
        shootsSliderLabel.setText("Shoots per turn: " + Math.round(shootsSlider.getValue()));
        int moves = Math.round(movesSlider.getValue());
        if (movesSlider.getValue() == movesSlider.getMaxValue())
            movesSliderLabel.setText("Moves per turn: infinity");
        else if (moves == 0)
            movesSliderLabel.setText("Moves per turn: disable");
        else
            movesSliderLabel.setText("Moves per turn: " + moves);

        //todo
    }
}
