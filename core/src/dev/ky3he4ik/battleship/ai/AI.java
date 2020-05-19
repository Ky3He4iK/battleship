package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.H;

abstract public class AI extends Thread implements Communication {
    @Nullable
    private PlayerFinished callback;

    @NotNull
    protected World enemy;

    @NotNull
    protected World my;
    private boolean isMyTurn;
    private boolean isPlaceShips;
    private boolean running;

    @NotNull
    protected GameConfig config;

    protected int turnX, turnY;
    private boolean turn = false;
    private boolean shipsPlaced = false;

    protected AI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
        super();
        this.callback = callback;
        isPlaceShips = false;
        isMyTurn = false;
        running = true;
        this.enemy = enemy;
        this.my = my;
        this.config = config;
    }

    @Override
    final public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Gdx.app.error("AI thread", e.getMessage(), e);
        }
        while (running) {
            if (isPlaceShips) {
                placeShips();
                isPlaceShips = false;
                if (callback != null)
                    callback.shipsPlaced();
                else
                    shipsPlaced = true;
            }
            if (isMyTurn) {
                Gdx.app.debug("AIThread", "do turn");
                turn();
                Gdx.app.debug("AIThread", "Shoot at " + turnX + 'x' + turnY);
                isMyTurn = false;
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

    protected void placeShips() {
        H.placeShipsRandom(my, config.getShips());
    }

    protected abstract void turn();

    @Override
    public void setTurn() {
        Gdx.app.debug("AIThread", "set turn");
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

    @Override
    public void restart() {
        running = true;
        if (!isAlive())
            start();
    }

    @Override
    public void enemyTurned(int x, int y) {
    }

    @Override
    public void enemyShipsPlaced() {
    }

    @Override
    public void finish() {
    }
}
