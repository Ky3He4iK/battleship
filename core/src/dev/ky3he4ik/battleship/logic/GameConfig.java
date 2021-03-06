package dev.ky3he4ik.battleship.logic;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import dev.ky3he4ik.battleship.ai.AILevel;
import dev.ky3he4ik.battleship.utils.Constants;

public class GameConfig {
    private int width, height;
    private boolean movingEnabled;
    private boolean additionalShots;
    private boolean decreasingField;
    private int movingPerTurn;
    private int shotsPerTurn;
    private int aiLevel;
    private int aiLevel2;
    private int version;
    @NotNull
    private GameType gameType;
    @NotNull
    private ArrayList<Ship> ships;

    /**
     * @param width           - width of field (in cells). Default 10
     * @param height          - height of field (in cells). Default 10
     * @param movingEnabled   - is ships can be moved after each turn?
     * @param additionalShots - if true player will get one more shot after enemy's ship hit
     * @param decreasingField - if true field will eventually shrink
     * @param movingPerTurn   - how many ships an be moved each turn
     * @param shotsPerTurn    - how many shoots each player gets
     * @param aiLevel         - difficulty level (only GameType.AI)
     * @param gameType        - type of game
     * @param ships           - list of available ships
     */
    public GameConfig(int width, int height, boolean movingEnabled, boolean additionalShots, boolean decreasingField, int movingPerTurn, int shotsPerTurn, int aiLevel, int aiLevel2, @NotNull GameType gameType, @NotNull ArrayList<Ship> ships) {
        this.width = width;
        this.height = height;
        this.movingEnabled = movingEnabled;
        this.additionalShots = additionalShots;
        this.decreasingField = decreasingField;
        this.movingPerTurn = movingPerTurn;
        this.shotsPerTurn = shotsPerTurn;
        this.aiLevel = aiLevel;
        this.aiLevel2 = aiLevel2;
        this.version = 1;
        this.gameType = gameType;
        this.ships = ships;
    }

    @NotNull
    public static GameConfig getSampleConfigWest() {
        return new GameConfig(10, 10, false,
                true, false, 0, 1, AILevel.EASY.id, AILevel.NOVICE.id, GameType.AI, Ship.getSampleShipsWest());
    }

    @NotNull
    public static GameConfig getSampleConfigEast() {
        return new GameConfig(10, 10, false,
                true, false, 0, 1, AILevel.EASY.id, AILevel.NOVICE.id, GameType.AI, Ship.getSampleShipsEast());
    }

    @NotNull
    public static GameConfig getSampleMoving() {
        return new GameConfig(10, 10, true,
                true, false, -1, 1, AILevel.EASY.id, AILevel.EASY.id, GameType.AI, Ship.getSampleShipsEast());
    }

    @Nullable
    public static GameConfig fromJSON(@Nullable String json) {
        return new Gson().fromJson(json, GameConfig.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameConfig)) return false;

        GameConfig config = (GameConfig) o;

        if (width != config.width) return false;
        if (height != config.height) return false;
        if (movingEnabled != config.movingEnabled) return false;
        if (additionalShots != config.additionalShots) return false;
        if (decreasingField != config.decreasingField) return false;
        if (movingPerTurn != config.movingPerTurn) return false;
        if (shotsPerTurn != config.shotsPerTurn) return false;
        if (aiLevel != config.aiLevel) return false;
        if (aiLevel2 != config.aiLevel2) return false;
        if (version != config.version) return false;
        if (gameType != config.gameType) return false;
        return ships.equals(config.ships);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + (movingEnabled ? 1 : 0);
        result = 31 * result + (additionalShots ? 1 : 0);
        result = 31 * result + (decreasingField ? 1 : 0);
        result = 31 * result + movingPerTurn;
        result = 31 * result + shotsPerTurn;
        result = 31 * result + aiLevel;
        result = 31 * result + aiLevel2;
        result = 31 * result + version;
        result = 31 * result + gameType.hashCode();
        result = 31 * result + ships.hashCode();
        return result;
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

