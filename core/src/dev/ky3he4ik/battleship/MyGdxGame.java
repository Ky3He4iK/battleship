package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class MyGdxGame extends Game {
    public static final int WIDTH = 1400;
    public static final int HEIGHT = 700;
    public SpriteBatch batch;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;

    @Override
//    @SuppressWarnings()
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
//        this.setScreen(new MainScreen(this));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MyGdxGame.WIDTH, MyGdxGame.HEIGHT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
