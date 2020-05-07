package dev.ky3he4ik.battleship.gui.placing;

import org.jetbrains.annotations.NotNull;

public interface AloneShipListener {
    // returns: can be moved
    boolean shipPressed(@NotNull float[] pos, @NotNull AloneShip ship);

    void shipReleased(@NotNull float[] pos, @NotNull AloneShip ship);

    void shipMoved(@NotNull float[] pos, @NotNull AloneShip ship);

    float getX();

    float getY();

    boolean isPlaced(int shipId);
}
