package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dev.ky3he4ik.battleship.World;

public class AIDummy extends AI {
    public AIDummy(@NotNull World enemy, @NotNull World my) {
        super(null, enemy, my);
    }

    @Override
    protected void placeShips() {
        int idx = 0;
        for (World.Ship ship : World.SHIPS_AVAILABLE) {
//            my.placeShip(ship, idx, 0, World.ROTATION_VERTICAL);
            my.placeShip(ship, 2, idx, World.ROTATION_HORIZONTAL);
            idx += 2;
        }
        isPlaceShips = false;
    }

    @Override
    protected void turn() {
        isMyTurn = false;
        Random random = new Random();
        turnX = random.nextInt(enemy.getHeight());
        turnY = random.nextInt(enemy.getWidth());
        while (enemy.isOpened(turnX, turnY)) {
            turnX = random.nextInt(enemy.getHeight());
            turnY = random.nextInt(enemy.getWidth());
        }
    }
}
