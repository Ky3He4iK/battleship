package dev.ky3he4ik.battleship.logic;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class World {
    public static final int ROTATION_HORIZONTAL = 0;
    public static final int ROTATION_VERTICAL = 1;
    public static final int EMPTY_CELL = 0x0; // no ship
    private static final int STATE_UNDAMAGED = 0x1; // ship
    private static final int STATE_DAMAGED = 0x2; // damaged ship
    private static final int STATE_SUNK = 0x3; // sunk ship
    // cell states:
    private static final int STATE_MASK = 0xf; // reserved for more states
    private static final int STATE_SHIFT = 0;
    private static final int SHIP_SHIFT = 8;
    private BitSet[] opened;
    private int[][] field;
    private ArrayList<Ship> ships;
    private int width;
    private int height;

    public World(int width, int height) {
        reset(width, height);
    }

    @NotNull
    public static World fromJSON(@NotNull String json) {
        return new Gson().fromJson(json, World.class);
    }

    @Contract(pure = true)
    public ArrayList<Ship> getShips() {
        return ships;
    }

    @Contract(pure = true)
    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    private void setOpened(BitSet[] opened) {
        this.opened = opened;
    }

    public int getWidth() {
        return width;
    }

    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    private void setState(int i, int j, int state) {
        if (inBounds(i, j))
            field[i][j] = field[i][j] & (~STATE_MASK) | (state << STATE_SHIFT);
    }

    @Contract(pure = true)
    public int getState(int i, int j) {
        if (inBounds(i, j))
            return (field[i][j] & STATE_MASK) >> STATE_SHIFT;
        return -1;
    }

    /**
     * Shoot to `i`x`j` cell
     *
     * @return array of opened cells
     */
    @NotNull
    public ArrayList<int[]> open(int i, int j) {
        ArrayList<int[]> openedCells = new ArrayList<>();
        if (!inBounds(i, j) || opened[i].get(j))
            return openedCells;
        opened[i].set(j);
        openedCells.add(new int[]{i, j});
        if (getState(i, j) != EMPTY_CELL) {
            setState(i, j, STATE_DAMAGED);

            // check ship
            outerLoop:
            for (int iter = i + 1; iter < width; iter++) {
                switch (getState(iter, j)) {
                    case STATE_UNDAMAGED:
                        return openedCells;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = i - 1; iter >= 0; iter--) {
                switch (getState(iter, j)) {
                    case STATE_UNDAMAGED:
                        return openedCells;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j + 1; iter < height; iter++) {
                switch (getState(i, iter)) {
                    case STATE_UNDAMAGED:
                        return openedCells;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j - 1; iter >= 0; iter--) {
                switch (getState(i, iter)) {
                    case STATE_UNDAMAGED:
                        return openedCells;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            int x = i, y = j, rot = ROTATION_VERTICAL, len = 1;
            // kill ship
            outerLoop:
            for (int iter = i + 1; iter < width; iter++) {
                switch (getState(iter, j)) {
                    case STATE_DAMAGED:
                        rot = ROTATION_HORIZONTAL;
                        len++;
                        setState(iter, j, STATE_SUNK);
                        break;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = i - 1; iter >= 0; iter--) {
                switch (getState(iter, j)) {
                    case STATE_DAMAGED:
                        x = iter;
                        rot = ROTATION_HORIZONTAL;
                        len++;
                        setState(iter, j, STATE_SUNK);
                        break;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j + 1; iter < height; iter++) {
                switch (getState(i, iter)) {
                    case STATE_DAMAGED:
                        rot = ROTATION_VERTICAL;
                        len++;
                        setState(i, iter, STATE_SUNK);
                        break;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j - 1; iter >= 0; iter--) {
                switch (getState(i, iter)) {
                    case STATE_DAMAGED:
                        y = iter;
                        rot = ROTATION_VERTICAL;
                        len++;
                        setState(i, iter, STATE_SUNK);
                        break;
                    case EMPTY_CELL:
                        break outerLoop;
                }
            }
            setState(i, j, STATE_SUNK);
            for (int it = -1; it <= len; it++) {
                if (rot == ROTATION_VERTICAL) {
                    openedCells.addAll(open(x + 1, y + it));
                    openedCells.addAll(open(x - 1, y + it));
                    openedCells.addAll(open(x, y + it));
                } else {
                    openedCells.addAll(open(x + it, y + 1));
                    openedCells.addAll(open(x + it, y - 1));
                    openedCells.addAll(open(x + it, y));
                }
            }
        }
        return openedCells;
    }

    public boolean isOpened(int idx, int idy) {
        if (inBounds(idx, idy))
            return opened[idx].get(idy);
        return false;
    }

    @Contract(pure = true)
    public boolean isDead() {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.length; i++)
                if (!isOpened(ship.idx + H.I(ship.rotation == ROTATION_HORIZONTAL) * i, ship.idy + H.I(ship.rotation == ROTATION_VERTICAL) * i))
                    return false;
        }
        return true;
    }

    public void reset(int width, int height) {
        this.width = width;
        this.height = height;
        if (field == null || field.length != width)
            field = new int[width][];
        if (opened == null || opened.length != width)
            opened = new BitSet[width];
        if (ships == null)
            ships = new ArrayList<>();
        else
            ships.clear();
        System.gc();

        for (int i = 0; i < width; i++) {
            if (field[i] == null || field.length != height)
                field[i] = new int[height];
            if (opened[i] == null || opened.length != height)
                opened[i] = new BitSet(height);
            else
                opened[i].clear();
            for (int j = 0; j < height; j++)
                field[i][j] = EMPTY_CELL;
        }
    }

    public void reset() {
        reset(width, height);
    }

    public boolean placeShip(Ship ship, int idx, int idy, int rotation) {
        if (!inBounds(idx, idy))
            return false;
        else if (rotation == ROTATION_HORIZONTAL && !inBounds(idx + ship.length - 1, idy))
            return false;
        else if (rotation == ROTATION_VERTICAL && !inBounds(idx, idy + ship.length - 1))
            return false;

        Ship cShip = findShip(ship.code);
        if (cShip != null && idx == cShip.idx && idy == cShip.idy && rotation == cShip.rotation)
            return true;

        removeShip(ship.code);
        for (int i = -1; i <= ship.length; i++) {
            if (rotation == ROTATION_HORIZONTAL) {
                if (cellIsBusy(idx + i, idy) || cellIsBusy(idx + i, idy + 1) || cellIsBusy(idx + i, idy - 1) || (i >= 0 && i < ship.length && isOpened(idx + i, idy)))
                    return false;
            } else {
                if (cellIsBusy(idx, idy + i) || cellIsBusy(idx + 1, idy + i) || cellIsBusy(idx - 1, idy + i) || (i >= 0 && i < ship.length && isOpened(idx, idy + i)))
                    return false;
            }
        }
        ships.add(ship.move(idx, idy, rotation));
        for (int i = 0; i < ship.length; i++) {
            if (rotation == ROTATION_VERTICAL)
                field[idx][idy + i] = (ship.code << SHIP_SHIFT) | STATE_UNDAMAGED;
            else
                field[idx + i][idy] = (ship.code << SHIP_SHIFT) | STATE_UNDAMAGED;
        }
        return true;
    }

    @Contract(pure = true)
    private boolean cellIsBusy(int idx, int idy) {
        return inBounds(idx, idy) && getState(idx, idy) != EMPTY_CELL;
    }

    @Contract(pure = true)
    private boolean inBounds(int idx, int idy) {
        return idx >= 0 && idy >= 0 && idx < width && idy < height;
    }

    public void removeShip(int shipId) {
        for (int i = 0; i < ships.size(); i++)
            if (ships.get(i).code == shipId) {
                Ship ship = ships.get(i);
                for (int j = 0; j < ship.length; j++)
                    field[ship.idx + j * H.I(ship.rotation == ROTATION_HORIZONTAL)][ship.idy + j * H.I(ship.rotation == ROTATION_VERTICAL)] = EMPTY_CELL;
                ships.remove(i);
                return;
            }
    }

    public boolean rotate(int shipId) {
        for (int i = 0; i < ships.size(); i++)
            if (ships.get(i).code == shipId) {
                Ship ship = ships.get(i);
                for (int j = 0; j < ship.length; j++)
                    field[ship.idx + j * H.I(ship.rotation == ROTATION_HORIZONTAL)]
                            [ship.idy + j * H.I(ship.rotation == ROTATION_VERTICAL)] = EMPTY_CELL;
                ships.remove(i);
                return placeShip(ship, ship.idx, ship.idy, 1 - ship.rotation);
            }
        return false;
    }

    @Nullable
    public Ship findShip(int shipId) {
        for (Ship ship : ships)
            if (ship.code == shipId)
                return ship;
        return null;
    }

    public boolean shipAlive(int shipId) {
        Ship ship = findShip(shipId);
        if (ship == null)
            return false;
        for (int i = 0; i < ship.length; i++)
            if (isOpened(ship.getNthX(i), ship.getNthY(i)))
                return true;
        return false;
    }

    public boolean shipDead(int shipId) {
        Ship ship = findShip(shipId);
        if (ship == null)
            return false;
        for (int i = 0; i < ship.length; i++)
            if (!isOpened(ship.getNthX(i), ship.getNthY(i)))
                return false;
        return true;
    }

    public boolean isPlaced(int shipId) {
        for (Ship ship : ships)
            if (ship.code == shipId)
                return true;
        return false;
    }

    public void duplicate(@NotNull World other) {
        other.opened = opened;
        if (!other.ships.equals(ships)) {
            other.ships.clear();
            other.ships.addAll(ships);
        }
        if (!Arrays.deepEquals(other.field, field)) {
            Gdx.app.debug("World", "fields mismatch");
            other.field = field;
        }
        other.width = width;
        other.height = height;
    }

    @NotNull
    public String toJson() {
        return new Gson().toJson(this);
    }

    public static class Ship {
        public final int length;
        public final String name;
        public final int code;
        public final int idx;
        public final int idy;
        public final int rotation;

        public Ship(int length, int code, String name, int idx, int idy, int rotation) {
            this.length = length;
            this.code = code;
            this.name = name;
            this.idx = idx;
            this.idy = idy;
            this.rotation = rotation;
        }

        public Ship copy() {
            return new Ship(length, code, name, idx, idy, rotation);
        }

        Ship move(int idx, int idy, int rotation) {
            String name_ = (name.endsWith(Constants.ROTATED_SUFFIX)) ? name.substring(0, name.length() - Constants.ROTATED_SUFFIX.length()) : name;
            return new Ship(length, code, name_ + (rotation == ROTATION_HORIZONTAL ? Constants.ROTATED_SUFFIX : ""),
                    idx, idy, rotation);
        }

        public boolean containsCell(int i, int j) {
            if (rotation == ROTATION_HORIZONTAL)
                return idy == j && idx <= i && idx + length > i;
            else
                return idx == i && idy <= j && idy + length > j;
        }

        @NotNull
        public GameConfig.Ship convert() {
            String name_ = (name.endsWith(Constants.ROTATED_SUFFIX)) ? name.substring(0, name.length() - Constants.ROTATED_SUFFIX.length()) : name;
            return new GameConfig.Ship(length, code, name_);
        }

        public int getNthX(int i) {
            return H.I(rotation == ROTATION_HORIZONTAL) * i + idx;
        }

        public int getNthY(int i) {
            return H.I(rotation == ROTATION_VERTICAL) * i + idy;
        }
    }
}
