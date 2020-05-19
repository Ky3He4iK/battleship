package dev.ky3he4ik.battleship.logic;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.Field;

public interface Communication {
    void setTurn();

    void setPlaceShips();

    void enemyTurned(int x, int y);

    void enemyShipsPlaced();

    void init();

    void dispose();

    void setCallback(@NotNull Field callback);

    void restart();

    void finish();

    boolean isConnected();
}
