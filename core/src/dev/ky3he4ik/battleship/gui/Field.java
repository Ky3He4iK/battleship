package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.gui.placing.AloneShip;
import dev.ky3he4ik.battleship.gui.placing.AloneShipListener;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.H;

public class Field extends Group implements PlayerFinished, AloneShipListener {
    @NotNull
    private final World world;
    @NotNull
    private Cell[][] cells;
    @NotNull
    private ArrayList<AloneShip> children;

    private int clickX, clickY;
    private boolean clicked = false;
    private boolean showShips;
    private float cellSize;

    private boolean shadow = false;
    private int shadowUX, shadowUY, shadowLX, shadowLY;
    private int shadowRot;
    private int lastAccessId = 0;

    @Nullable
    private Communication communication;
    private int playerId;

    @NotNull
    private GameStage callback;

    public Field(@NotNull final World world, float cellSize, @Nullable final Communication communication, int playerId, @NotNull GameStage callback) {
        this.world = world;
        this.cellSize = cellSize;
        this.showShips = false;
        this.communication = communication;
        this.playerId = playerId;
        this.callback = callback;
        children = new ArrayList<>();

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
        setColor(1, 1, 1, 1);
    }

    public void init() {
        for (World.Ship ship : world.getShips()) {
            AloneShip child = new AloneShip(this, ship.convert());
            child.setRotation(ship.rotation);
            child.setPlaced(true);
            children.add(child);
        }
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
//        for transparency
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        super.draw(batch, parentAlpha);
        batch.setColor(1, 1, 1, 0.5f);
        if (showShips) {
            for (World.Ship ship : world.getShips())
                if (!world.shipDead(ship.code)) {
                    Sprite sprite = SpriteManager.getInstance().getSprite(ship.name);
                    batch.draw(sprite, getX() + ship.idx * cellSize, getY() + ship.idy * cellSize,
                            sprite.getOriginX(), sprite.getOriginY(),
                            sprite.getWidth(), sprite.getHeight(),
                            1, 1, sprite.getRotation());
                }
        }
        batch.setColor(1, 0, 0, 0.5f);
        for (World.Ship ship : world.getShips())
            if (world.shipDead(ship.code)) {
                Sprite sprite = SpriteManager.getInstance().getSprite(ship.name);
                batch.draw(sprite, getX() + ship.idx * cellSize, getY() + ship.idy * cellSize,
                        sprite.getOriginX(), sprite.getOriginY(),
                        sprite.getWidth(), sprite.getHeight(),
                        1, 1, sprite.getRotation());
            }

        batch.setColor(1, 1, 1, 1);
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
        for (AloneShip child : children)
            removeActor(child);
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
        for (AloneShip child : children)
            removeActor(child);
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
    public float[] unHighlight(@NotNull GameConfig.Ship ship, float x, float y, int rotation) {
        if (shadow)
            shadow = false;
        else {
            shadowLX = innerCellX(x);
            shadowLY = innerCellY(y);
            shadowRot = rotation;
        }
        if (world.placeShip(ship.convert(), shadowLX, shadowLY, shadowRot))
            return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
        return null;
    }

    @NotNull
    public float[] getRectPos(int idx, int idy) {
        return new float[]{getX() + idx * cellSize, getY() + idy * cellSize};
    }

    private int innerCellX(float x) {
        return Math.round((x - H.getAbsCoord(this)[0]) / cellSize);
    }

    public float globalCellX(int idx) {
        return getX() + cellSize * idx;
    }

    public float globalCellY(int idy) {
        return getY() + cellSize * idy;
    }

    private int innerCellY(float y) {
        return Math.round((y - H.getAbsCoord(this)[1]) / cellSize);
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
    public float[] rotate(@NotNull GameConfig.Ship ship, @NotNull float[] coord, int rotation) {
        if (!world.rotate(ship.id)) {
            shadow = false;
            if (world.placeShip(ship.convert(), innerCellX(coord[0]), innerCellY(coord[1]), rotation))
                return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
            return null;
        } else
            return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
    }

    @Override
    public boolean shipPressed(@NotNull float[] pos, @NotNull AloneShip ship) {
        if (world.shipAlive(ship.id)) {
            lastAccessId = ship.id - 1;
            removeShip(pos[0], pos[1], ship.id);
            return true;
        }
        return false;
    }

    @Override
    public void shipReleased(@NotNull float[] pos, @NotNull AloneShip ship) {
        float[] newPos = unHighlight(ship.ship, pos[0], pos[1], ship.getShipRotation());
        float[] curPos = H.getAbsCoord(this);
        if (newPos != null) {
            ship.setPlaced(true);
            Gdx.app.debug("ShipPlacer", "Ship placed: " + ship.id + " (" + ship.getShipName() + ")");
            ship.setPosition(newPos[0] - curPos[0], newPos[1] - curPos[1]);
        } else
            ship.setPlaced(false);
    }

    @Override
    public void shipMoved(@NotNull float[] pos, @NotNull AloneShip ship) {
        highlight(pos[0], pos[1], ship.getShipRotation(), ship.length);
    }

    public void setShowShips(boolean showShips) {
        this.showShips = showShips;
    }
}
