package dev.ky3he4ik.battleship.logic;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import dev.ky3he4ik.battleship.utils.Constants;

public class GameConfig {
    public static class Ship {
        public final int length;
        public final int id;

        @NotNull
        public final String name;


        public Ship(int length, int id, @NotNull String name) {
            this.length = length;
            this.id = id;
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
            result = 31 * result + id;
            result = 31 * result + name.hashCode();
            return result;
        }

        @NotNull
        public static ArrayList<Ship> getSampleShipsWest() {
            return new ArrayList<>(Arrays.asList(new Ship(5, 1, Constants.SHIP_CARRIER_IMG),
                    new Ship(4, 2, Constants.SHIP_BATTLESHIP_IMG),
                    new Ship(3, 3, Constants.SHIP_DESTROYER_IMG),
                    new Ship(3, 4, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(2, 5, Constants.SHIP_PATROL_BOAT_IMG)));
        }

        @NotNull
        public static ArrayList<Ship> getSampleShipsEast() {
            return new ArrayList<>(Arrays.asList(new Ship(4, 1, Constants.SHIP_BATTLESHIP_IMG),
                    new Ship(3, 2, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(3, 3, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(2, 4, Constants.SHIP_PATROL_BOAT_IMG),
                    new Ship(2, 5, Constants.SHIP_PATROL_BOAT_IMG),
                    new Ship(2, 6, Constants.SHIP_PATROL_BOAT_IMG),
                    new Ship(1, 7, Constants.SHIP_RUBBER_BOAT_IMG),
                    new Ship(1, 8, Constants.SHIP_RUBBER_BOAT_IMG),
                    new Ship(1, 9, Constants.SHIP_RUBBER_BOAT_IMG),
                    new Ship(1, 10, Constants.SHIP_RUBBER_BOAT_IMG)));
        }

        public World.Ship convert() {
            return new World.Ship(length, id, name, 0, 0, 0);
        }
    }

    public enum GameType {
        LOCAL_2P,
        AI,
        LOCAL_INET,
        BLUETOOTH,
        GLOBAL_INET,
        AI_VS_AI
    }

    private int width, height;
    private boolean movingEnabled;
    private boolean multipleShots;
    private boolean additionalShots;
    private int movingPerTurn;
    private int shotsPerTurn;
    private int aiLevel;
    private int version = 1;

    @NotNull
    private GameType gameType;

    @NotNull
    private ArrayList<Ship> ships;

    /**
     * @param width           - width of field (in cells). Default 10
     * @param height          - height of field (in cells). Default 10
     * @param movingEnabled   - is ships can be moved after each turn?
     * @param multipleShots   - if true each player will shoot several times each turn
     * @param additionalShots - if true player will get one more shot after enemy's ship hit
     * @param movingPerTurn   - how many ships an be moved each turn
     * @param shotsPerTurn    - how many shoots each player gets (ignored if `multipleShots` s false)
     * @param aiLevel         - difficulty level (only GameType.AI)
     * @param gameType        - type of game
     * @param ships           - list of available ships
     */
    public GameConfig(int width, int height, boolean movingEnabled, boolean multipleShots,
                      boolean additionalShots, int movingPerTurn, int shotsPerTurn, int aiLevel,
                      @NotNull GameType gameType, @NotNull ArrayList<Ship> ships) {
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

    @NotNull
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
        return new GameConfig(10, 10, false, false,
                true, 0, 1, 1, GameType.AI, Ship.getSampleShipsWest());
    }

    @NotNull
    public static GameConfig getSampleConfigEast() {
        return new GameConfig(10, 10, false, false,
                true, 0, 1, 1, GameType.AI, Ship.getSampleShipsEast());
    }

    @NotNull
    public static GameConfig fromJSON(@NotNull String json) {
        return new Gson().fromJson(json, GameConfig.class);
    }

    @NotNull
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
