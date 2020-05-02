package dev.ky3he4ik.battleship.gui.placing;

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
import dev.ky3he4ik.battleship.utils.Constants;

public class RotateButton extends Actor implements EventListener {
    @NotNull
    private ShipPlacer callback;
    @NotNull
    private Sprite sprite;

    public RotateButton(@NotNull ShipPlacer callback) {
        this.callback = callback;
        this.sprite = SpriteManager.getInstance().getSprite(Constants.ARROW_ROTATE);
        addListener(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;

        Vector2 tmpCoords = new Vector2();
        event.toCoordinates(event.getListenerActor(), tmpCoords);

        if (event.getType() == InputEvent.Type.touchDown && event.getButton() == Input.Buttons.LEFT) {
            callback.rotate();
            return true;
        }
        return false;
    }
}
