package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dev.ky3he4ik.battleship.World;

public class AIThread extends Thread {
    private AIComputationFinished callback;
    private final World enemy;
    private final World my;
    private boolean isMyTurn;
    private boolean isPlaceShips;
    private boolean running;

    public AIThread(@NotNull AIComputationFinished callback, @NotNull final World enemy, @NotNull final World my) {
        super();
        this.callback = callback;
        isPlaceShips = false;
        isMyTurn = false;
        running = true;
        this.enemy = enemy;
        this.my = my;
    }

    @Override
    public void run() {
        while (running) {
            if (isPlaceShips) {
                int idx = 0;
                for (World.Ship ship: World.SHIPS_AVAILABLE) {
                    my.placeShip(ship, idx, 0, World.ROTATION_VERTICAL);
                    idx += 2;
                }
                isPlaceShips = false;
                callback.aiShipsPlaced();
            }
            if (isMyTurn) {
                isMyTurn = false;
                Random random = new Random();
                int i = random.nextInt(enemy.getHeight()), j = random.nextInt(enemy.getWidth());
                while (enemy.isOpened(i, j)) {
                    i = random.nextInt(enemy.getHeight());
                    j = random.nextInt(enemy.getWidth());
                }
                callback.aiTurnFinished(i, j);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Gdx.app.error("AI thread", e.getMessage(), e);
            }
        }

    }

    public void updateCallback(@NotNull AIComputationFinished callback) {
        this.callback = callback;
    }

    public void turn() {
        isMyTurn = true;
    }

    public void placeShips() {
        isPlaceShips = true;
    }

    public void dispose() {
        running = false;
    }
}
