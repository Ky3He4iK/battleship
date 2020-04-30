package dev.ky3he4ik.battleship.utils;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class Helpers {
    private Helpers() {
    }

    public static void placeShipsRandom(@NotNull World world, @NotNull GameConfig config) {
        boolean done = false;
        Random random = new Random();
        do {
            world.reset(world.getWidth(), world.getHeight());
            ArrayList<GameConfig.Ship> availableShips = config.getShips();
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
                    success = world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_HORIZONTAL)
                            || world.placeShip(availableShips.get(i).convert(), j / world.getWidth(), j % world.getHeight(), World.ROTATION_VERTICAL);
                }
            }
            if (world.getShips().size() == availableShips.size())
                done = true;
            else
                Gdx.app.error("Helpers/placeShipsRandom", "Ships placement failed. Retrying...");
        } while (!done);
    }
}
