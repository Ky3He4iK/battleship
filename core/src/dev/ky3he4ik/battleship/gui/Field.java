package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.utils.H;

public class Field extends Group implements PlayerFinished {
    @NotNull
    private final World world;
    @NotNull
    private Cell[][] cells;
    private int clickX, clickY;
    private boolean clicked = false;
    private boolean showShips;
    private float cellSize;

    @Nullable
    private Communication communication;
    private int playerId;

    @NotNull
    private GameStage callback;

    public Field(@NotNull final World world, float cellSize, boolean showShips, @Nullable final Communication communication, int playerId, @NotNull GameStage callback) {
        this.world = world;
        this.cellSize = cellSize;
        this.showShips = showShips;
        this.communication = communication;
        this.playerId = playerId;
        this.callback = callback;
        if (communication != null)
            communication.setCallback(this);

        cells = new Cell[world.getHeight()][];
        for (int i = 0; i < world.getHeight(); i++) {
            cells[i] = new Cell[world.getWidth()];
            for (int j = 0; j < world.getHeight(); j++) {
                cells[i][j] = new Cell(this, i, j);
                cells[i][j].setPosition(cellSize * i, cellSize * j);
                cells[i][j].setSize(cellSize, cellSize);
                cells[i][j].setVisible(true);
                addActor(cells[i][j]);
            }
        }
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (showShips) {
            for (World.Ship ship : world.getShips()) {
                Sprite sprite = SpriteManager.getInstance().getSprite(ship.name);
                batch.draw(sprite, getX() + ship.idx * cellSize, getY() + ship.idy * cellSize,
                        sprite.getOriginX() * cellSize, sprite.getOriginY() * cellSize,
                        sprite.getWidth() * cellSize, sprite.getHeight() * cellSize,
                        1, 1, sprite.getRotation());

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
        if (idx == -1 || idy == -1)
            return;
        clicked = false;
        clickX = idx;
        clickY = idy;
        if (!isOpened(idx, idy))
            callback.cellPressed(playerId, idx, idy);
    }

    @NotNull
    public int[] getClick() {
        return new int[]{(H.I(clicked)), clickX, clickY};
    }

    public void clearClick() {
        clicked = false;
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    public void dispose() {
        for (World.Ship ship : world.getShips())
            SpriteManager.getInstance().dispose(ship.name);
        for (Cell[] cells1 : cells) {
            for (Cell cell : cells1) {
                cell.dispose();
                cell.clearActions();
                cell.clearListeners();
                removeActor(cell);
            }
        }
    }

    public boolean open(int idx, int idy) {
        ArrayList<int[]> openedCells = world.open(idx, idy);
        for (int[] pair : openedCells)
            cells[pair[0]][pair[1]].blow(world.getState(pair[0], pair[1]) == World.STATE_EMPTY);
        return world.getState(idx, idy) != World.STATE_EMPTY;
    }

    public void setTurn() {
        if (communication != null)
            communication.setTurn();
    }

    public void setPlaceShips() {
        if (communication != null)
            communication.setPlaceShips();
    }

    @Override
    public void turnFinished(int i, int j) {
        callback.turnFinished(playerId, i, j);
    }

    @Override
    public void shipsPlaced() {
        callback.shipsPlaced(playerId);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void restart() {
        if (communication != null)
            communication.setPlaceShips();
        world.reset();
    }

    @Nullable
    public Communication getCommunication() {
        return communication;
    }

}
