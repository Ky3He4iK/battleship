package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class AloneShip extends Actor implements EventListener {
    @NotNull
    private final ShipPlacer callback;

    @NotNull
    private String[] names;

    private int rotation;

    @NotNull
    private Sprite sprite;

    public final int length;
    public final int id;

    private boolean placed = false;

    AloneShip(@NotNull final ShipPlacer shipPlacer, @NotNull String name, int length, int id) {
        callback = shipPlacer;
        this.length = length;
        this.names = new String[]{
                name + Constants.ROTATED_SUFFIX, name
        };
        this.id = id;
        rotation = World.ROTATION_HORIZONTAL;
        sprite = SpriteManager.getInstance().getSprite(names[rotation]);
        addListener(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!placed)
            batch.setColor(1, .5f, .5f, 1);
        batch.draw(sprite, getX(), getY(), sprite.getOriginX(), sprite.getOriginY(),
                sprite.getWidth(), sprite.getHeight(), 1, 1, sprite.getRotation());
        batch.setColor(1, 1, 1, 1);
    }

    public void rotate() {
        setSize(getHeight(), getWidth());
        rotation = 1 - rotation;
        sprite = SpriteManager.getInstance().getSprite(names[rotation]);
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;

        Vector2 tmpCoords = new Vector2();
        event.toCoordinates(event.getListenerActor(), tmpCoords);

        switch (event.getType()) {
            case touchDown:
                if (event.getButton() == Input.Buttons.LEFT) {
                    callback.pressed(H.getAbsCoord(this), this);
                    return true;
                }
                return false;
            case touchUp:
                if (event.getButton() == Input.Buttons.LEFT) {
                    callback.released(H.getAbsCoord(this), this);
                    return true;
                }
                return false;
            case touchDragged:
                setPosition(Gdx.input.getX() - callback.getX() - getWidth() / 2, (Gdx.graphics.getHeight() - Gdx.input.getY()) - callback.getY() - getHeight() / 2);
                callback.hover(getX(), getY(), this);
                return true;
            case scrolled:
                rotate();
                return true;
            default:
                return false;
        }
    }

    public int getShipRotation() {
        return rotation;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public float getGlobalX() {
        return getX() + getParent().getX();
    }

    public float getGlobalY() {
        return getY() + getParent().getY();
    }

    @NotNull
    public String getShipName() {
        return names[rotation];
    }

    public void setShipRotation(int rotation) {
        if (this.rotation != rotation)
            rotate();
    }
}
