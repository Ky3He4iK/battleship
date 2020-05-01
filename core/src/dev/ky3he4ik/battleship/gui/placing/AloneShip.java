package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.utils.Constants;

public class AloneShip extends Actor {
    @NotNull
    final ShipPlacer callback;

    @NotNull
    private String[] names;

    private int rotation;

    @NotNull
    private Sprite sprite;

    AloneShip(@NotNull final ShipPlacer shipPlacer, @NotNull String name) {
        callback = shipPlacer;
        this.names = new String[]{
                name + Constants.ROTATED_SUFFIX, name
        };
        rotation = World.ROTATION_HORIZONTAL;
        sprite = SpriteManager.getInstance().getSprite(names[rotation]);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    public void rotate() {
        setSize(getHeight(), getWidth());
        rotation = 1 - rotation;
        sprite = SpriteManager.getInstance().getSprite(names[rotation]);
    }
}
