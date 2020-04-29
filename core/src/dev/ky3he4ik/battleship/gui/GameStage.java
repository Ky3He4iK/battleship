package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.utils.Constants;

public class GameStage extends Stage implements PlayerFinished {
    @NotNull
    private Field leftPlayer;
    @NotNull
    private Field rightPlayer;
    @NotNull
    private final GameConfig config;

    GameStage(@NotNull final GameConfig config, final World leftWorld, final World rightWorld) {
        super(new ExtendViewport(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        this.config = config;
        float cellSize = Math.min(getWidth() / (config.getWidth() * 2 + 4), getHeight() / (config.getHeight() + 3));
        float redundantX = (getWidth() - cellSize * (config.getWidth() * 2 + 4)) / 2;
        float redundantY = (getHeight() - cellSize * (config.getHeight() + 3)) / 2;

        Gdx.app.debug("GameStage/init", "cellSize = " + cellSize);

        leftPlayer = new Field(leftWorld, cellSize, config.getGameType() != GameConfig.GameType.LOCAL_2P);
        rightPlayer = new Field(rightWorld, cellSize, false);

        leftPlayer.setPosition(redundantX + cellSize, redundantY + cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        leftPlayer.setVisible(true);
        addActor(leftPlayer);

        rightPlayer.setPosition(redundantX + cellSize * (config.getWidth() + 3), redundantY + cellSize);
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setVisible(true);
        addActor(rightPlayer);
    }

    @Override
    public void dispose() {
        super.dispose();
        leftPlayer.dispose();
        leftPlayer.clearActions();
        leftPlayer.clearListeners();
        rightPlayer.clearActions();
        rightPlayer.clearListeners();
        rightPlayer.dispose();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        float cellSize = Math.min(getWidth() / (config.getWidth() * 2 + 4), getHeight() / (config.getHeight() + 3));
        float redundantX = (getWidth() - cellSize * (config.getWidth() * 2 + 4)) / 2;
        float redundantY = (getHeight() - cellSize * (config.getHeight() + 3)) / 2;

        leftPlayer.setPosition(redundantX + cellSize, redundantY + cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        rightPlayer.setPosition(redundantX + cellSize * (config.getWidth() + 3), redundantY + cellSize);
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
    }

    @Override
    public void aiTurnFinished(int playerId, int i, int j) {

    }

    @Override
    public void aiShipsPlaced(int playerId) {

    }
}
