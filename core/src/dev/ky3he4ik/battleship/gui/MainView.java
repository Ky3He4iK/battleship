package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import dev.ky3he4ik.battleship.MyGdxGame;
import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.ai.AIThread;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

public class MainView implements Screen, PlayerFinished {
    private GameStage stage;
    private MyGdxGame game;

    public MainView(final MyGdxGame game) {
        this.game = game;
        GameConfig config = GameConfig.getSampleConfigEast();
        stage = new GameStage(config, new World(config.getWidth(), config.getHeight()), new World(config.getWidth(), config.getHeight()));
        Gdx.input.setInputProcessor(stage);
//        player1 = new World(10, 10);
//        player2 = new World(10, 10);
//        aiThread = new AIThread(this, player1, player2);
//        background = new Texture("Background_v01.jpg");

//        setConstants();
//        aiThread.start();
//        aiThread.placeShips();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.act(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
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
