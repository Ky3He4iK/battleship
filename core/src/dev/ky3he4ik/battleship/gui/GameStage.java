package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class GameStage extends Stage {
    private Field leftPlayer;
    private Field rightPlayer;
    private final GameConfig config;

    GameStage(final GameConfig config, final World leftWorld, final World rightWorld) {
        super();
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
