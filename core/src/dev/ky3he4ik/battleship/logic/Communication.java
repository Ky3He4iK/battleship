package dev.ky3he4ik.battleship.logic;

import org.jetbrains.annotations.NotNull;

public interface Communication {
    void setTurn();

    void setPlaceShips();

    void enemyTurned(int x, int y);

    void enemyShipsPlaced();

    void init();

    void dispose();

    void setCallback(@NotNull PlayerFinished callback);

    void restart();

    void finish();
}
