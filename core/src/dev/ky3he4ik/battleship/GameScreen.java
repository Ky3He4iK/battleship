package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import dev.ky3he4ik.battleship.ai.AIComputationFinished;
import dev.ky3he4ik.battleship.ai.AIThread;

public class GameScreen implements Screen, AIComputationFinished {
    public static final int CELL_MARGIN = 2;

    public static final int CELLS_CNT_X = 25;
    public static final int CELLS_CNT_Y = 15;

    private @NotNull
    AIThread aiThread;
    private @NotNull
    World player1, player2;
    private final MyGdxGame game;

    private Texture background;
    private float totalDelta = 0;

    private int step;
    private float xMargin;
    //    private float middleGap;
    private float yMargin;
    private float timeout;

    private boolean forceRedraw = false;
    private boolean aiFinished = false;
    private boolean p1turn = true;
    private int aiX = -1, aiY = -1;

    private void setConstants() {
        step = (int) Math.min((game.camera.viewportWidth - 1) / CELLS_CNT_X, (game.camera.viewportHeight - 1) / CELLS_CNT_Y);
        xMargin = (game.camera.viewportWidth - step * CELLS_CNT_X) / 2;
        yMargin = (game.camera.viewportHeight - step * CELLS_CNT_Y) / 2;
        if (xMargin * 2 + step * CELLS_CNT_X != game.camera.viewportWidth || yMargin * 2 + step * CELLS_CNT_Y != game.camera.viewportHeight)
            Gdx.app.error("GameScreen", "Screen constants invalid!\nx: {" + xMargin + ", " + step
                    + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
        Gdx.app.debug("GameScreen", "Screen constants:\nx: {" + xMargin + ", " + step
                + "}\ny: {" + yMargin + ", " + step + "}\nScreen: " + game.camera.viewportWidth + "x" + game.camera.viewportHeight);
    }

    private void proceedClick(int x, int y, boolean isMainBtn) {
        int xCell = (int) (x - xMargin) / step - 14;
        int yCell = (int) (y - yMargin) / step - 1;
        Gdx.app.debug("GameScreen", "Touch at " + xCell + "x" + yCell);
        if (xCell < 0 || xCell > 9 || yCell < 0 || yCell > 9 || !p1turn)
            return;
        if (!player2.isOpened(xCell, yCell)) {
            player2.open(xCell, yCell);
            if (!player2.isAlive())
                Gdx.app.error("GameScreen", "P1 won!");
            p1turn = player2.getState(xCell, yCell) != World.STATE_EMPTY;
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

        // actions
        if (!p1turn && aiFinished) {
            if (aiX < 0 || aiY < 0) {
                aiFinished = false;
                aiThread.turn();
                Gdx.app.log("GameScreen", "AI action is incorrect");
                return;
            }
            player1.open(aiX, aiY);
            if (!player1.isAlive())
                Gdx.app.error("GameScreen", "P2 won!");
            p1turn = player1.getState(aiX, aiY) == World.STATE_EMPTY;
            aiFinished = false;
            aiThread.turn();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
            proceedClick(Gdx.input.getX(), (int) (game.camera.viewportHeight - Gdx.input.getY()), true);


        // draw
        game.batch.begin();

//        Gdx.gl.glClearColor(.7f, .7f, 0.5f, 1);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.end();

        // background grid
        Gdx.gl.glLineWidth(.5f);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(.9f, .9f, .9f, .5f);
        for (int i = 0; i <= CELLS_CNT_X; i++)
            game.shapeRenderer.line(xMargin + step * i, 0, xMargin + step * i, game.camera.viewportHeight);
        for (int i = 0; i <= CELLS_CNT_Y; i++)
            game.shapeRenderer.line(0, yMargin + step * i, game.camera.viewportWidth, yMargin + step * i);
        game.shapeRenderer.end();

        Gdx.gl.glLineWidth(GameScreen.CELL_MARGIN * 2);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (p1turn)
            game.shapeRenderer.setColor(0, 0, .8f, 1);
        else
            game.shapeRenderer.setColor(.8f, 0, 0, 1);

        // draw lines
        for (int i = 0; i <= 10; i++) {
            // p1 ||
            game.shapeRenderer.line(xMargin + (i + 2) * step, yMargin + step, xMargin + (i + 2) * step,
                    yMargin + step * 11);

            // p2 ||
            game.shapeRenderer.line(xMargin + (i + 14) * step, yMargin + step,
                    xMargin + (i + 14) * step, yMargin + step * 11);

            // p1 --
            game.shapeRenderer.line(xMargin + 2 * step, yMargin + (i + 1) * step,
                    xMargin + 12 * step, yMargin + (i + 1) * step);

            // p2 --
            game.shapeRenderer.line(xMargin + 14 * step, yMargin + (i + 1) * step,
                    xMargin + 24 * step, yMargin + (i + 1) * step);
        }
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw squares
        HashMap<Integer, Integer> colorMap = new HashMap<>();
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
                game.shapeRenderer.rect(xMargin + (i + 2) * step + CELL_MARGIN,
                        yMargin + (j + 1) * step + CELL_MARGIN, step - CELL_MARGIN * 2, step - CELL_MARGIN * 2);
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
                game.shapeRenderer.rect(xMargin + (i + 14) * step + CELL_MARGIN,
                        yMargin + (j + 1) * step + CELL_MARGIN, step - CELL_MARGIN * 2, step - CELL_MARGIN * 2);
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
        Gdx.app.debug("GameScreen", "Resize to " + width + "x" + height);
        game.camera.setToOrtho(false, width, height);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
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
        aiThread.turn();
    }
}
