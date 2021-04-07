package dev.ky3he4ik.battleship.logic.inet;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/*ships: id, idx, idy, rotation */
public class Action {
    @NotNull
    private ActionType actionType;
    @Nullable
    private String config;
    private int playerId;
    @NotNull
    private String name;
    @Nullable
    private int[] pos;
    @Nullable
    private int[][] ships;
    @Nullable
    private String otherName;
    @Nullable
    private String msg;
    private long gameId;
    private int code;
    private long uuid;

    public Action(@NotNull ActionType actionType, @Nullable String config, int playerId, @NotNull String name, @Nullable int[] pos, @Nullable int[][] ships, @Nullable String otherName, @Nullable String msg, long gameId, int code, long uuid) {
        this.actionType = actionType;
        this.config = config;
        this.playerId = playerId;
        this.name = name;
        this.pos = pos;
        this.ships = ships;
        this.otherName = otherName;
        this.msg = msg;
        this.gameId = gameId;
        this.code = code;
        this.uuid = uuid;
    }

    public Action(@NotNull Action action) {
        this.actionType = action.actionType;
        this.config = action.config;
        this.playerId = action.playerId;
        this.name = action.name;
        this.pos = action.pos;
        this.ships = action.ships;
        this.otherName = action.otherName;
        this.msg = action.msg;
        this.gameId = action.gameId;
        this.code = action.code;
        this.uuid = action.uuid;
    }

    Action(@NotNull ActionType actionType, @NotNull String name, long uuid) {
        this.actionType = actionType;
        this.config = null;
        this.playerId = 0;
        this.name = name;
        this.pos = null;
        this.ships = null;
        this.otherName = null;
        this.msg = null;
        this.gameId = 0;
        this.code = 0;
        this.uuid = uuid;
    }

    static @Nullable
    Action fromJson(@NotNull String json) {
        return new Gson().fromJson(json, Action.class);
    }

    public static @NotNull
    Action ok() {
        return new Action(ActionType.OK, "", 0);
    }

    public static @NotNull
    Action no() {
        return new Action(ActionType.NO, "", 0);
    }

    static @NotNull
    Action ping(@NotNull String name, long uuid) {
        return new Action(ActionType.PING, name, uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;

        Action action = (Action) o;

        if (playerId != action.playerId) return false;
        if (gameId != action.gameId) return false;
        if (code != action.code) return false;
        if (uuid != action.uuid) return false;
        if (actionType != action.actionType) return false;
        if (config != null ? !config.equals(action.config) : action.config != null) return false;
        if (!name.equals(action.name)) return false;
        if (!Arrays.equals(pos, action.pos)) return false;
        if (!Arrays.deepEquals(ships, action.ships)) return false;
        if (otherName != null ? !otherName.equals(action.otherName) : action.otherName != null)
            return false;
        return msg != null ? msg.equals(action.msg) : action.msg == null;
    }

    @Override
    public int hashCode() {
        int result = actionType.hashCode();
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + playerId;
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(pos);
        result = 31 * result + Arrays.deepHashCode(ships);
        result = 31 * result + (otherName != null ? otherName.hashCode() : 0);
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        result = 31 * result + (int) (gameId ^ (gameId >>> 32));
        result = 31 * result + code;
        result = 31 * result + (int) (uuid ^ (uuid >>> 32));
        return result;
    }

    @NotNull
    ActionType getActionType() {
        return actionType;
    }

    public void setActionType(@NotNull ActionType actionType) {
        this.actionType = actionType;
    }

    @Nullable
    public String getConfig() {
        return config;
    }

    public void setConfig(@Nullable String config) {
        this.config = config;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    int[] getPos() {
        return pos;
    }

    void setPos(@Nullable int[] pos) {
        this.pos = pos;
    }

    @Nullable
    public int[][] getShips() {
        return ships;
    }

    void setShips(@Nullable int[][] ships) {
        this.ships = ships;
    }

    @Nullable
    String getOtherName() {
        return otherName;
    }

    void setOtherName(@Nullable String otherName) {
        this.otherName = otherName;
    }

    @Nullable
    String getMsg() {
        return msg;
    }

    void setMsg(@Nullable String msg) {
        this.msg = msg;
    }

    public long getGameId() {
        return gameId;
    }

    void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    @NotNull
    String toJson() {
        return new Gson().toJson(this);
    }


    enum ActionType { // name, uuid for all
        CONNECT, // < -
        // > if success

        GET_HOSTS, // < name, otherName
        // > msg: `\n` separated list of connected names

        HOST, // < name, config, msg: password
        // > code

        CONNECTED, // > otherName

        INFO, // < otherName
        // > config, code: password length

        JOIN, // < name, otherName, msg: password?
        // > code: zero if fail, config

        START_GAME, // > playerId


        PLACE_SHIPS,    // < code, playerId, ships
        // > -//-


        TURN,   // < code, playerId, ships
        // > -//-


        GAME_END, // > -

        DISCONNECT, // < -
        // > -

        PING,   // < -
        // > -

        OK,     // < -
        // > -

        // Just... no
        NO,     // < -
        // > -

        SYNC,   // msg: World in JSON
    }


}
