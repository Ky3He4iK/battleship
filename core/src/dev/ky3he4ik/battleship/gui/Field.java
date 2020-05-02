package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
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

    private boolean shadow = false;
    private int shadowUX, shadowUY, shadowLX, shadowLY;
    private int shadowRot;

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
                cells[i][j].setBounds(cellSize * i, cellSize * j, cellSize, cellSize);
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
                        sprite.getOriginX(), sprite.getOriginY(),
                        sprite.getWidth(), sprite.getHeight(),
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
        clicked = false;
        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                cell.clearAnimation();
    }

    @Nullable
    public Communication getCommunication() {
        return communication;
    }

    public void removeShip(float x, float y, int shipId) {
        world.removeShip(shipId);
    }

    public void highlight(float x, float y, int rotation, int length) {
        shadow = true;
        shadowLX = innerCellX(x);
        shadowLY = innerCellY(y);
        shadowUX = shadowLX + (1 - rotation) * (length - 1);
        shadowUY = shadowLY + rotation * (length - 1);
        shadowRot = rotation;
    }

    @Nullable
    public float[] unHighlight(@NotNull GameConfig.Ship ship) {
        shadow = false;
        if (world.placeShip(ship.convert(), shadowLX, shadowLY, shadowRot))
            return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
        return null;
    }

    @NotNull
    public float[] getRectPos(int idx, int idy) {
        return new float[]{getX() + idx * cellSize, getY() + idy * cellSize};
    }

    private int innerCellX(float x) {
        return Math.round((x - getX()) / cellSize);
    }

    private float globalCellX(int idx) {
        return getX() + cellSize * idx;
    }

    private float globalCellY(int idy) {
        return getY() + cellSize * idy;
    }

    private int innerCellY(float y) {
        return Math.round((y - getY()) / cellSize);
    }

    public boolean getShadow() {
        return shadow;
    }

    public int getShadowUX() {
        return shadowUX;
    }

    public int getShadowUY() {
        return shadowUY;
    }

    public int getShadowLX() {
        return shadowLX;
    }

    public int getShadowLY() {
        return shadowLY;
    }

    @Nullable
    public float[] rotate(@NotNull GameConfig.Ship ship, float x, float y, int rotation) {
        if (!world.rotate(ship.id)) {
            shadow = false;
            if (world.placeShip(ship.convert(), innerCellX(x), innerCellY(y), rotation))
                return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
            return null;
        } else
            return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
    }
}
