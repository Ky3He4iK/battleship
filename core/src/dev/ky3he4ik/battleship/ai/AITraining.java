package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.H;

public class AITraining extends AI {
    public AITraining(@Nullable PlayerFinished callback, @NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(callback, enemy, my, config);
    }

    @Override
    protected void placeShips() {
        H.placeShipsRandom(my, config.getShips());
    }

    @Override
    protected void turn() {
        Random random = new Random();
        turnX = random.nextInt(enemy.getHeight());
        turnY = random.nextInt(enemy.getWidth());
        while (enemy.isOpened(turnX, turnY)) {
            turnX = random.nextInt(enemy.getHeight());
            turnY = random.nextInt(enemy.getWidth());
        }
    }
}
