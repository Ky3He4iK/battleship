package dev.ky3he4ik.battleship.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.logic.GameConfig;

/**
 * Class with several useful functions
 */
public final class H {
    private H() {
    }

    public static void placeShipsRandom(@NotNull World world, @NotNull ArrayList<GameConfig.Ship> availableShips) {
        boolean done = false;
        Random random = new Random();
        do {
            world.reset(world.getWidth(), world.getHeight());
            boolean success = true;
            for (int i = 0; success && i < availableShips.size(); i++) {
                success = false;
                for (int j = 0; !success && j < world.getHeight() * world.getWidth() * 2; j++) {
                    int x = random.nextInt(world.getWidth()), y = random.nextInt(world.getHeight());
                    int rotation = random.nextBoolean() ? World.ROTATION_VERTICAL : World.ROTATION_HORIZONTAL;
                    if (x == world.getWidth() || y == world.getHeight())
                        Gdx.app.error("Helpers/placeShipsRandom", "random.nextInt(bound) == bound!");
                    success = world.placeShip(availableShips.get(i).convert(), x, y, rotation);
                }
                if (!success)
                    Gdx.app.debug("Helpers/placeShipsRandom", "Random placement failed. Using fallback");
                for (int j = 0; !success && j < world.getHeight() * world.getWidth(); j++) {
                    if (random.nextBoolean())
                        success = world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_HORIZONTAL)
                                || world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_VERTICAL);
                    else
                        success = world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_VERTICAL)
                                || world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_HORIZONTAL);
                }
            }
            if (world.getShips().size() == availableShips.size())
                done = true;
            else
                Gdx.app.error("Helpers/placeShipsRandom", "Ships placement failed. Retrying...");
        } while (!done);
    }

    public static void placeShipsLines(@NotNull World world, @NotNull GameConfig config) {
        int idx = 0;
        int idy = 0;
        int step = 0;
        ArrayList<GameConfig.Ship> availableShips = config.getShips();
        for (int i = 0; i < availableShips.size(); ) {
            boolean success = world.placeShip(availableShips.get(i).convert(), idx, idy, World.ROTATION_HORIZONTAL);
            idy += 2;
            step = Math.max(step, availableShips.get(i).length);
            if (idy >= config.getHeight()) {
                idy -= config.getHeight();
                idx += step + 1;
                step = 0;
            }
            if (success)
                i++;
        }
    }

    /**
     * Indicator function. @return `1` if @param i is true and `0` otherwise
     */
    public static int I(boolean i) {
        return i ? 1 : 0;
    }

    @NotNull
    public static float[] getAbsCoord(@Nullable Actor actor) {
        float[] coord = new float[] {0, 0};
        while (actor != null) {
            coord[0] += actor.getX();
            coord[1] += actor.getY();
            actor = actor.getParent();
        }
        return coord;
    }
}
