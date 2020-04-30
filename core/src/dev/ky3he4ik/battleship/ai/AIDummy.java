package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

public class AIDummy extends AI {
    protected AIDummy(@Nullable PlayerFinished callback, @NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(callback, enemy, my, config);
    }

    @Override
    protected void placeShips() {

    }

    @Override
    protected void turn() {

    }
}
