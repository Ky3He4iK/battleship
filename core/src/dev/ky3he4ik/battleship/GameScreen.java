package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.ai.AIComputationFinished;
import dev.ky3he4ik.battleship.ai.AIThread;

public class GameScreen implements Screen, AIComputationFinished {
    public static final int CELL_MARGIN = 2;

    private @NotNull
    AIThread aiThread;
    private @NotNull
    World player1, player2;
    private final MyGdxGame game;

    private Texture background;
    private float totalDelta = 0;

    private int step;
    private float xMargin;
    private float middleGap;
    private float yMargin;
    private float timeout;

    private boolean forceRedraw = false;
    private boolean aiFinished = false;
    private boolean p1turn = true;
    private int aiX = -1, aiY = -1;

    private void setConstants() {
        step = (int) Math.min(game.camera.viewportWidth / 20, game.camera.viewportHeight / 10);
        while ((step & (step - 1)) != 0)
            step = step & (step - 1);
        float restX = game.camera.viewportWidth - step * 20;
        xMargin = restX / 4;
        middleGap = restX / 2 + xMargin + step * 10;
        yMargin = (game.camera.viewportHeight - step * 10) / 2;
        if (xMargin + step * 10 + middleGap != game.camera.viewportWidth || yMargin * 2 + step * 10 != game.camera.viewportHeight)
            Gdx.app.error("GameScreen", "Screen constants invalid!\nx: {" + xMargin + ", " + step + ", " + middleGap
                    + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
        Gdx.app.debug("GameScreen", "Screen constants:\nx: {" + xMargin + ", " + step + ", " + middleGap
                + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
    }

    private void proceedClick(int x, int y, boolean isMainBtn) {
        int xCell = (int) (x - middleGap) / step;
        int yCell = (int) (game.camera.viewportHeight - y - yMargin) / step;
        Gdx.app.debug("GameScreen", "Touch at " + xCell + "x" + yCell);
        if (xCell < 0 || xCell > 9 || yCell < 0 || yCell > 9 || !p1turn)
            return;
        if (!player2.isOpened(xCell, yCell)) {
            player2.open(xCell, yCell);
            if (!player2.isAlive())
                Gdx.app.error("GameScreen", "P1 won!");
            p1turn = player2.getState(xCell, yCell) == World.STATE_EMPTY;
        }
    }

    public GameScreen(final MyGdxGame game) {
        this.game = game;
        player1 = new World(10, 10);
        player2 = new World(10, 10);
        aiThread = new AIThread(this, player1, player2);
//        background = new Texture("Background_v01.jpg");

        setConstants();
        aiThread.start();
        aiThread.placeShips();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        float deltaTime = Gdx.graphics.getDeltaTime();
//        if (deltaTime > 0.05f) // if less then 20 FPS
//            deltaTime = 0.05f;
//        totalDelta += deltaTime;
//        if (totalDelta < 1 / 5f && !forceRedraw) // 5 fps
//            return;
//        totalDelta = 0;
//        forceRedraw = false;
        if (!p1turn && aiFinished) {
            aiFinished = false;
            aiThread.turn();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
            proceedClick(Gdx.input.getX(), Gdx.input.getY(), true);

        game.batch.begin();

        Gdx.gl.glClearColor(.7f, .7f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);

        // draw lines
        for (int i = 0; i <= 10; i++) {
            game.shapeRenderer.line(xMargin + i * step, yMargin, xMargin + i * step,
                    game.camera.viewportHeight - yMargin);

            game.shapeRenderer.line(game.camera.viewportWidth - xMargin - i * step, yMargin,
                    game.camera.viewportWidth - xMargin - i * step, game.camera.viewportHeight - yMargin);

            game.shapeRenderer.line(xMargin, yMargin + i * step,
                    xMargin + 10 * step, yMargin + i * step);

            game.shapeRenderer.line(middleGap, yMargin + i * step,
                    game.camera.viewportWidth - xMargin, yMargin + i * step);
        }
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw squares
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (player1.isOpened(i, j))
                    switch (player1.getState(i, j)) {
                        case World.STATE_EMPTY:
                            game.shapeRenderer.setColor(World.COLOR_EMPTY);
                            break;
                        case World.STATE_UNDAMAGED:
                            game.shapeRenderer.setColor(World.COLOR_UNDAMAGED);
                            break;
                        case World.STATE_DAMAGED:
                            game.shapeRenderer.setColor(World.COLOR_DAMAGED);
                            break;
                        case World.STATE_SUNK:
                            game.shapeRenderer.setColor(World.COLOR_SUNK);
                            break;
                    }
                else
                    game.shapeRenderer.setColor(World.COLOR_UNKNOWN);
                game.shapeRenderer.rect(xMargin + i * step + CELL_MARGIN,
                        yMargin + j * step + CELL_MARGIN, step - CELL_MARGIN * 2, step - CELL_MARGIN * 2);
                if (player2.isOpened(i, j))
                    switch (player2.getState(i, j)) {
                        case World.STATE_EMPTY:
                            game.shapeRenderer.setColor(World.COLOR_EMPTY);
                            break;
                        case World.STATE_UNDAMAGED:
                            game.shapeRenderer.setColor(World.COLOR_UNDAMAGED);
                            break;
                        case World.STATE_DAMAGED:
                            game.shapeRenderer.setColor(World.COLOR_DAMAGED);
                            break;
                        case World.STATE_SUNK:
                            game.shapeRenderer.setColor(World.COLOR_SUNK);
                            break;
                    }
                else
                    game.shapeRenderer.setColor(World.COLOR_UNKNOWN);
                game.shapeRenderer.rect(middleGap + i * step + CELL_MARGIN,
                        yMargin + j * step + CELL_MARGIN, step - CELL_MARGIN * 2, step - CELL_MARGIN * 2);
            }
        }

        game.shapeRenderer.end();

        game.batch.begin();

        game.font.setColor(.3f, .3f, .7f, 1);
        game.font.draw(game.batch, "Mouse: " + Gdx.input.getX() + "x" + Gdx.input.getY() + "\nFPS: " + Gdx.graphics.getFramesPerSecond(), 10, game.camera.viewportHeight - game.font.getCapHeight());
        if (!aiFinished)
            game.font.draw(game.batch, "Loading...", 10, game.camera.viewportHeight - 5 * game.font.getCapHeight());
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
        game.camera.update();
        game.shapeRenderer.updateMatrices();
        game.batch.setProjectionMatrix(game.camera.combined);
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
        aiThread.dispose();

    }

    @Override
    public void aiTurnFinished(int i, int j) {
        aiX = i;
        aiY = j;
        aiFinished = true;
    }

    @Override
    public void aiShipsPlaced() {
        aiFinished = true;
    }
}
