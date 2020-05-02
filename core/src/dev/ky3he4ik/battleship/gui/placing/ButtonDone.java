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
import dev.ky3he4ik.battleship.utils.Constants;

public class ButtonDone extends Actor implements EventListener {
    @NotNull
    private Sprite sprite;
    @NotNull
    private ShipPlacer callback;

    ButtonDone(@NotNull ShipPlacer callback) {
        this.callback = callback;
        sprite = SpriteManager.getInstance().getSprite(Constants.BUTTON_DONE);
        addListener(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;

        if (event.getType() == InputEvent.Type.touchDown && event.getButton() == Input.Buttons.LEFT) {
            Gdx.app.debug("ButtonDone", "done");
            callback.donePressed();
            return true;
        }
        return false;
    }
}
