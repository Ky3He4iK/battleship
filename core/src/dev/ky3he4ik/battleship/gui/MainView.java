package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import dev.ky3he4ik.battleship.MyGdxGame;
import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

public class MainView implements Screen {
    private GameStage stage;
    private MyGdxGame game;

    public MainView(final MyGdxGame game) {
        this.game = game;
        GameConfig config = GameConfig.getSampleConfigEast();
        stage = new GameStage(config, new World(config.getWidth(), config.getHeight()), new World(config.getWidth(), config.getHeight()));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.resize(width, height);
        Gdx.app.debug("MainView", "Resize to " + width + "x" + height);
        game.camera.setToOrtho(false, width, height);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        game.batch.setProjectionMatrix(game.camera.combined);
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

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        game.batch.begin();
        game.font.setColor(.3f, .3f, .7f, 1);
        game.font.draw(game.batch, "Mouse: " + Gdx.input.getX() + "x" + Gdx.input.getY() + "\nFPS: " + Gdx.graphics.getFramesPerSecond(), 10, game.camera.viewportHeight - game.font.getCapHeight());
        game.batch.end();
//        if (!aiFinished)
//            game.font.draw(game.batch, "Loading...", 10, game.camera.viewportHeight - 5 * game.font.getCapHeight());
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
}
