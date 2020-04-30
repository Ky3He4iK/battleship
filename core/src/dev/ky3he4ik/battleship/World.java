package dev.ky3he4ik.battleship;

import com.badlogic.gdx.graphics.Color;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.utils.Constants;

public class World {
    public static class Ship {
        public final int len;
        public final String name;
        public final int code;
        public final int idx;
        public final int idy;
        public final int rotation;


        public Ship(int len, int code, String name, int idx, int idy, int rotation) {
            this.len = len;
            this.code = code;
            this.name = name;
            this.idx = idx;
            this.idy = idy;
            this.rotation = rotation;
        }

        public Ship copy() {
            return new Ship(len, code, name, idx, idy, rotation);
        }

        public Ship move(int idx, int idy, int rotation) {
            return new Ship(len, code, name, idx, idy, rotation);
        }

        public boolean containsCell(int i, int j) {
            if (rotation == ROTATION_HORIZONTAL)
                return idy == j && idx <= i && idx + len > i;
            else
                return idx == i && idy <= j && idy + len > j;
        }
    }

    public static final int ROTATION_HORIZONTAL = 0;
    public static final int ROTATION_VERTICAL = 1;

    public static Color COLOR_UNKNOWN = new Color(.3f, .3f, .3f, 1);
    public static Color COLOR_EMPTY = new Color(0, 0, .5f, 1);
    public static Color COLOR_UNDAMAGED = new Color(0, 1, 0, 1);
    public static Color COLOR_DAMAGED = new Color(.5f, 0, 0, 1);
    public static Color COLOR_SUNK = new Color(1, 0, 0, 1);

    // cell states:
    public static final int STATE_MASK = 0xf; // reserved for more states
    public static final int STATE_SHIFT = 0;

    public static final int STATE_EMPTY = 0x0; // no ship
    public static final int STATE_UNDAMAGED = 0x1; // ship
    public static final int STATE_DAMAGED = 0x2; // damaged ship
    public static final int STATE_SUNK = 0x3; // sunk ship

    // ship codes
    public static final int SHIP_MASK = 0xff00; // reserved for more ships
    public static final int SHIP_SHIFT = 8;

    public static final int SHIP_NOSHIP = 0x000; // literally no ship;
    public static final int SHIP_CARRIER = 0x100;
    public static final int SHIP_BATTLESHIP = 0x200;
    public static final int SHIP_DESTROYER = 0x300;
    public static final int SHIP_SUBMARINE = 0x400;
    public static final int SHIP_PATROL_BOAT = 0x500;

    public static final int INVALID = -1;

    public static final Ship[] SHIPS_AVAILABLE = {new Ship(5, SHIP_CARRIER, Constants.SHIP_CARRIER_IMG, 0, 0, 0),
            new Ship(4, SHIP_BATTLESHIP, Constants.SHIP_BATTLESHIP_IMG, 0, 0, 0),
            new Ship(3, SHIP_DESTROYER, Constants.SHIP_DESTROYER_IMG, 0, 0, 0),
            new Ship(3, SHIP_SUBMARINE, Constants.SHIP_SUBMARINE_IMG, 0, 0, 0),
            new Ship(2, SHIP_PATROL_BOAT, Constants.SHIP_PATROL_BOAT_IMG, 0, 0, 0),
    };

    private BitSet[] opened;
    private int[][] field;
    private ArrayList<Ship> ships;
    private int width;
    private int height;

    @Contract(pure = true)
    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    @Contract(pure = true)
    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    @Contract(pure = true)
    public BitSet[] getOpened() {
        return opened;
    }

