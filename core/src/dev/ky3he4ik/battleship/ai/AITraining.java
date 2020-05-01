package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.H;

public class AITraining extends AI {
    public AITraining(@NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(null, enemy, my, config);
    }

    @Override
    protected void placeShips() {
        H.placeShipsLines(my, config);
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

    @Override
    public void restart() {

    }
}
