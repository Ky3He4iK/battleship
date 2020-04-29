package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dev.ky3he4ik.battleship.gui.MainView;
import dev.ky3he4ik.battleship.utils.Constants;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;

//    public Viewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.APP_WIDTH, Constants.APP_HEIGHT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
//        viewport = new ExtendViewport(WIDTH, HEIGHT, camera);

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        if (Constants.DEBUG_MODE)
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
//        this.setScreen(new MainScreen(this));
        Gdx.gl.glLineWidth(Constants.CELL_MARGIN * 2);
//        setScreen(new GameScreen(this));
        setScreen(new MainView(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
