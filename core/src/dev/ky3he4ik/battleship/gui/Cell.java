package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import dev.ky3he4ik.battleship.World;

public class Cell extends Actor {
    private final Field field;
    private TextureRegion region;
    private int idx, idy, state = -1;
    private boolean isOpened = false;

    public int getIdx() {
        return idx;
    }

    public int getIdy() {
        return idy;
    }

    public int getState() {
        return state;
    }

    public Cell(final Field field, int idx, int idy) {
        this.field = field;
        this.idx = idx;
        this.idy = idy;

        region = new TextureRegion();
        updateState(field.getState(idx, idy), field.isOpened(idx, idy));

        setBounds(region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight());

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateState(field.getState(idx, idy), field.isOpened(idx, idy));
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private void updateState(int state, boolean isOpened) {
        if (state == this.state && isOpened == this.isOpened)
            return;
        this.state = state;
        this.isOpened = isOpened;
        if (isOpened)
        switch (state) {
            case World.STATE_EMPTY:
                region.setTexture(SpriteManager.getInstance().getSprite("cell_empty.png").getTexture());
                break;
            case World.STATE_UNDAMAGED:
                region.setTexture(SpriteManager.getInstance().getSprite("cell_undamaged.png").getTexture());
                break;
            case World.STATE_DAMAGED:
            case World.STATE_SUNK:
                region.setTexture(SpriteManager.getInstance().getSprite("cell_hit.png").getTexture());
                break;
            default:
                Gdx.app.error("Cell " + idx + "x" + idy, "Invalid state: " + state);
        }
        else
            region.setTexture(SpriteManager.getInstance().getSprite("cell_closed.png").getTexture());
    }
}
