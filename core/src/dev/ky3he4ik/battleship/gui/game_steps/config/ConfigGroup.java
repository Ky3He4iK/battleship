package dev.ky3he4ik.battleship.gui.game_steps.config;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.gui.game_steps.StepConfigure;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ConfigGroup extends Stage {
    @NotNull
    private StepConfigure callback;

    @NotNull
    private GameConfig config;

    @NotNull
    private Table scrollTable;

    @NotNull
    private Table outerTable;

    @NotNull
    ScrollPane scroller;

    @Nullable
    InputProcessor inputProcessor = null;

    public ConfigGroup(@NotNull StepConfigure callback) {
        this.callback = callback;
        config = callback.getConfig();

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
    }

    public void init() {
        inputProcessor = Gdx.input.getInputProcessor();
        setVisible(true);
        Gdx.input.setInputProcessor(this);
        outerTable.setFillParent(true);
        outerTable.add(scroller).fill().expand();
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

    }
}
