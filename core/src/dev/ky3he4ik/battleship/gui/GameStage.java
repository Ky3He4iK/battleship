package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class GameStage extends Stage {
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
        leftPlayer = new Field(leftWorld, cellSize, config.getGameType() != GameConfig.GameType.LOCAL_2P);
        rightPlayer = new Field(rightWorld, cellSize, false);

        leftPlayer.setPosition(cellSize, cellSize);
        leftPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        addActor(leftPlayer);

        rightPlayer.setPosition(cellSize * (config.getWidth() + 3), cellSize);
        rightPlayer.setSize(cellSize * config.getWidth(), cellSize * config.getHeight());
        addActor(rightPlayer);
    }

//    @Override
//    public void draw() {
//        super.draw();
//    }
}
