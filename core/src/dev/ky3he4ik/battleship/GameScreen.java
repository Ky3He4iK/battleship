package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.ai.AIThread;

public class GameScreen implements Screen {
    private @NotNull
    AIThread thread;
    private @NotNull
    World player1, player2;
    private final MyGdxGame game;
    private OrthographicCamera camera;
    private Texture background;
    private float totalDelta = 0;

    public GameScreen(final MyGdxGame game) {
        this.game = game;
        background = new Texture("Background_v01.jpg");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();
//        if (deltaTime > 0.05f) // if less then 20 FPS
//            deltaTime = 0.05f;
        totalDelta += deltaTime;
        if (totalDelta < 1) // redraw once per second
            return;
        totalDelta = 0;


        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.debug("GameScreen", "Back button pressed");
//            game.setScreen(new MainScreen(game));
            //todo: main screen
//            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {

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
    public void dispose() {
        background.dispose();
        thread.dispose();

    }
}
