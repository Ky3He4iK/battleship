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

import java.util.ArrayList;
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
    private Label aiLevelGroupLabel;

    @NotNull
    private ButtonGroup<TextButton> aiLevelGroup;

    @NotNull
    private Label aiLevelGroupLabel2;

    @NotNull
    private ButtonGroup<TextButton> aiLevelGroup2;

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
    private CheckBox decrField;

    @NotNull
    private Label decrFieldLabel;

    @NotNull
    private ShipCntChooser[] shipCntChoosers;

    @NotNull
    private ScrollPane scrollPane;

    @NotNull
    private Container<ScrollPane> tableContainer;

    private boolean isLeftAIShown = true;
    private boolean isRightAIShown = true;

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
        setDebugAll(Constants.DEBUG_MODE);

        gameTypeGroup = new ButtonGroup<>();
        GameConfig.GameType[] gameTypes = GameConfig.GameType.values();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(H.getSpriteDrawable(Constants.BUTTON_FRAME_SELECTED_2), H.getSpriteDrawable(Constants.BUTTON_FRAME), H.getSpriteDrawable(Constants.BUTTON_FRAME_SELECTED), font);
        for (GameConfig.GameType gameType : gameTypes) {
            TextButton btn = new TextButton(gameType.name(), style);
            btn.setName(gameType.name());
            gameTypeGroup.add(btn);
        }
        gameTypeGroup.setChecked(config.getGameType().name());

        aiLevelGroupLabel = new Label("AI difficulty: ", labelStyle);
        aiLevelGroupLabel2 = new Label("Left AI difficulty: ", labelStyle);
        aiLevelGroup = new ButtonGroup<>();
        aiLevelGroup2 = new ButtonGroup<>();
        AILevel[] aiLevels = AILevel.values();
        for (AILevel aiLevel : aiLevels) {
            TextButton btn = new TextButton(aiLevel.name, style);
            btn.setName(aiLevel.name);
            aiLevelGroup.add(btn);
            btn = new TextButton(aiLevel.name, style);
            btn.setName(aiLevel.name);
            aiLevelGroup2.add(btn);
        }
        aiLevelGroup.setChecked(Objects.requireNonNull(AILevel.getById(config.getAiLevel())).name);
        aiLevelGroup2.setChecked(Objects.requireNonNull(AILevel.getById(config.getAiLevel2())).name);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(H.getSpriteDrawable(Constants.SLIDER_BACKGROUND), H.getSpriteDrawable(Constants.SLIDER_KNOB));
        widthSlider = new Slider(4, 20, 1, false, sliderStyle);
        widthSliderLabel = new Label(null, labelStyle);
        heightSlider = new Slider(4, 20, 1, false, sliderStyle);
        heightSliderLabel = new Label(null, labelStyle);

        shootsSlider = new Slider(1, 15, 1, false, sliderStyle);
        shootsSliderLabel = new Label(null, labelStyle);

        movesSlider = new Slider(0, 16, 1, false, sliderStyle);
        movesSliderLabel = new Label(null, labelStyle);

        addShots = H.getCheckbox(font);
        addShotsLabel = new Label("Additional shots", labelStyle);

        decrField = H.getCheckbox(font);
        decrFieldLabel = new Label("Decreasing field", labelStyle);

        GameConfig.Ship[] ships = GameConfig.Ship.getAllShipsSamples();
        ArrayList<GameConfig.Ship> shipArrayList = config.getShips();
        shipCntChoosers = new ShipCntChooser[ships.length];
        int id = 0;
        for (GameConfig.Ship ship : ships) {
            int cnt = 0;
            for (GameConfig.Ship ship1 : shipArrayList)
                if (ship1.name.equals(ship.name))
                    cnt++;

            shipCntChoosers[id] = new ShipCntChooser(ship, font, cnt, callback.getCallback().getCellSize());
            id++;
        }


        scrollTable = new Table();
        scrollTable.align(Align.center);
        scrollTable.setFillParent(true);
        scrollTable.defaults().uniform();

        scrollPane = new ScrollPane(scrollTable);

        tableContainer = new Container<>(scrollPane);
//        tableContainer.setFillParent(true);
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
                callbackCallback.getRedundantY() + callbackCallback.getFooterHeight(),
                Gdx.graphics.getWidth() - callbackCallback.getRedundantX() * 2 - callbackCallback.getSideWidth() * 2,
                Gdx.graphics.getHeight() - callbackCallback.getRedundantY() * 2 - callbackCallback.getFooterHeight() - callbackCallback.getHeaderHeight());
