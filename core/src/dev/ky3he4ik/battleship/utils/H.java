package dev.ky3he4ik.battleship.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.vectors.Vec2d;

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
     * Indicator function. @return `1` if @param i is true and `0` otherwise
     */
    public static int I(boolean i) {
        return i ? 1 : 0;
    }

    @NotNull
    public static dev.ky3he4ik.battleship.utils.vectors.Vec2d getAbsCoord(@Nullable Actor actor) {
        dev.ky3he4ik.battleship.utils.vectors.Vec2d coord = new Vec2d(0, 0);
        while (actor != null) {
            coord.x += actor.getX();
            coord.y += actor.getY();
            actor = actor.getParent();
        }
        return coord;
    }

    public static void setBoundsByWidth(@NotNull ActorWithSprite actor, float x, float y, float width) {
        float scale = actor.getSprite().getWidth() / width;
        actor.setBounds(x, y, width, actor.getSprite().getHeight() / scale);
    }

    public static void setBoundsByHeight(@NotNull ActorWithSprite actor, float x, float y, float height) {
        float scale = actor.getSprite().getHeight() / height;
        actor.setBounds(x, y, actor.getSprite().getWidth() / scale, height);
    }

    @NotNull
    public static SpriteDrawable getSpriteDrawable(@NotNull String spriteName) {
        SpriteDrawable drawable = new SpriteDrawable(SpriteManager.getInstance().getSprite(spriteName));
        drawable.setMinHeight(Gdx.graphics.getHeight() / 10f);
        drawable.setMinWidth(Gdx.graphics.getWidth() / 10f);
        return drawable;
    }

    @NotNull
    public static SpriteDrawable getSpriteDrawableMinimized(@NotNull String spriteName) {
        SpriteDrawable drawable = new SpriteDrawable(SpriteManager.getInstance().getSprite(spriteName));
        drawable.setMinHeight(0);
        drawable.setMinWidth(0);
        return drawable;
    }

    @NotNull
    public static CheckBox getCheckbox(@NotNull BitmapFont font) {
        return new CheckBox(null, new CheckBox.CheckBoxStyle(getSpriteDrawable(Constants.BUTTON_DONE_FRAME), getSpriteDrawable(Constants.BUTTON_DONE_SELECTED), font, font.getColor()));
    }
}
