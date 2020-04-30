package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class AIDummy extends AI {
    private int hitX = -1, hitY = -1;

    public AIDummy(@NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(null, enemy, my, config);
    }

    @Override
    protected void placeShips() {
        boolean done = false;
        Random random = new Random();
        do {
            my.reset(my.getWidth(), my.getHeight());
            ArrayList<GameConfig.Ship> availableShips = config.getShips();
            boolean success = true;
            for (int i = 0; success && i < availableShips.size(); i++) {
                success = false;
                for (int j = 0; !success && j < my.getHeight() * my.getWidth() * 2; j++) {
                    int x = random.nextInt(my.getWidth()), y = random.nextInt(my.getHeight());
                    int rotation = random.nextBoolean() ? World.ROTATION_VERTICAL : World.ROTATION_HORIZONTAL;
                    if (x == my.getWidth() || y == my.getHeight())
                        Gdx.app.error("AIDummy", "random.nextInt(bound) == bound!");
                    success = my.placeShip(availableShips.get(i).convert(), x, y, rotation);
                }
                if (!success)
                    Gdx.app.debug("AIDummy", "Random placement failed. Using fallback");
                for (int j = 0; !success && j < my.getHeight() * my.getWidth(); j++) {
                    success = my.placeShip(availableShips.get(i).convert(), j / my.getWidth(), j % my.getHeight(), World.ROTATION_HORIZONTAL)
                            || my.placeShip(availableShips.get(i).convert(), j / my.getWidth(), j % my.getHeight(), World.ROTATION_VERTICAL);
                }
            }
            if (my.getShips().size() == availableShips.size())
                done = true;
            else
                Gdx.app.error("AIDummy", "Ships placement failed. Retrying...");
        } while (!done);
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