    public void setOpened(BitSet[] opened) {
        this.opened = opened;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public World(int width, int height) {
        reset(width, height);
    }

    @Contract(pure = true)
    public World copy() {
        World child = new World(width, height);
        child.setOpened(opened);
        child.setField(field);
        return child;
    }

    public void setState(int i, int j, int state) {
        if (inBounds(i, j))
            field[i][j] = field[i][j] & (~STATE_MASK) | (state << STATE_SHIFT);
    }

    @Contract(pure = true)
    public int getState(int i, int j) {
        if (inBounds(i, j))
            return (field[i][j] & STATE_MASK) >> STATE_SHIFT;
        return -1;
    }

    public void open(int i, int j) {
        if (!inBounds(i, j) && opened[i].get(j))
            return;
        opened[i].set(j);
        if (getState(i, j) == STATE_UNDAMAGED) {
            setState(i, j, STATE_DAMAGED);

            // check ship
            outerLoop:
            for (int iter = i + 1; iter < width; iter++) {
                switch (getState(iter, j)) {
                    case STATE_UNDAMAGED:
                        return;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = i - 1; iter >= 0; iter--) {
                switch (getState(iter, j)) {
                    case STATE_UNDAMAGED:
                        return;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j + 1; iter < height; iter++) {
                switch (getState(i, iter)) {
                    case STATE_UNDAMAGED:
                        return;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j - 1; iter >= 0; iter--) {
                switch (getState(i, iter)) {
                    case STATE_UNDAMAGED:
                        return;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }

            // kill ship
            outerLoop:
            for (int iter = i + 1; iter < width; iter++) {
                switch (getState(iter, j)) {
                    case STATE_DAMAGED:
                        setState(iter, j, STATE_SUNK);
                        break;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = i - 1; iter >= 0; iter--) {
                switch (getState(iter, j)) {
                    case STATE_DAMAGED:
                        setState(iter, j, STATE_SUNK);
                        break;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j + 1; iter < height; iter++) {
                switch (getState(i, iter)) {
                    case STATE_DAMAGED:
                        setState(i, iter, STATE_SUNK);
                        break;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            outerLoop:
            for (int iter = j - 1; iter >= 0; iter--) {
                switch (getState(i, iter)) {
                    case STATE_DAMAGED:
                        setState(i, iter, STATE_SUNK);
                        break;
                    case STATE_EMPTY:
                        break outerLoop;
                }
            }
            setState(i, j, STATE_SUNK);
        }
    }

    public boolean isOpened(int idx, int idy) {
        if (inBounds(idx, idy))
            return opened[idx].get(idy);
        return false;
    }

    @Contract(pure = true)
    public boolean isDead() {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.len; i++)
                if (getState(ship.idy + (ship.rotation == ROTATION_VERTICAL ? i : 0), ship.idx + (ship.rotation == ROTATION_HORIZONTAL ? i : 0)) == STATE_UNDAMAGED)
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
            ships = new ArrayList<>(SHIPS_AVAILABLE.length);
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
                field[i][j] = STATE_EMPTY;
        }
    }

    public boolean placeShip(Ship ship, int idx, int idy, int rotation) {
        if (!inBounds(idx, idy))
            return false;
        else if (rotation == ROTATION_HORIZONTAL && !inBounds(idx + ship.len, idy))
            return false;
        else if (rotation == ROTATION_VERTICAL && !inBounds(idx, idy + ship.len))
            return false;

        for (int i = -1; i <= ship.len; i++) {
            if (rotation == ROTATION_HORIZONTAL) {
                if (cellIsBusy(idx + i, idy) || cellIsBusy(idx + i, idy + 1) || cellIsBusy(idx + i, idy - 1))
                    return false;
            } else {
                if (cellIsBusy(idx, idy + i) || cellIsBusy(idx + 1, idy + i) || cellIsBusy(idx - 1, idy + i))
                    return false;
            }
        }
        ships.add(ship.move(idx, idy, rotation));
        for (int i = 0; i < ship.len; i++) {
            if (rotation == ROTATION_VERTICAL)
                field[idx][idy + i] = (ship.code << SHIP_SHIFT) | STATE_UNDAMAGED;
            else
                field[idx + i][idy] = (ship.code << SHIP_SHIFT) | STATE_UNDAMAGED;
        }
        return true;
    }

    @Contract(pure = true)
    private boolean cellIsBusy(int idx, int idy) {
        return inBounds(idx, idy) && getState(idx, idy) != STATE_EMPTY;
    }

    @Contract(pure = true)
    public boolean inBounds(int idx, int idy) {
        return idx >= 0 && idy >= 0 && idx < width && idy < height;
    }
}
