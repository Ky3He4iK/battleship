package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.ai.AIThread;

public class GameScreen implements Screen {
    private @NotNull
    AIThread thread;
    private @NotNull
    World player1, player2;
    private final MyGdxGame game;

    private Texture background;
    private float totalDelta = 0;

    private int step;
    private float xMargin;
    private float middleGap;
    private float yMargin;

    private boolean forceRedraw = false;

    private void setConstants() {
        step = (int) Math.min(game.camera.viewportWidth / 20, game.camera.viewportHeight / 10);
        while ((step & (step - 1)) != 0)
            step = step & (step - 1);
        float restX = game.camera.viewportWidth - step * 20;
        xMargin = restX / 4;
        middleGap = restX / 2;
        yMargin = (game.camera.viewportHeight - step * 10) / 2;
        if (xMargin * 2 + step * 20 + middleGap != game.camera.viewportWidth || yMargin * 2 + step * 10 != game.camera.viewportHeight)
            Gdx.app.error("GameScreen", "Screen constants invalid!\nx: {" + xMargin + ", " + step + ", " + middleGap
                    + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
        Gdx.app.debug("GameScreen", "Screen constants:\nx: {" + xMargin + ", " + step + ", " + middleGap
                + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
    }

    public GameScreen(final MyGdxGame game) {
        this.game = game;
//        background = new Texture("Background_v01.jpg");

        setConstants();
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
        if (totalDelta < 1 / 5f && !forceRedraw) // 5 fps
            return;
        totalDelta = 0;
        forceRedraw = false;

        game.batch.begin();

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(.3f, .3f, 1f, 1f);

        for (int i = 0; i <= 10; i++) {
            game.shapeRenderer.line(xMargin + i * step, yMargin, xMargin + i * step,
                    game.camera.viewportHeight - yMargin);

            game.shapeRenderer.line(game.camera.viewportWidth - xMargin - i * step, yMargin,
                    game.camera.viewportWidth - xMargin - i * step, game.camera.viewportHeight - yMargin);

            game.shapeRenderer.line(xMargin, yMargin + i * step,
                    xMargin + 10 * step, yMargin + i * step);

            game.shapeRenderer.line(xMargin + 10 * step + middleGap, yMargin + i * step,
                    game.camera.viewportWidth - xMargin, yMargin + i * step);
        }


        game.shapeRenderer.end();
        game.font.setColor(1, 0, 0f, 1);
        game.font.draw(game.batch, "Mouse: " + Gdx.input.getX() + "x" + Gdx.input.getY(), game.camera.viewportWidth / 2, game.camera.viewportHeight / 2);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.debug("GameScreen", "Back button pressed");
//            game.setScreen(new MainScreen(game));
            //todo: main screen
//            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {
        game.camera.setToOrtho(false, width, height);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        setConstants();
        forceRedraw = true;
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
//        background.dispose();
        thread.dispose();

    }
}
