package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

public class AIDummy extends AI {
    public AIDummy(@NotNull PlayerFinished callback, @NotNull World enemy, @NotNull World my) {
        super(callback, enemy, my);
    }

    @Override
    protected void placeShips() {
        int idx = 0;
        for (World.Ship ship : World.SHIPS_AVAILABLE) {
//                    my.placeShip(ship, idx, 0, World.ROTATION_VERTICAL);
            my.placeShip(ship, 2, idx, World.ROTATION_HORIZONTAL);
            idx += 2;
        }
        isPlaceShips = false;
        callback.shipsPlaced();
    }

    @Override
    protected void turn() {
        isMyTurn = false;
        Random random = new Random();
        int i = random.nextInt(enemy.getHeight()), j = random.nextInt(enemy.getWidth());
        while (enemy.isOpened(i, j)) {
            i = random.nextInt(enemy.getHeight());
            j = random.nextInt(enemy.getWidth());
        }
        callback.turnFinished(i, j);
    }

    @Override
    public void setCallback(@NotNull PlayerFinished callback) {
        this.callback = callback;
    }
}
