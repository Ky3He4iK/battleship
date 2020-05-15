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

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class AloneShip extends Actor implements EventListener {
    @NotNull
    private final AloneShipListener callback;

    @NotNull
    private String[] names;

    private int rotation;

    @NotNull
    private Sprite sprite;

    @NotNull
    public final GameConfig.Ship ship;

    public final int length;
    public final int id;

    private boolean placed = false;
    private boolean canBeMoved = false;
    private boolean dead = false;

    public AloneShip(@NotNull final AloneShipListener callback, @NotNull final GameConfig.Ship ship) {
        this.callback = callback;
        this.ship = ship;
        this.length = ship.length;
        this.names = new String[]{
                ship.rotatedName(), ship.name
        };
        this.id = ship.id;
        rotation = World.ROTATION_HORIZONTAL;
        sprite = SpriteManager.getInstance().getSprite(names[rotation]);
        addListener(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!placed || dead)
            batch.setColor(getColor());
        else
            setPlaced(callback.isPlaced(id));
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
                    canBeMoved = callback.shipPressed(H.getAbsCoord(this), this);
                    return true;
                }
                return false;
            case touchUp:
                if (event.getButton() == Input.Buttons.LEFT && canBeMoved && !dead) {
                    callback.shipReleased(H.getAbsCoord(this), this);
                    return true;
                }
                return false;
            case touchDragged:
                if (canBeMoved && !dead) {
                    float[] pos = H.getAbsCoord(this);
                    float mouseX = Math.max(Math.min(Gdx.input.getX(), Gdx.graphics.getWidth()), 0);
                    float mouseY = Math.max(Math.min(Gdx.graphics.getHeight() - Gdx.input.getY(), Gdx.graphics.getHeight()), 0);
                    setPosition(mouseX - pos[0] + getX() - getWidth() / 2,
                            mouseY - pos[1] + getY() - getHeight() / 2);
                    callback.shipMoved(H.getAbsCoord(this), this);
                }
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
        if (placed)
            setColor(1, .5f, .5f, .7f);
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

    public void setDead() {
        dead = true;
        setColor(1, .7f, .7f, .7f);
    }
}
