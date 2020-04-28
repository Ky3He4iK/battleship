package dev.ky3he4ik.battleship.logic;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class GameConfig {
    static class Ship {
        public final int length;

        @NotNull
        public final String name;

        public Ship(int length, @NotNull String name) {
            this.length = length;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Ship)) return false;

            Ship ship = (Ship) o;

            if (length != ship.length) return false;
            return name.equals(ship.name);
        }

        @Override
        public int hashCode() {
            int result = length;
            result = 31 * result + name.hashCode();
            return result;
        }

        @NotNull
        public static ArrayList<Ship> getSampleShipsWest() {
            return (ArrayList<Ship>) Arrays.asList(new Ship[]{
                    new Ship(5, "Carrier"),
                    new Ship(4, "Battleship"),
                    new Ship(3, "Destroyer"),
                    new Ship(3, "Submarine"),
                    new Ship(2, "Patrol_boat")
            });
        }

        @NotNull
        public static ArrayList<Ship> getSampleShipsEast() {
            return (ArrayList<Ship>) Arrays.asList(new Ship[]{
                    new Ship(4, "Battleship"),
                    new Ship(3, "Submarine"),
                    new Ship(3, "Submarine"),
                    new Ship(2, "Patrol_boat"),
                    new Ship(2, "Patrol_boat"),
                    new Ship(2, "Patrol_boat"),
                    new Ship(1, "Rubber_boat"),
                    new Ship(1, "Rubber_boat"),
                    new Ship(1, "Rubber_boat"),
                    new Ship(1, "Rubber_boat")
            });
        }
    }

    enum GameType {
        LOCAL_2P,
        AI,
        LOCAL_INET,
        BLUETOOTH,
        GLOBAL_INET
    }

    private int width, height;
    private boolean movingEnabled;
    private boolean multipleShots;
    private boolean additionalShots;
    private int movingPerTurn;
    private int shotsPerTurn;
    private int aiLevel;

    @NotNull
    private GameType gameType;

    @NotNull
    private ArrayList<Ship> ships;

    public GameConfig(int width, int height, boolean movingEnabled, boolean multipleShots, boolean additionalShots, int movingPerTurn, int shotsPerTurn, int aiLevel, @NotNull GameType gameType, @NotNull ArrayList<Ship> ships) {
        this.width = width;
        this.height = height;
        this.movingEnabled = movingEnabled;
        this.multipleShots = multipleShots;
        this.additionalShots = additionalShots;
        this.movingPerTurn = movingPerTurn;
        this.shotsPerTurn = shotsPerTurn;
        this.aiLevel = aiLevel;
        this.gameType = gameType;
        this.ships = ships;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMovingEnabled() {
        return movingEnabled;
    }

    public boolean isMultipleShots() {
        return multipleShots;
    }

    public boolean isAdditionalShots() {
        return additionalShots;
    }

    public int getMovingPerTurn() {
        return movingPerTurn;
    }

    public int getShotsPerTurn() {
        return shotsPerTurn;
    }

    public int getAiLevel() {
        return aiLevel;
    }

    public GameType getGameType() {
        return gameType;
    }

    @NotNull
    public ArrayList<Ship> getShips() {
        return ships;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameConfig)) return false;

        GameConfig that = (GameConfig) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (movingEnabled != that.movingEnabled) return false;
        if (multipleShots != that.multipleShots) return false;
        if (additionalShots != that.additionalShots) return false;
        if (movingPerTurn != that.movingPerTurn) return false;
        if (shotsPerTurn != that.shotsPerTurn) return false;
        if (aiLevel != that.aiLevel) return false;
        if (gameType != that.gameType) return false;
        return ships.equals(that.ships);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + (movingEnabled ? 1 : 0);
        result = 31 * result + (multipleShots ? 1 : 0);
        result = 31 * result + (additionalShots ? 1 : 0);
        result = 31 * result + movingPerTurn;
        result = 31 * result + shotsPerTurn;
        result = 31 * result + aiLevel;
        result = 31 * result + gameType.hashCode();
        result = 31 * result + ships.hashCode();
        return result;
    }

    @NotNull
    public static GameConfig getSampleConfigWest() {
        return new GameConfig(10, 10, false, false, false, 0, 0, 1, GameType.AI, Ship.getSampleShipsWest());
    }

    @NotNull
    public static GameConfig getSampleConfigEast() {
        return new GameConfig(10, 10, false, false, false, 0, 0, 1, GameType.AI, Ship.getSampleShipsEast());
    }

    @NotNull
    public static GameConfig fromJSON(String json) {
        return new Gson().fromJson(json, GameConfig.class);
    }

    @NotNull
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