    public boolean isMovingEnabled() {
        return movingEnabled;
    }

    public void setMovingEnabled(boolean movingEnabled) {
        this.movingEnabled = movingEnabled;
    }

    public boolean isAdditionalShots() {
        return additionalShots;
    }

    public void setAdditionalShots(boolean additionalShots) {
        this.additionalShots = additionalShots;
    }

    public boolean isDecreasingField() {
        return decreasingField;
    }

    public void setDecreasingField(boolean decreasingField) {
        this.decreasingField = decreasingField;
    }

    public int getMovingPerTurn() {
        return movingPerTurn;
    }

    public void setMovingPerTurn(int movingPerTurn) {
        this.movingPerTurn = movingPerTurn;
    }

    public int getShotsPerTurn() {
        return shotsPerTurn;
    }

    public void setShotsPerTurn(int shotsPerTurn) {
        this.shotsPerTurn = shotsPerTurn;
    }

    public int getAiLevel() {
        return aiLevel;
    }

    public void setAiLevel(int aiLevel) {
        this.aiLevel = aiLevel;
    }

    public int getAiLevel2() {
        return aiLevel2;
    }

    public void setAiLevel2(int aiLevel2) {
        this.aiLevel2 = aiLevel2;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @NotNull
    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(@NotNull GameType gameType) {
        this.gameType = gameType;
    }

    @NotNull
    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(@NotNull ArrayList<Ship> ships) {
        this.ships = ships;
    }

    @NotNull
    public String toJSON() {
        return new Gson().toJson(this);
    }

    @NotNull
    public GameConfig duplicate(@Nullable GameConfig other) {
        if (other == null) {
            other = new GameConfig(width, height, movingEnabled, additionalShots, decreasingField, movingPerTurn, shotsPerTurn, aiLevel, aiLevel2, gameType, ships);
            other.version = version;
        } else {
            other.width = width;
            other.height = height;
            other.movingEnabled = movingEnabled;
            other.additionalShots = additionalShots;
            other.decreasingField = decreasingField;
            other.movingPerTurn = movingPerTurn;
            other.shotsPerTurn = shotsPerTurn;
            other.aiLevel = aiLevel;
            other.aiLevel2 = aiLevel2;
            other.version = version;
            other.gameType = gameType;
            other.ships = ships;
        }
        return other;
    }

    public enum GameType {
        LOCAL_2P,
        AI,
        //        LOCAL_INET,
//        BLUETOOTH,
        GLOBAL_INET,
        AI_VS_AI
    }

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

        @NotNull
        static ArrayList<Ship> getSampleShipsWest() {
            return new ArrayList<>(Arrays.asList(new Ship(5, 1, Constants.SHIP_CARRIER_IMG),
                    new Ship(4, 2, Constants.SHIP_BATTLESHIP_IMG),
                    new Ship(3, 3, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(3, 4, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(2, 5, Constants.SHIP_PATROL_BOAT_IMG)));
        }

        @NotNull
        static ArrayList<Ship> getSampleShipsEast() {
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

        @NotNull
        public static Ship[] getAllShipsSamples() {
            return new Ship[]{
                    new Ship(5, 1, Constants.SHIP_CARRIER_IMG),
                    new Ship(4, 2, Constants.SHIP_BATTLESHIP_IMG),
                    new Ship(3, 3, Constants.SHIP_SUBMARINE_IMG),
                    new Ship(2, 4, Constants.SHIP_PATROL_BOAT_IMG),
                    new Ship(1, 5, Constants.SHIP_RUBBER_BOAT_IMG)
            };
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
        public World.Ship convert() {
            return new World.Ship(length, id, name, 0, 0, 0);
        }

        @NotNull
        public Ship clone(int newId) {
            return new Ship(length, newId, name);
        }

        @NotNull
        public String rotatedName() {
            if (name.endsWith(Constants.ROTATED_SUFFIX))
                return name;
            return name + Constants.ROTATED_SUFFIX;
        }
    }
}
