package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.H;

public class AIDummy extends AI {
    private int hitX = -1, hitY = -1;
    private Queue<int[]> queue;

    @Override
    public void restart() {
        queue.clear();
        hitX = -1;
    }

    public AIDummy(@NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(null, enemy, my, config);
        queue = new LinkedList<>();

    }

    @Override
    protected void placeShips() {
        H.placeShipsRandom(my, config.getShips());
    }

    @Override
    protected void turn() {
        if (hitX == -1) {
            Random random = new Random();
            while (!queue.isEmpty()) {
                int[] pair = queue.poll();
                if (!enemy.isOpened(pair[0], pair[1])) {
                    turnX = pair[0];
                    turnY = pair[1];
                    rememberCell();
                    return;
                }
            }

            turnX = random.nextInt(enemy.getHeight());
            turnY = random.nextInt(enemy.getWidth());
            while (enemy.isOpened(turnX, turnY)) {
                turnX = random.nextInt(enemy.getHeight());
                turnY = random.nextInt(enemy.getWidth());
            }
            rememberCell();
        } else {
            if (hitX > 0)
                queue.add(new int[]{hitX - 1, turnY});
            if (hitY > 0)
                queue.add(new int[]{hitX, turnY - 1});
            if (hitX + 1 < enemy.getWidth())
                queue.add(new int[]{hitX + 1, turnY});
            if (hitY + 1 < enemy.getHeight())
                queue.add(new int[]{hitX, turnY + 1});
            hitX = -1;
            turn();
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
