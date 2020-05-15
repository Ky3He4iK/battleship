package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;

public class AIDummy extends AI {
    private int hitX = -1, hitY = -1;
    private Queue<int[]> queue;

    @Override
    public void restart() {
        super.restart();
        queue.clear();
        hitX = -1;
    }

    public AIDummy(@Nullable PlayerFinished callback, @NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(callback, enemy, my, config);
        queue = new LinkedList<>();
    }

    @Override
    protected void turn() {
        if (hitX == -1) {
            while (!queue.isEmpty()) {
                int[] pair = queue.poll();
                if (!enemy.isOpened(pair[0], pair[1])) {
                    turnX = pair[0];
                    turnY = pair[1];
                    rememberCell();
                    return;
                }
            }

            Random random = new Random();
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

    protected void rememberCell() {
        Gdx.app.debug("AIDummy", "remember cell: " + turnX + "x" + turnY);
        if (enemy.getState(turnX, turnY) == World.STATE_UNDAMAGED) {
            hitX = turnX;
            hitY = turnY;
        } else
            hitX = -1;
    }
}
