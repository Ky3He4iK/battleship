package dev.ky3he4ik.battleship.gui.placing;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.utils.vectors.Vec2d;

public interface AloneShipListener {
    // returns: can be moved
    boolean shipPressed(@NotNull Vec2d pos, @NotNull AloneShip ship);

    void shipReleased(@NotNull Vec2d pos, @NotNull AloneShip ship);

    void shipMoved(@NotNull Vec2d pos, @NotNull AloneShip ship);

    float getX();

    float getY();

    boolean isPlaced(int shipId);
}
