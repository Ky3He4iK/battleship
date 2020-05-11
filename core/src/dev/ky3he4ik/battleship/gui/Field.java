package dev.ky3he4ik.battleship.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.gui.placing.AloneShip;
import dev.ky3he4ik.battleship.gui.placing.AloneShipListener;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.H;

public class Field extends Group implements PlayerFinished, AloneShipListener {
    @NotNull
    private World world;
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
    private int lastAccessId = -1;
    private int shipsCnt;

    @Nullable
    private Communication communication;
    private int playerId;

    @NotNull
    private StepsDirector callback;

    public Field(@NotNull final World world, float cellSize, @Nullable final Communication communication, int playerId, @NotNull StepsDirector callback) {
        this.world = world;
        this.cellSize = cellSize;
        this.showShips = false;
        this.communication = communication;
        this.playerId = playerId;
        this.callback = callback;
        children = new ArrayList<>();

        if (communication != null)
            communication.setCallback(this);

        cells = new Cell[world.getWidth()][];
        for (int i = 0; i < world.getWidth(); i++) {
            cells[i] = new Cell[world.getHeight()];
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
        if (world.getWidth() != cells.length || (cells.length > 0 && world.getHeight() != cells[0].length)) {
            for (Cell[] cells1 : cells)
                for (Cell cell : cells1)
                    cell.dispose();

            cells = new Cell[world.getWidth()][];
            for (int i = 0; i < world.getWidth(); i++) {
                cells[i] = new Cell[world.getHeight()];
                for (int j = 0; j < world.getHeight(); j++) {
                    cells[i][j] = new Cell(this, i, j);
                    cells[i][j].setBounds(cellSize * i, cellSize * j, cellSize, cellSize);
                    cells[i][j].setVisible(true);
                    addActor(cells[i][j]);
                }
            }
        }
    }

    public void start() {
        shipsCnt = world.getShips().size();
        for (AloneShip ship: children)
            removeActor(ship);
        children.clear();
        for (World.Ship ship : world.getShips()) {
            AloneShip child = new AloneShip(this, ship.convert());
            child.setRotation(ship.rotation);
            child.setPlaced(true);
            child.setVisible(showShips);
            addActor(child);
            children.add(child);
            child.setBounds(ship.idx * cellSize, ship.idy * cellSize, cellSize * ship.length, cellSize);
            if (ship.rotation == World.ROTATION_VERTICAL)
                child.rotate();
        }
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        batch.setColor(1, 1, 1, 0.5f);
//        if (showShips) {
//            for (World.Ship ship : world.getShips())
//                if (!world.shipDead(ship.code)) {
//                    Sprite sprite = SpriteManager.getInstance().getSprite(ship.name);
//                    batch.draw(sprite, getX() + ship.idx * cellSize, getY() + ship.idy * cellSize,
//                            sprite.getOriginX(), sprite.getOriginY(),
//                            sprite.getWidth(), sprite.getHeight(),
//                            1, 1, sprite.getRotation());
//                }
//            for (AloneShip child : children)
//                if (world.shipDead(child.id))
//                    child.setVisible(false);
//        }
//        batch.setColor(1, 0, 0, 0.5f);
        for (AloneShip ship : children)
            if (world.shipDead(ship.id)) {
                ship.setDead();
                ship.setVisible(true);
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
        if (idx < 0 || idy < 0 || idx >= world.getWidth() || idy >= world.getHeight())
            return;
        clickX = idx;
        clickY = idy;
        if (!isOpened(idx, idy) && callback.getOpponent(playerId).getWorld().getShips().size() == shipsCnt)
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
        lastAccessId = -1;
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
        shadowLX = innerCellX(x);
        shadowLY = innerCellY(y);
        shadowRot = rotation;
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
    public float[] rotate(@NotNull AloneShip ship, @NotNull float[] coord, int rotation) {
        if (!world.rotate(ship.id)) {
            shadow = false;
            if (world.placeShip(ship.ship.convert(), innerCellX(coord[0]), innerCellY(coord[1]), rotation))
                return new float[]{globalCellX(shadowLX), globalCellY(shadowLY)};
            return null;
        } else
            return new float[]{coord[0], coord[1]};
    }

    @Override
    public boolean shipPressed(@NotNull float[] pos, @NotNull AloneShip ship) {
        if ((callback.canMove(playerId) || !ship.isPlaced()) && (!ship.isPlaced() || (world.shipAlive(ship.id) && callback.getTurn() == playerId))) {
            lastAccessId = ship.id;
            removeShip(pos[0], pos[1], ship.id);
            return true;
        } else {
            registerRelease(innerCellX(Gdx.input.getX() - cellSize / 2), innerCellY(Gdx.graphics.getHeight() - Gdx.input.getY() - cellSize / 2));
            return false;
        }
    }

    @Override
    public void shipReleased(@NotNull float[] pos, @NotNull AloneShip ship) {
        float[] curPos = H.getAbsCoord(this);
//        if (pos[0] + cellSize / 2 < curPos[0] || pos[1] + ship.getHeight() / 2 < curPos[1] || pos[0] > curPos[0] + getWidth() || pos[1] > curPos[1] + getHeight()) {
        ship.setPlaced(false);
        ship.setPosition(Math.min(curPos[0] + getWidth() - cellSize, Math.max(curPos[0], pos[0])) - curPos[0],
                Math.min(curPos[1] + getHeight() - cellSize, Math.max(curPos[1], pos[1])) - curPos[1]);
//        }
        for (AloneShip child : children) {
            if (!child.isPlaced()) {
                pos = H.getAbsCoord(child);
                float[] newPos = unHighlight(child.ship, pos[0], pos[1], child.getShipRotation());
                if (newPos != null) {
                    callback.registerMove(playerId);
                    child.setPlaced(true);
                    Gdx.app.debug("ShipPlacer", "Ship placed: " + child.id + " (" + child.getShipName() + ")");
                    child.setPosition(newPos[0] - curPos[0], newPos[1] - curPos[1]);
                } else {
                    child.setPlaced(false);
                    child.setPosition(pos[0] - curPos[0], pos[1] - curPos[1]);
                }
            }
        }
    }

    @Override
    public void shipMoved(@NotNull float[] pos, @NotNull AloneShip ship) {
        highlight(pos[0], pos[1], ship.getShipRotation(), ship.length);
    }

    @Override
    public boolean isPlaced(int shipId) {
        return world.isPlaced(shipId);
    }

    public void setShowShips(boolean showShips) {
        this.showShips = showShips;
        for (AloneShip ship : children)
            ship.setVisible(showShips);
    }

    public boolean rotateButtonPressed() {
        if (lastAccessId != -1)
            for (AloneShip child : children)
                if (child.id == lastAccessId) {
                    shadowLX = innerCellX(child.getGlobalX());
                    shadowLY = innerCellY(child.getGlobalY());
                    child.rotate();
                    float[] res = rotate(child, H.getAbsCoord(child), child.getShipRotation());
                    if (res != null) {
                        child.setPlaced(true);
                        child.setPosition(res[0] - getX(), res[1] - getY());
                        return true;
                    }
                    child.setPlaced(false);
                    break;
                }
        return false;
    }
}
