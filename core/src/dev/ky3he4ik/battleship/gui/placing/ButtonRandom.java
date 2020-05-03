package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.graphics.g2d.Batch;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.utils.Constants;

public class ButtonRandom extends ActorWithSprite {
    public ButtonRandom(@NotNull ActorWithSpriteListener callback, @NotNull String spriteName, int buttonId) {
        super(callback, spriteName, buttonId);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected void onPress() {
        sprite = SpriteManager.getInstance().getSprite(Constants.BUTTON_RND_SELECTED);
    }

    @Override
    protected void onRelease() {
        sprite = SpriteManager.getInstance().getSprite(Constants.BUTTON_RND);
    }
}
