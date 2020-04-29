package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.World;

import static dev.ky3he4ik.battleship.World.ROTATION_HORIZONTAL;

public class Field extends Group {
    private final World world;
    private Cell cells[][];
    private int clickX, clickY;
    private boolean clicked = false;
    private boolean showShips;
    private float cellSize;

    public Field(final World world, float cellSize, boolean showShips) {
        this.world = world;
        this.cellSize = cellSize;
        this.showShips = showShips;
        cells = new Cell[world.getHeight()][];
        for (int i = 0; i < world.getHeight(); i++) {
            cells[i] = new Cell[world.getWidth()];
            for (int j = 0; j < world.getHeight(); j++) {
                cells[i][j] = new Cell(this, i, j);
                cells[i][j].setPosition(cellSize * i, cellSize * j);
                addActor(cells[i][j]);
            }
        }
        for (World.Ship ship: world.getShips())
            SpriteManager.getInstance().initSprite(ship.name);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (showShips) {
            for (World.Ship ship: world.getShips()) {
                batch.draw(SpriteManager.getInstance().getSprite(ship.name), getX() + ship.idy * cellSize, getY() + ship.idx, getOriginX(), getOriginY(),
                        getWidth(), getHeight(), getScaleX(), getScaleY(), ship.rotation == ROTATION_HORIZONTAL ? -90 : 0);
            }
        }
    }

    public int getState(int idx, int idy) {
        return world.getState(idx, idy);
    }

    public boolean isOpened(int idx, int idy) {
        return world.isOpened(idx, idy);
    }

    public void registerClick(int idx, int idy) {
        clicked = true;
        clickX = idx;
        clickY = idy;
    }

    public void registerRelease(int idx, int idy) {
        clicked = false;
        clickX = idx;
        clickY = idy;
    }

    @Nullable
    public int[] getClick() {
        return new int[]{(clicked ? 1 : 0), clickX, clickY};
    }

    public void clearClick() {
        clicked = false;
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        for (World.Ship ship: world.getShips())
            SpriteManager.getInstance().dispose(ship.name);
        for (Cell[] cells1: cells) {
            for (Cell cell: cells1) {
                cell.dispose();
                cell.clearActions();
                cell.clearListeners();
                removeActor(cell);
            }
        }
    }
}
