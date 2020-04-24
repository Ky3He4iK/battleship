package dev.ky3he4ik.battleship;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.BitSet;

public class World {
    public static class Ship {
        public int len;
        public String name;
        public int code;

        public Ship(int len, int code, String name) {
            this.len = len;
            this.code = code;
            this.name = name;
        }

        public Ship copy() {
            return new Ship(len, code, name);
        }
    }

    public static final int ROTATION_HORIZONTAL = 0;
    public static final int ROTATION_VERTICAL = 1;

    public static Color COLOR_UNKNOWN = new Color(.3f, .3f, .3f, 0);
    public static Color COLOR_EMPTY = new Color(0, 0, .5f, 0);
    public static Color COLOR_UNDAMAGED = new Color(0, 1, 0, 0);
    public static Color COLOR_DAMAGED = new Color(.5f, 0, 0, 0);
    public static Color COLOR_SUNK = new Color(1, 0, 0, 0);

    // cell states:
    public static final int STATE_MASK = 0xf; // reserved for more states
    public static final int STATE_EMPTY = 0x0; // no ship
    public static final int STATE_UNDAMAGED = 0x1; // ship
    public static final int STATE_DAMAGED = 0x2; // damaged ship
    public static final int STATE_SUNK = 0x3; // sunk ship

    // ship codes
    public static final int SHIP_MASK = 0xf00; // reserved for more ships
    public static final int SHIP_NOSHIP = 0x000; // literally no ship;
    public static final int SHIP_CARRIER = 0x100;
    public static final int SHIP_BATTLESHIP = 0x200;
    public static final int SHIP_DESTROYER = 0x300;
    public static final int SHIP_SUBMARINE = 0x400;
    public static final int SHIP_PATROL_BOAT = 0x500;

    public static final Ship[] SHIPS_AVAILABLE = {new Ship(5, SHIP_CARRIER, "Carrier"),
            new Ship(4, SHIP_BATTLESHIP, "Battleship"),
            new Ship(3, SHIP_DESTROYER, "Destroyer"),
            new Ship(3, SHIP_SUBMARINE, "Submarine"),
            new Ship(2, SHIP_PATROL_BOAT, "Patrol Boat"),
    };

    private BitSet[] opened;
    private int[][] field;
    private ArrayList<Ship> ships;
    private ArrayList<Integer[]> shipsPos;
    private int width;
    private int height;

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public World(int width, int height) {
        reset(width, height);
    }

    public World copy() {
        World child = new World(width, height);
        child.setOpened(opened);
        child.setField(field);
        return child;
    }

    public void setState(int i, int j, int state) {
        field[i][j] = field[i][j] & (~STATE_MASK) | state;
    }

    public int getState(int i, int j) {
        return field[i][j] & STATE_MASK;
    }

    public void open(int i, int j) {
        opened[i].set(j);
        if (getState(i, j) == STATE_UNDAMAGED) {
            setState(i, j, STATE_DAMAGED);
            // check ship
            int mii = i, mai = i, mij = j, maj = j;
            while (mii >= 0 && getState(mii, j) == STATE_DAMAGED)
                --mii;
            while (mai < height && getState(mai, j) == STATE_DAMAGED)
                ++mai;
            while (mij >= 0 && getState(i, mij) == STATE_DAMAGED)
                --mij;
            while (maj < width && getState(i, maj) == STATE_DAMAGED)
                ++maj;
            if (mii < 0)
                ++mii;
            if (mai >= height)
                --mai;
            if (mij < 0)
                ++mij;
            if (maj >= width)
                --maj;
            if (!((mii >= 0 && getState(mii, j) == STATE_UNDAMAGED)
                    || (mai < height && getState(mai, j) == STATE_UNDAMAGED)
                    || (mij >= 0 && getState(i, mij) == STATE_UNDAMAGED)
                    || (maj < width && getState(i, mij) == STATE_UNDAMAGED))) { // If has not any float fragment
                for (int ii = mii; ii <= maj; ii++)
                    setState(ii, j, STATE_SUNK);
                for (int jj = mij; jj <= maj; jj++)
                    setState(i, jj, STATE_SUNK);
            }
        }
    }

    public boolean isOpened(int idx, int idy) {
        return opened[idx].get(idy);
    }

    public boolean isAlive() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (getState(i, j) == STATE_UNDAMAGED)
                    return true;
        return false;
    }

    public void reset(int width, int height) {
        this.width = width;
        this.height = height;
        if (field == null || field.length != height)
            field = new int[height][];
        if (opened == null || opened.length != height)
            opened = new BitSet[height];
        if (ships == null)
            ships = new ArrayList<>(SHIPS_AVAILABLE.length);
        else
            ships.clear();
        if (shipsPos == null)
            shipsPos = new ArrayList<>(SHIPS_AVAILABLE.length);
        else
            shipsPos.clear();
        System.gc();

        for (int i = 0; i < height; i++) {
            if (field[i] == null || field.length != width)
                field[i] = new int[width];
            if (opened[i] == null || opened.length != width)
                opened[i] = new BitSet(width);
            else
                opened[i].clear();
            for (int j = 0; j < width; j++)
                field[i][j] = STATE_EMPTY;
        }
    }

    public boolean placeShip(Ship ship, int idx, int idy, int rotation) {
        for (int i = 0; i < ship.len; i++) {
            if (rotation == ROTATION_HORIZONTAL) {
                // todo: check for correct placement
            } else {

            }
        }
        ships.add(ship);
        shipsPos.add(new Integer[]{idx, idy, rotation});
        for (int i = 0; i < ship.len; i++) {
            if (rotation == ROTATION_HORIZONTAL)
                field[idy][idx + i] = ship.code | STATE_UNDAMAGED;
            else
                field[idy + i][idx] = ship.code | STATE_UNDAMAGED;
        }
        return true;
    }
}
