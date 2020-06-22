package dev.ky3he4ik.battleship.logic;

import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class World {
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

        int getNthX(int i) {
            return H.I(rotation == ROTATION_HORIZONTAL) * i + idx;
        }

        int getNthY(int i) {
            return H.I(rotation == ROTATION_VERTICAL) * i + idy;
        }
    }

    public static final int ROTATION_HORIZONTAL = 0;
    public static final int ROTATION_VERTICAL = 1;
    private static final int EMPTY_CELL = 0x0; // no ship
    private BitSet[] opened;
    private int[][] field;
    private ArrayList<Ship> ships;
    private int width;
    private int height;

    public World(int width, int height) {
        reset(width, height);
    }

    @Contract(pure = true)
    public ArrayList<Ship> getShips() {
        return ships;
    }

    public int getWidth() {
        return width;
    }

    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    public boolean isEmptyCell(int i, int j) {
        if (inBounds(i, j))
            return field[i][j] == EMPTY_CELL;
        return true;
    }

    /**
     * Shoot to `idx`x`idy` cell
     *
     * @return array of opened cells
     */
    @NotNull
    public ArrayList<int[]> open(int idx, int idy) {
        ArrayList<int[]> openedCells = new ArrayList<>();
        if (!inBounds(idx, idy) || opened[idx].get(idy))
            return openedCells;
        opened[idx].set(idy);
        openedCells.add(new int[]{idx, idy});
        Ship ship = findShip(field[idx][idy]);
        if (ship == null)
            return openedCells;

        boolean isDead = true;
        for (int i = 0; i < ship.length; i++) {
            if (!isCellOpened(ship.getNthX(i), ship.getNthY(i)))
                isDead = false;
        }
        if (isDead) {
            for (int it = -1; it <= ship.length; it++) {
                if (ship.rotation == ROTATION_VERTICAL) {
                    openedCells.addAll(open(ship.idx + 1, ship.idy + it));
                    openedCells.addAll(open(ship.idx - 1, ship.idy + it));
                    openedCells.addAll(open(ship.idx, ship.idy + it));
                } else {
                    openedCells.addAll(open(ship.idx + it, ship.idy + 1));
                    openedCells.addAll(open(ship.idx + it, ship.idy - 1));
                    openedCells.addAll(open(ship.idx + it, ship.idy));
                }
            }
        }
        return openedCells;
    }

    public boolean isCellOpened(int idx, int idy) {
        if (inBounds(idx, idy))
            return opened[idx].get(idy);
        return false;
    }

    @Contract(pure = true)
    public boolean isDead() {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.length; i++)
                if (!isCellOpened(ship.getNthX(i), ship.getNthY(i)))
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
                if (cellIsBusy(idx + i, idy) || cellIsBusy(idx + i, idy + 1) || cellIsBusy(idx + i, idy - 1) || (i >= 0 && i < ship.length && isCellOpened(idx + i, idy)))
                    return false;
            } else {
                if (cellIsBusy(idx, idy + i) || cellIsBusy(idx + 1, idy + i) || cellIsBusy(idx - 1, idy + i) || (i >= 0 && i < ship.length && isCellOpened(idx, idy + i)))
                    return false;
            }
        }
        ships.add(ship.move(idx, idy, rotation));
        for (int i = 0; i < ship.length; i++) {
            if (rotation == ROTATION_VERTICAL)
                field[idx][idy + i] = ship.code;
            else
                field[idx + i][idy] = ship.code;
        }
        return true;
    }

    @Contract(pure = true)
    private boolean cellIsBusy(int idx, int idy) {
        return inBounds(idx, idy) && !isEmptyCell(idx, idy);
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
                    field[ship.getNthX(j)][ship.getNthY(j)] = EMPTY_CELL;
                ships.remove(i);
                return;
            }
    }

    public boolean rotate(int shipId) {
        for (int i = 0; i < ships.size(); i++)
            if (ships.get(i).code == shipId) {
                Ship ship = ships.get(i);
                for (int j = 0; j < ship.length; j++)
                    field[ship.getNthX(j)]
                            [ship.getNthY(j)] = EMPTY_CELL;
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
            if (isCellOpened(ship.getNthX(i), ship.getNthY(i)))
                return true;
        return false;
    }

    public boolean shipDead(int shipId) {
        Ship ship = findShip(shipId);
        if (ship == null)
            return false;
        for (int i = 0; i < ship.length; i++)
            if (!isCellOpened(ship.getNthX(i), ship.getNthY(i)))
                return false;
        return true;
    }

    public boolean isPlaced(int shipId) {
        for (Ship ship : ships)
            if (ship.code == shipId)
                return true;
        return false;
    }

    @NotNull
    public String toJson() {
        return new Gson().toJson(this);
    }
}
