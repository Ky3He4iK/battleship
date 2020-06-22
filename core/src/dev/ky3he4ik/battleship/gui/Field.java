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
import dev.ky3he4ik.battleship.utils.vectors.Vec2d;
import dev.ky3he4ik.battleship.utils.vectors.Vec2dInt;
import dev.ky3he4ik.battleship.utils.vectors.Vec3dInt;

public class Field extends Group implements PlayerFinished, AloneShipListener {
    @NotNull
    private World world;
    @NotNull
    private Cell[][] cells;
    @NotNull
    private ArrayList<AloneShip> childrenShips;

    private int clickX, clickY;
    private boolean clicked = false;
    private boolean showShips;
    private float cellSize;

    private boolean shadow = false;
    private int shadowUX, shadowUY, shadowLX, shadowLY;
    private int shadowRot;
    private int lastAccessId = -1;
    private boolean movingShips = false;

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
        childrenShips = new ArrayList<>();

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
            cellSize = callback.getCellSize();
            for (Cell[] cells1 : cells)
                for (Cell cell : cells1) {
                    removeActor(cell);
                }

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
        if (communication != null)
            communication.restart();
    }

    public void start() {
        for (AloneShip ship : childrenShips)
            removeActor(ship);
        childrenShips.clear();
        for (World.Ship ship : world.getShips()) {
            AloneShip child = new AloneShip(this, ship.convert());
//            child.setRotation(ship.rotation);
            child.setPlaced(true);
            child.setVisible(showShips);
            addActor(child);
            childrenShips.add(child);
            child.setBounds(ship.idx * cellSize, ship.idy * cellSize, cellSize * ship.length, cellSize);
            if (ship.rotation == World.ROTATION_VERTICAL)
                child.rotate();
        }
        if (communication != null)
            communication.setTurn();
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
        for (AloneShip ship : childrenShips)
            if (world.shipDead(ship.id)) {
                ship.setDead();
                World.Ship ship1 = world.findShip(ship.id);
                if (ship1 != null) {
                    ship.setBounds(ship1.idx * cellSize, ship1.idy * cellSize, cellSize * ship.length, cellSize);
                    if (ship1.rotation != ship.getShipRotation())
                        ship.rotate();
                }
                ship.setVisible(true);
            }
    }

    boolean isEmptyCell(int idx, int idy) {
        return world.isEmptyCell(idx, idy);
    }

    public boolean isOpened(int idx, int idy) {
        return world.isCellOpened(idx, idy);
    }

    void registerClick(int idx, int idy) {
        clicked = true;
        clickX = idx;
        clickY = idy;
    }

    void registerRelease(int idx, int idy) {
        clicked = false;
        if (idx < 0 || idy < 0 || idx >= world.getWidth() || idy >= world.getHeight())
            return;
        clickX = idx;
        clickY = idy;
        if (!isOpened(idx, idy) && callback.getOpponent(playerId).getWorld().getShips().size() >= world.getShips().size())
            callback.cellPressed(playerId, idx, idy);
    }

    @NotNull
    Vec3dInt getClick() {
        return new Vec3dInt(clickX, clickY, (H.I(clicked)));
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
                cell.clearActions();
                cell.clearListeners();
                removeActor(cell);
            }
        }
        for (AloneShip child : childrenShips)
            removeActor(child);
    }

    public boolean open(int idx, int idy) {
        ArrayList<Vec2dInt> openedCells = world.open(idx, idy);
        for (Vec2dInt pair : openedCells)
            cells[pair.x][pair.y].blow(world.isEmptyCell(pair.x, pair.y));
        return !world.isEmptyCell(idx, idy);
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
        for (AloneShip child : childrenShips)
            removeActor(child);
    }

    @Nullable
    public Communication getCommunication() {
        return communication;
    }

    public void setCommunication(@Nullable Communication communication) {
        if (this.communication != null)
            this.communication.dispose();
        this.communication = communication;
        if (communication != null)
            communication.setCallback(this);
    }

    public void removeCommunication() {
        if (this.communication != null)
            this.communication.dispose();
        communication = null;
    }

    public void removeShip(int shipId) {
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
    public Vec2d unHighlight(@NotNull GameConfig.Ship ship, float x, float y, int rotation) {
        if (shadow)
            shadow = false;
        shadowLX = innerCellX(x);
        shadowLY = innerCellY(y);
        shadowRot = rotation;
        if (world.placeShip(ship.convert(), shadowLX, shadowLY, shadowRot))
            return new Vec2d(globalCellX(shadowLX), globalCellY(shadowLY));
        return null;
    }

    private int innerCellX(float x) {
        return Math.round((x - H.getAbsCoord(this).x) / cellSize);
    }

    public float globalCellX(int idx) {
        return getX() + cellSize * idx;
    }

    public float globalCellY(int idy) {
        return getY() + cellSize * idy;
    }

    private int innerCellY(float y) {
        return Math.round((y - H.getAbsCoord(this).y) / cellSize);
    }

    boolean getShadow() {
        return shadow;
    }

    int getShadowUX() {
        return shadowUX;
    }

    int getShadowUY() {
        return shadowUY;
    }

    int getShadowLX() {
        return shadowLX;
    }

    int getShadowLY() {
        return shadowLY;
    }

    @Nullable
    public Vec2d rotate(@NotNull AloneShip ship, @NotNull Vec2d coord, int rotation) {
        if (!world.rotate(ship.id)) {
            shadow = false;
            movingShips = true;
            if (world.placeShip(ship.ship.convert(), innerCellX(coord.x), innerCellY(coord.y), rotation)) {
                movingShips = false;
                return new Vec2d(globalCellX(shadowLX), globalCellY(shadowLY));
            }
            movingShips = false;
            return null;
        } else
            return new Vec2d(coord.x, coord.y);
    }

    @Override
    public boolean shipPressed(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        if (ship.isNotPlaced() || (callback.canMove(playerId) && world.shipAlive(ship.id) && callback.getTurn() == playerId && callback.getConfig().getGameType() != GameConfig.GameType.AI_VS_AI)) {
            lastAccessId = ship.id;
            removeShip(ship.id);
            return true;
        } else {
            registerRelease(innerCellX(Gdx.input.getX() - cellSize / 2), innerCellY(Gdx.graphics.getHeight() - Gdx.input.getY() - cellSize / 2));
            return false;
        }
    }

    @Override
    public void shipReleased(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        Vec2d curPos = H.getAbsCoord(this);
//        if (pos[0] + cellSize / 2 < curPos[0] || pos[1] + ship.getHeight() / 2 < curPos[1] || pos[0] > curPos[0] + getWidth() || pos[1] > curPos[1] + getHeight()) {
        ship.setPlaced(false);
        ship.setPosition(Math.min(curPos.x + getWidth() - cellSize, Math.max(curPos.x, pos.x)) - curPos.x,
                Math.min(curPos.y + getHeight() - cellSize, Math.max(curPos.y, pos.y)) - curPos.y);
//        }
        for (AloneShip child : childrenShips) {
            if (child.isNotPlaced()) {
                pos = H.getAbsCoord(child);
                Vec2d newPos = unHighlight(child.ship, pos.x, pos.y, child.getShipRotation());
                if (newPos != null) {
                    callback.registerMove(playerId);
                    child.setPlaced(true);
                    Gdx.app.debug("ShipPlacer", "Ship placed: " + child.id + " (" + child.getShipName() + ")");
                    child.setPosition(newPos.x - curPos.x, newPos.y - curPos.y);
                } else {
                    child.setPlaced(false);
                    child.setPosition(pos.x - curPos.x, pos.y - curPos.y);
                }
            }
        }
    }

    @Override
    public void shipMoved(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        highlight(pos.x, pos.y, ship.getShipRotation(), ship.length);
    }

    @Override
    public boolean isPlaced(int shipId) {
        return world.isPlaced(shipId);
    }

    public void setShowShips(boolean showShips) {
        this.showShips = showShips;
        if (childrenShips.isEmpty()) {
            for (World.Ship ship : world.getShips()) {
                AloneShip child = new AloneShip(this, ship.convert());
                child.setPlaced(true);
                child.setVisible(showShips);
                addActor(child);
                childrenShips.add(child);
                child.setBounds(ship.idx * cellSize, ship.idy * cellSize, cellSize * ship.length, cellSize);
                if (ship.rotation == World.ROTATION_VERTICAL)
                    child.rotate();
            }
        } else
            for (AloneShip ship : childrenShips)
                ship.setVisible(showShips);
    }

    public boolean rotateButtonPressed() {
        if (lastAccessId != -1)
            for (AloneShip child : childrenShips)
                if (child.id == lastAccessId) {
                    if (callback.canMove(playerId)) {
                        shadowLX = innerCellX(child.getGlobalX());
                        shadowLY = innerCellY(child.getGlobalY());
                        child.rotate();
                        Vec2d res = rotate(child, H.getAbsCoord(child), child.getShipRotation());
                        if (res != null) {
                            child.setPlaced(true);
                            child.setPosition(res.x - getX(), res.y - getY());
                            callback.registerMove(playerId);
                            return true;
                        }
                        child.setPlaced(false);
                    }
                    break;
                }
        return false;
    }

    @Override
    public void gotConfig() {
        callback.gotConfig = true;
    }

    @NotNull
    public StepsDirector getCallback() {
        return callback;
    }

    public void moveShip(int shipId, int idx, int idy, int rotation) {
        if (movingShips)
            return;
        for (AloneShip child : childrenShips)
            if (child.id == shipId) {
                child.setPlaced(true);
                child.setVisible(showShips);
                child.setBounds(idx * cellSize, idy * cellSize, cellSize * child.length, cellSize);
                if (rotation != child.getShipRotation())
                    child.rotate();
            }
    }
}
