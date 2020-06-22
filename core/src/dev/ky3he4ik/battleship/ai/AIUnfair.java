package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;

public class AIUnfair extends AIDummy {
    protected AIUnfair(@Nullable PlayerFinished callback, @NotNull World enemy, @NotNull World my, @NotNull GameConfig config) {
        super(callback, enemy, my, config);
    }

    @Override
    protected void turn() {
        if (new Random().nextInt(128) < 200)
            super.turn();
        else {
            for (int i = 0; i < enemy.getWidth(); i++)
                for (int j = 0; j < enemy.getHeight(); j++)
                    if (!enemy.isOpened(i, j) && enemy.getState(i, j) == World.EMPTY_CELL) {
                        turnX = i;
                        turnY = j;
                        rememberCell();
                        return;
                    }
            turnX = 0;
            turnY = 0;
        }
    }
}
