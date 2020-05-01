package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.utils.Constants;

public class Cell extends Actor {
    private final Field field;
    private Sprite sprite;
    private int idx, idy, state = -1;
    private boolean isOpened = false;

    @Nullable
    private Animation<TextureRegion> animation = null;
    private boolean isWaterBlow = false;
    private float animationPassed = 0;

    public int getIdx() {
        return idx;
    }

    public int getIdy() {
        return idy;
    }

    public int getState() {
        return state;
    }

    public Cell(final Field field, final int idx, final int idy) {
        this.field = field;
        this.idx = idx;
        this.idy = idy;

        SpriteManager manager = SpriteManager.getInstance();
        manager.initSprite(Constants.CELL_EMPTY_IMG);
        manager.initSprite(Constants.CELL_UNDAMAGED_IMG);
        manager.initSprite(Constants.CELL_DAMAGED_IMG);
        manager.initSprite(Constants.CELL_SUNK_IMG);
        manager.initSprite(Constants.CELL_CLOSED_IMG);

        updateState(field.getState(idx, idy), field.isOpened(idx, idy));

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT && x >= 1 && y >= 1 && x < getWidth() - 1 && x < getHeight() - 1) {
                    Gdx.app.debug("Cell " + idx + "x" + idy, "Press at " + idx + "x" + idy + " (" + x + "x" + y + "; " + pointer + ")");
                    field.registerClick(idx, idy);
                    return true;
                }
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                int localX = idx;
                int localY = idy;
                while (x > getWidth()) {
                    x -= getWidth();
                    localX++;
                }
                while (y > getHeight()) {
                    y -= getHeight();
                    localY++;
                }
                while (x < 0) {
                    x += getWidth();
                    localX--;
                }
                while (y < 0) {
                    y += getHeight();
                    localY--;
                }
                if (localX >= 0 && localY >= 0 && localX < field.getWorld().getWidth() && localY < field.getWorld().getHeight()) {
                    Gdx.app.debug("Cell " + idx + "x" + idy, "Drag at " + localX + "x" + localY + " (" + x + "x" + y + "; " + pointer + ")");
                    field.registerClick(localX, localY);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    int localX = idx;
                    int localY = idy;
                    while (x > getWidth()) {
                        x -= getWidth();
                        localX++;
                    }
                    while (y > getHeight()) {
                        y -= getHeight();
                        localY++;
                    }
                    while (x < 0) {
                        x += getWidth();
                        localX--;
                    }
                    while (y < 0) {
                        y += getHeight();
                        localY--;
                    }
                    if (localX >= 0 && localY >= 0 && localX < field.getWorld().getWidth() && localY < field.getWorld().getHeight()) {
                        Gdx.app.debug("Cell " + idx + "x" + idy, "Release at " + localX + "x" + localY + " (" + x + "x" + y + "; " + pointer + ")");
                        field.registerRelease(localX, localY);
                    } else
                        field.registerRelease(-1, -1);
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateState(field.getState(idx, idy), field.isOpened(idx, idy));
        int[] click = field.getClick();
        if (click[0] == 1 && (click[1] == idx || click[2] == idy)) {
            if (click[1] == idx && click[2] == idy) {
                batch.setColor(1, 0.5f, 0.5f, 1);
            } else {
                batch.setColor(0.5f, 0.5f, 0.7f, 1);
            }
        } else
            batch.setColor(1, 1, 1, 1);
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        if (animation != null) {
            animationPassed += Gdx.graphics.getDeltaTime();
            if (animation.isAnimationFinished(animationPassed))
                animation = null;
            else {
                TextureRegion currentFrame = animation.getKeyFrame(animationPassed);
//                if (!isWaterBlow)
//                    batch.setColor(1, .6f, .6f, 1);
                batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            }
        }


        batch.setColor(1, 1, 1, 1);
    }

    private void updateState(int state, boolean isOpened) {
        if (state == this.state && isOpened == this.isOpened)
            return;
        this.state = state;
        this.isOpened = isOpened;
        if (isOpened)
            switch (state) {
                case World.STATE_EMPTY:
                    sprite = SpriteManager.getInstance().getSprite(Constants.CELL_EMPTY_IMG);
                    break;
                case World.STATE_UNDAMAGED:
                    sprite = SpriteManager.getInstance().getSprite(Constants.CELL_UNDAMAGED_IMG);
                    break;
                case World.STATE_DAMAGED:
                    sprite = SpriteManager.getInstance().getSprite(Constants.CELL_DAMAGED_IMG);
                    break;
                case World.STATE_SUNK:
                    sprite = SpriteManager.getInstance().getSprite(Constants.CELL_SUNK_IMG);
                    break;
                default:
                    Gdx.app.error("Cell " + idx + "x" + idy, "Invalid state: " + state);
            }
        else
            sprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
    }

    public void dispose() {
        SpriteManager manager = SpriteManager.getInstance();
        manager.dispose(Constants.CELL_EMPTY_IMG);
        manager.dispose(Constants.CELL_UNDAMAGED_IMG);
        manager.dispose(Constants.CELL_DAMAGED_IMG);
        manager.dispose(Constants.CELL_CLOSED_IMG);
    }

    public void blow(boolean isWater) {
        isWaterBlow = isWater;
        if (isWaterBlow)
            animation = AnimationManager.getInstance().getAnimation(Constants.WATER_BLOW_ANIMATION);
        else
            animation = AnimationManager.getInstance().getAnimation(Constants.BLOW_ANIMATION);
        animationPassed = 0;
    }
}
