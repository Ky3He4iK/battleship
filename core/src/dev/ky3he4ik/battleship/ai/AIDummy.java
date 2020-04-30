package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Helpers;

public class AIDummy extends AI {
    private int hitX = -1, hitY = -1;

    public AIDummy(@NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(null, enemy, my, config);
    }

    @Override
    protected void placeShips() {
        Helpers.placeShipsRandom(my, config);
    }

    @Override
    protected void turn() {
        if (hitX == -1) {
            Random random = new Random();
            turnX = random.nextInt(enemy.getHeight());
            turnY = random.nextInt(enemy.getWidth());
            while (enemy.isOpened(turnX, turnY)) {
                turnX = random.nextInt(enemy.getHeight());
                turnY = random.nextInt(enemy.getWidth());
            }
            rememberCell();
        } else {
            // check ship
            for (int iter = hitX + 1; iter < enemy.getWidth(); iter++) {
                if (!enemy.isOpened(iter, hitY)) {
                    turnX = iter;
                    turnY = hitY;
                    rememberCell();
                    return;
                }
            }
            for (int iter = hitX - 1; iter >= 0; iter--) {
                if (!enemy.isOpened(iter, hitY)) {
                    turnX = iter;
                    turnY = hitY;
                    rememberCell();
                    return;
                }
            }
            for (int iter = hitY + 1; iter < enemy.getHeight(); iter++) {
                if (!enemy.isOpened(hitX, iter)) {
                    turnX = iter;
                    turnY = hitY;
                    rememberCell();
                    return;
                }
            }
            for (int iter = hitY - 1; iter >= 0; iter--) {
                if (!enemy.isOpened(hitX, iter)) {
                    turnX = iter;
                    turnY = hitY;
                    rememberCell();
                    return;
                }
            }
        }
    }

    private void rememberCell() {
        if (enemy.getState(turnX, turnY) == World.STATE_UNDAMAGED) {
            hitX = turnX;
            hitY = turnY;
        } else
            hitX = -1;
    }
}
