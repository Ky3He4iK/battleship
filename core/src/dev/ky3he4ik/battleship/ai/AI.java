package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

abstract public class AI extends Thread {
    protected PlayerFinished callback;
    protected final World enemy;
    protected final World my;
    protected boolean isMyTurn;
    protected boolean isPlaceShips;
    protected boolean running;
    protected int id;

    protected AI(@NotNull PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, int id) {
        super();
        this.callback = callback;
        isPlaceShips = false;
        isMyTurn = false;
        running = true;
        this.enemy = enemy;
        this.my = my;
        this.id = id;
    }

    @Override
    final public void run() {
        while (running) {
            if (isPlaceShips) {
                placeShips();
            }
            if (isMyTurn) {
                turn();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Gdx.app.error("AI thread", e.getMessage(), e);
            }
        }

    }

    protected abstract void placeShips();

    protected abstract void turn();

    public void updateCallback(@NotNull PlayerFinished callback) {
        this.callback = callback;
    }

    public void setTurn() {
        isMyTurn = true;
    }

    public void setPlaceShips() {
        isPlaceShips = true;
    }

    public void dispose() {
        running = false;
    }
}
