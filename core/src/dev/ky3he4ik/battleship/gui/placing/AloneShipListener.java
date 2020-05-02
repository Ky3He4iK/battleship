package dev.ky3he4ik.battleship.gui.placing;

import org.jetbrains.annotations.NotNull;

public interface AloneShipListener {
    // returns: can be moved
    public boolean shipPressed(@NotNull float[] pos, @NotNull AloneShip ship);

    public void shipReleased(@NotNull float[] pos, @NotNull AloneShip ship);

    public void shipMoved(@NotNull float[] pos, @NotNull AloneShip ship);

    public float getX();

    public float getY();
}
