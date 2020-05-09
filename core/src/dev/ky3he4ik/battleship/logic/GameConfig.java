package dev.ky3he4ik.battleship.logic;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import dev.ky3he4ik.battleship.ai.AILevel;
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
     * @param multipleShots   - if true each player will shoot several times each turn
     * @param additionalShots - if true player will get one more shot after enemy's ship hit
     * @param decreasingField - if true field will eventually shrink
     * @param movingPerTurn   - how many ships an be moved each turn
     * @param shotsPerTurn    - how many shoots each player gets (ignored if `multipleShots` s false)
     * @param aiLevel         - difficulty level (only GameType.AI)
     * @param gameType        - type of game
     * @param ships           - list of available ships
     */
    public GameConfig(int width, int height, boolean movingEnabled, boolean multipleShots, boolean additionalShots, boolean decreasingField, int movingPerTurn, int shotsPerTurn, int aiLevel, int aiLevel2, @NotNull GameType gameType, @NotNull ArrayList<Ship> ships) {
        this.width = width;
        this.height = height;
        this.movingEnabled = movingEnabled;
        this.multipleShots = multipleShots;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameConfig)) return false;

        GameConfig config = (GameConfig) o;

        if (width != config.width) return false;
        if (height != config.height) return false;
        if (movingEnabled != config.movingEnabled) return false;
        if (multipleShots != config.multipleShots) return false;
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
        result = 31 * result + (multipleShots ? 1 : 0);
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

    public boolean isMultipleShots() {
        return multipleShots;
    }

    public void setMultipleShots(boolean multipleShots) {
        this.multipleShots = multipleShots;
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
    public static GameConfig getSampleConfigWest() {
        return new GameConfig(10, 10, false, false,
                true, false, 0, 1, AILevel.EASY.id, AILevel.NOVICE.id, GameType.AI, Ship.getSampleShipsWest());
    }

    @NotNull
    public static GameConfig getSampleConfigEast() {
        return new GameConfig(10, 10, false, false,
                true, false, 0, 1, AILevel.EASY.id, AILevel.NOVICE.id, GameType.AI, Ship.getSampleShipsEast());
    }

    @NotNull
    public static GameConfig getSampleMoving() {
        return new GameConfig(10, 10, true, false,
                true, false, -1, 1, AILevel.EASY.id, AILevel.NOVICE.id, GameType.AI, Ship.getSampleShipsEast());
    }

    @NotNull
    public static GameConfig fromJSON(@NotNull String json) {
        return new Gson().fromJson(json, GameConfig.class);
    }

    @NotNull
    public String toJSON() {
        return new Gson().toJson(this);
    }

    public void duplicate(@NotNull GameConfig other) {
        other.width = width;
        other.height = height;
        other.movingEnabled = movingEnabled;
        other.multipleShots = multipleShots;
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
}
