package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActorWithSprite extends Actor implements EventListener {
    @NotNull
    protected Sprite sprite;

    @Nullable
    protected Sprite altSprite;

    @NotNull
    protected ActorWithSpriteListener callback;

    protected int buttonId;

    protected boolean isPressed;

    public ActorWithSprite(@NotNull ActorWithSpriteListener callback, @NotNull String spriteName, @Nullable String altSpriteName, int buttonId) {
        this.callback = callback;
        this.buttonId = buttonId;
        sprite = SpriteManager.getInstance().getSprite(spriteName);
        isPressed = false;
        if (altSpriteName == null)
            altSprite = null;
        else
            altSprite = SpriteManager.getInstance().getSprite(altSpriteName);
        addListener(this);
    }

    public ActorWithSprite(@NotNull ActorWithSpriteListener callback, @NotNull String spriteName, int buttonId) {
        this.callback = callback;
        this.buttonId = buttonId;
        sprite = SpriteManager.getInstance().getSprite(spriteName);
        isPressed = false;
        altSprite = null;
        addListener(this);
    }

    @NotNull
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        if (isPressed && altSprite != null)
            batch.draw(altSprite, getX(), getY(), getWidth(), getHeight());
        else
            batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }

    public int getButtonId() {
        return buttonId;
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
                    isPressed = true;
                    onPress();
                    callback.buttonPressed(buttonId);
                    return true;
                }
                return false;
            case touchUp:
                if (event.getButton() == Input.Buttons.LEFT) {
                    isPressed = false;
                    onRelease();
                    callback.buttonReleased(buttonId);
                    return true;
                }
                return false;
            case touchDragged:
                onDrag();
                callback.buttonMoved(buttonId);
                return true;
            default:
                return false;
        }
    }

    protected void onPress() {
    }

    protected void onRelease() {
    }

    protected void onDrag() {
    }
}
