package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.utils.Constants;

public class MainView implements Screen, PlayerFinished {
    private Stage stage;

    public void create() {
        stage = new Stage(new ExtendViewport(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        // See below for what true means.
        stage.getViewport().update(width, height, true);
    }

    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void aiTurnFinished(int i, int j) {

    }

    @Override
    public void aiShipsPlaced() {

    }
}
