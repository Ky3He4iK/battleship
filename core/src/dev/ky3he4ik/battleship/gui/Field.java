package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.World;

public class Field extends Group {
    private final World world;
    Cell cells[][];
    private int clickX, clickY;
    private boolean clicked = false;

    public Field(final World world, float cellSize) {
        this.world = world;
        cells = new Cell[world.getHeight()][];
        for (int i = 0; i < world.getHeight(); i++) {
            cells[i] = new Cell[world.getWidth()];
            for (int j = 0; j < world.getHeight(); j++) {
                cells[i][j] = new Cell(this, i, j);
                cells[i][j].setPosition(cellSize * i, cellSize * j);
                addActor(cells[i][j]);
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

    @Nullable
    public int[] getClick() {
        if (clicked)
            return new int[]{clickX, clickY};
        return null;
    }

    public void clearClick() {
        clicked = false;
    }
}
