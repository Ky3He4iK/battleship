package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

abstract public class AI extends Thread implements Communication {
    @Nullable
    protected PlayerFinished callback;

    @NotNull
    protected final World enemy;

    @NotNull
    protected final World my;
    protected boolean isMyTurn;
    protected boolean isPlaceShips;
    protected boolean running;

    protected int turnX, turnY;
    private boolean turn = false;
    private boolean shipsPlaced = false;

    protected AI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my) {
        super();
        this.callback = callback;
        isPlaceShips = false;
        isMyTurn = false;
        running = true;
        this.enemy = enemy;
        this.my = my;
    }

    @Override
    final public void run() {
        while (running) {
            if (isPlaceShips) {
                placeShips();
                if (callback != null)
                    callback.shipsPlaced();
                else
                    shipsPlaced = true;
            }
            if (isMyTurn) {
                turn();
                if (callback != null)
                    callback.turnFinished(turnX, turnY);
                else
                    turn = true;
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

    @Override
    public void setTurn() {
        isMyTurn = true;
    }

    @Override
    public void setPlaceShips() {
        isPlaceShips = true;
    }

    @Override
    public void dispose() {
        running = false;
    }

    @Override
    public void init() {
        start();
    }

    @Override
    public void setCallback(@NotNull PlayerFinished callback) {
        this.callback = callback;
        if (shipsPlaced) {
            callback.shipsPlaced();
            shipsPlaced = false;
        }
        if (turn) {
            callback.turnFinished(turnX, turnY);
            turn = false;
        }
    }
}
