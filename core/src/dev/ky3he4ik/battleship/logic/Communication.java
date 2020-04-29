package dev.ky3he4ik.battleship.logic;

import org.jetbrains.annotations.NotNull;

public interface Communication {
    void setTurn();

    void setPlaceShips();

    void init();

    void dispose();

    void setCallback(@NotNull PlayerFinished callback);
}
