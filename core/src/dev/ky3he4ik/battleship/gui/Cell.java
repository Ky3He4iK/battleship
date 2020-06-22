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

import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.vectors.Vec3dInt;

public class Cell extends Actor {
    private final Field field;
    private Sprite sprite;
    private int idx, idy;
    private boolean isOpened = false;
    private boolean isEmpty = false;

    @Nullable
    private Animation<TextureRegion> animation = null;
    private float animationPassed = 0;

    Cell(final Field field, final int idx, final int idy) {
        this.field = field;
        this.idx = idx;
        this.idy = idy;

        SpriteManager manager = SpriteManager.getInstance();
        manager.initSprite(Constants.CELL_EMPTY_IMG);
        manager.initSprite(Constants.CELL_DAMAGED_IMG);
        manager.initSprite(Constants.CELL_CLOSED_IMG);

        updateState(field.isEmptyCell(idx, idy), field.isOpened(idx, idy));

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
        updateState(field.isEmptyCell(idx, idy), field.isOpened(idx, idy));
        Vec3dInt click = field.getClick();
        if (click.z == 1 && (click.x == idx || click.y == idy)) {
            if (click.x == idx && click.y == idy)
                batch.setColor(1, 0.5f, 0.5f, 1);
            else
                batch.setColor(0.5f, 0.5f, 0.7f, 1);
        } else if (field.getShadow() && field.getShadowLX() <= idx && idx <= field.getShadowUX()
                && field.getShadowLY() <= idy && idy <= field.getShadowUY())
            batch.setColor(0.3f, 0.3f, 0.3f, 1);
        else
            batch.setColor(1, 1, 1, 1);
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
        if (animation != null) {
            animationPassed += Gdx.graphics.getDeltaTime();
            if (animation.isAnimationFinished(animationPassed))
                animation = null;
            else
                batch.draw(animation.getKeyFrame(animationPassed), getX(), getY(), getWidth(), getHeight());
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void updateState(boolean isEmpty, boolean isOpened) {
        if (isEmpty == this.isEmpty && isOpened == this.isOpened)
            return;
        this.isEmpty = isEmpty;
        this.isOpened = isOpened;
        if (isOpened) {
            if (isEmpty)
                sprite = SpriteManager.getInstance().getSprite(Constants.CELL_EMPTY_IMG);
            else
                sprite = SpriteManager.getInstance().getSprite(Constants.CELL_DAMAGED_IMG);
        } else
            sprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
    }

    void blow(boolean isWater) {
        if (isWater)
            animation = AnimationManager.getInstance().getAnimation(Constants.WATER_BLOW_ANIMATION);
        else
            animation = AnimationManager.getInstance().getAnimation(Constants.BLOW_ANIMATION);
        animationPassed = 0;
    }

    void clearAnimation() {
        animation = null;
    }
}
