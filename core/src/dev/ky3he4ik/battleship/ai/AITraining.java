package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class AITraining extends AI {
    public AITraining(@NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(null, enemy, my, config);
    }

    @Override
    protected void placeShips() {
        int idx = 0;
        int idy = 0;
        int step = 0;
        ArrayList<GameConfig.Ship> availableShips = config.getShips();
        for (int i = 0; i < availableShips.size(); ) {
            boolean success = my.placeShip(availableShips.get(i).convert(), idx, idy, World.ROTATION_HORIZONTAL);
            idy += 2;
            step = Math.max(step, availableShips.get(i).length);
            if (idy >= config.getHeight()) {
                idy -= config.getHeight();
                idx += step + 1;
                step = 0;
            }
            if (success)
                i++;
        }
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