//        tableContainer.height(tableContainer.getHeight() * 5);
//        scrollPane.setBounds(0, tableContainer.getY(), tableContainer.getWidth(), tableContainer.getHeight());
//        scrollTable.setBounds(1, 1, tableContainer.getWidth() - 2, tableContainer.getHeight() - 2);

        scrollTable.add(new Label("Game mode:", labelStyle));
        Array<TextButton> btns = gameTypeGroup.getButtons();
        for (TextButton btn : new Array.ArrayIterator<>(btns))
            scrollTable.add(btn).fill();
        scrollTable.row();

        scrollTable.add(aiLevelGroupLabel);
        btns = aiLevelGroup.getButtons();
        for (TextButton btn : new Array.ArrayIterator<>(btns))
            scrollTable.add(btn).fill();
        scrollTable.row();

        scrollTable.add(aiLevelGroupLabel2);
        btns = aiLevelGroup2.getButtons();
        for (TextButton btn : new Array.ArrayIterator<>(btns))
            scrollTable.add(btn).fill();
        scrollTable.row();

        int colspan = scrollTable.getColumns() - 1;
        float width = tableContainer.getWidth() / (colspan + 1) * colspan - .5f;
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
//        todo: decreasing field in game
//        scrollTable.add(decrFieldLabel);
//        scrollTable.add(decrField).colspan(colspan).width(width).align(Align.right);
//        addShots.setChecked(config.isDecreasingField());
//        scrollTable.row();

        scrollTable.row().height(callbackCallback.getCellSize() * 2);
        int idx = 0;
        for (ShipCntChooser ship : shipCntChoosers) {
            if (idx + 1 >= scrollTable.getColumns()) {
                idx = 0;
                scrollTable.row();
            }
            ship.buildTable(callbackCallback.getCellSize());
            scrollTable.add(ship).colspan(2).fill();
            idx += 2;
        }

//        scrollTable.row().height(callbackCallback.getFooterHeight() + callbackCallback.getRedundantY() + callbackCallback.getCellSize() * 10);
//        scrollTable.add(new Label(null, labelStyle));
        scrollTable.setHeight(tableContainer.getHeight() * 60);

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
        setVisible(false);
        Gdx.app.debug("ConfigGroup/finish", config.toJSON());
        return config;
    }

    private boolean isValidConfig(@NotNull GameConfig config) {
        return true; //todo: isValidConfig
    }

    public void setVisible(boolean visible) {
        scrollTable.setVisible(visible);
        doneButton.setVisible(visible);
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == DONE_BUTTON_ID) {


            GameConfig mConfig = config.duplicate(null);

            TextButton checked = aiLevelGroup.getChecked();
            if (checked != null) {
                AILevel level = AILevel.getByName(checked.getName());
                if (level != null)
                    mConfig.setAiLevel(level.id);
            }
            checked = aiLevelGroup2.getChecked();
            if (checked != null) {
                AILevel level = AILevel.getByName(checked.getName());
                if (level != null)
                    mConfig.setAiLevel2(level.id);
            }
            checked = gameTypeGroup.getChecked();
            if (checked != null)
                mConfig.setGameType(GameConfig.GameType.valueOf(checked.getName()));
            mConfig.setWidth(Math.round(widthSlider.getValue()));
            mConfig.setHeight(Math.round(heightSlider.getValue()));
            mConfig.setShotsPerTurn(Math.round(shootsSlider.getValue()));
            if (movesSlider.getValue() == movesSlider.getMaxValue())
                mConfig.setMovingPerTurn(-1);
            else {
                mConfig.setMovingPerTurn(Math.round(movesSlider.getValue()));
                if (mConfig.getMovingPerTurn() == 0)
                    mConfig.setMovingEnabled(false);
                else
                    mConfig.setMovingEnabled(true);
            }
            mConfig.setAdditionalShots(addShots.isChecked());

            int id = 1;
            ArrayList<GameConfig.Ship> ships = new ArrayList<>();
            for (ShipCntChooser ship : shipCntChoosers) {
                for (int i = 0; i < ship.getCount(); i++) {
                    ships.add(ship.getShip().clone(id));
                    id++;
                }
            }
            mConfig.setShips(ships);

            if (isValidConfig(mConfig)) {
                mConfig.duplicate(config);
                callback.ConfigIsDone();
                return true;
            }
            return false;
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

        boolean isRightAi = gameTypeGroup.getChecked().getName().equals(GameConfig.GameType.AI_VS_AI.name())
                || gameTypeGroup.getChecked().getName().equals(GameConfig.GameType.AI.name());
        if (isRightAi != isRightAIShown) {
            isRightAIShown = isRightAi;
            Array<TextButton> btns = aiLevelGroup.getButtons();
            for (TextButton btn : new Array.ArrayIterator<>(btns))
                btn.setVisible(isRightAi);
            aiLevelGroupLabel.setVisible(isRightAi);
        }
        boolean isLeftAi = gameTypeGroup.getChecked().getName().equals(GameConfig.GameType.AI_VS_AI.name());
        if (isLeftAi != isLeftAIShown) {
            isLeftAIShown = isLeftAi;
            Array<TextButton> btns = aiLevelGroup2.getButtons();
            for (TextButton btn : new Array.ArrayIterator<>(btns))
                btn.setVisible(isLeftAi);
            aiLevelGroupLabel2.setVisible(isLeftAi);
        }
        //todo
    }
}
