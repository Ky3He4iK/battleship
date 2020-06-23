package dev.ky3he4ik.battleship.utils;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;

/**
 * Класс с набором разных полезных функций
 */
public final class H {
    private H() {
    }

    /**
     * Расположить корабли из @param availableShips на поле @param world в случайном порядке
     */
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

    /**
     * Расположить корабли из @param availableShips на поле @param world по рядам.
     *
     * @return успешность
     * todo: более компактное размещение
     */
    public static boolean placeShipsLines(@NotNull World world, @NotNull ArrayList<GameConfig.Ship> availableShips) {
        int idx = 0;
        int idy = 0;
        int step = 0;
        for (int i = 0; i < availableShips.size(); ) {
            boolean success = world.placeShip(availableShips.get(i).convert(), idx, idy, World.ROTATION_HORIZONTAL);
            idy += 2;
            step = Math.max(step, availableShips.get(i).length);
            if (idy >= world.getHeight()) {
                idy -= world.getHeight();
                idx += step + 1;
                step = 0;
            }
            if (success)
                i++;
            else
                return false;
        }
        return true;
    }

    /**
     * Функция-индикатор. Возвращает 1 если i истинно и 0 в противном случае
     * Indicator function. @return `1` if @param i is true and `0` otherwise
     */
    public static int I(boolean i) {
        return i ? 1 : 0;
    }
}
