package dev.ky3he4ik.battleship.logic.inet;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.StaticContent;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.vectors.Vec2dInt;

public class MultiplayerInet extends Thread implements Communication {
    private static String TAG = "MultiplayerInet";

    private StaticContent staticContent = StaticContent.getInstance();

    @Nullable
    private String opponent;
    @NotNull
    private World localField;
    @NotNull
    private World inetField;
    @NotNull
    private GameConfig config;
    @Nullable
    private Field callback = null;
    private long gameId;
    @NotNull
    private String passwd = "";
    private int i = 0;
    private boolean running = true;
    private boolean isSync = false;
    private boolean isHost;
    private boolean shipsSent = false;
    @Nullable
    private Socket client;
    @Nullable
    private Action pending;
    private boolean isReconnect;

    public MultiplayerInet(@NotNull final World localField, @NotNull final World inetField, @NotNull GameConfig config, boolean isHost) {
        this.localField = localField;
        this.inetField = inetField;
        this.config = config;
        this.isHost = isHost;
    }

    @Override
    public void setTurn() {
    }

    @Override
    public void setPlaceShips() {
    }

    @Override
    public void init() {
        start();
        running = true;
    }

    @Override
    public void dispose() {
        running = false;
        if (client != null)
            client.disconnect(staticContent.deviceId);
    }

    @Override
    public void setCallback(@NotNull Field callback) {
        this.callback = callback;
    }

    @Override
    public void restart() {
        running = true;
        if (!isAlive())
            start();
    }

    @Override
    public void run() {
        if (client != null)
            client.disconnect(staticContent.deviceId);
        try {
            client = new Socket(this, staticContent.deviceId);
            if (!isHost) {
                client.send(new Action(Action.ActionType.GET_HOSTS, staticContent.deviceId));
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, e.getMessage(), e);
        }
        running = true;
        Gdx.app.error(TAG, "isHost: " + isHost);
        opponent = null;
        shipsSent = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        running = true;
        Gdx.app.error("MulpiplayerInet", "Start looping: " + running);
        while (running) {
            try {
                if (client == null)
                    client = new Socket(this, staticContent.deviceId);
                if (isReconnect) {
                    client.reconnectBlocking();
                    if (pending != null)
                        client.send(pending);
                    isReconnect = false;
                }
                i++;
                if (i % 10 == 0) {
                    client.send(Action.ping(staticContent.deviceId));
                    Gdx.app.error(TAG, "ping: " + staticContent.deviceId);
                }

                if (i > 100) {
                    i = 0;
                    sync();
                }
                if (client == null)
                    client = new Socket(this, staticContent.deviceId);
                if (i % 50 == 0) {
                    if (opponent == null) {
                        if (isHost)
                            onOpen();
                        else
                            client.send(new Action(Action.ActionType.GET_HOSTS, staticContent.deviceId));
                    }
                    Gdx.app.error(TAG, "opponent: " + opponent + "; isHost: " + isHost);
                }
                Thread.sleep(100);
            } catch (Exception e) {
                Gdx.app.error(TAG, e.getMessage(), e);
            }
        }

    }

    @Override
    public void enemyTurned(int x, int y) {
        if (client != null) {
            ArrayList<World.Ship> ships = localField.getShips();
            int[][] shipsA = new int[ships.size()][];
            for (int i = 0; i < ships.size(); i++) {
                World.Ship ship = ships.get(i);
                shipsA[i] = new int[]{ship.code, ship.idx, ship.idy, ship.rotation};
            }
            Action action = new Action(Action.ActionType.TURN, staticContent.deviceId);
            action.setPos(new Vec2dInt(x, y));
            action.setShips(shipsA);
            action.setGameId(gameId);
            action.setOtherName(opponent);
            client.send(action);
        }
    }

    @Override
    public void enemyShipsPlaced() {
        if (client != null && localField.getShips().size() == config.getShips().size()) {
            ArrayList<World.Ship> ships = localField.getShips();
            int[][] shipsA = new int[ships.size()][];
            for (int i = 0; i < ships.size(); i++) {
                World.Ship ship = ships.get(i);
                shipsA[i] = new int[]{ship.code, ship.idx, ship.idy, ship.rotation};
            }
            Action action = new Action(Action.ActionType.PLACE_SHIPS, staticContent.deviceId);
            action.setShips(shipsA);
            action.setGameId(gameId);
            action.setOtherName(opponent);
            client.send(action);
            Gdx.app.debug("MultyplayerInet", "ships sent");
        }
    }

    @Override
    public void finish() {
        if (client != null)
            client.close();
        client = null;
    }

    void onAction(@NotNull Action action) {
        if (client == null)
            return;
        switch (action.getActionType()) {
            case HOST:
            case PING:
            case NO:
            case OK:
            case CONNECT:
                break;
            case PLACE_SHIPS:
                if (callback != null && action.getShips() != null) {
                    int[][] shipsA = action.getShips();
                    for (int[] ints : shipsA) {
                        String shipName = "";
                        int shipL = 0;
                        for (GameConfig.Ship ship1 : config.getShips())
                            if (ship1.id == ints[0]) {
                                shipName = ints[3] == World.ROTATION_VERTICAL ? ship1.name : ship1.rotatedName();
                                shipL = ship1.length;
                                break;
                            }
                        World.Ship ship = new World.Ship(shipL, ints[0], shipName, ints[1], ints[2], ints[3]);
                        inetField.placeShip(ship, ship.idx, ship.idy, ship.rotation);
                    }
                    if (opponent == null)
                        opponent = action.getOtherName();
                    callback.shipsPlaced();
                    if (!shipsSent) {
                        shipsSent = true;
                        enemyShipsPlaced();
                    }
                }
                break;
            case GET_HOSTS:
                //todo: opponent chooser
                String m = action.getMsg();
                if (m != null && m.length() > 0) {
                    int p = m.indexOf('\n');
                    if (p != -1)
                        m = m.substring(0, p);
                    if (m.equals(staticContent.deviceId))
                        break;
                    Action action1 = new Action(Action.ActionType.JOIN, staticContent.deviceId);
                    action1.setOtherName(m);
                    action1.setMsg(passwd);
                    client.send(action1);
                    opponent = m;
                    Gdx.app.error(TAG, "new opponent: " + opponent);
                }
                break;
            case TURN:
                if (callback != null && action.getShips() != null && action.getPos() != null) {
                    int[][] shipsA = action.getShips();
                    for (int[] ints : shipsA) {
                        String shipName = "";
                        int shipL = 0;
                        for (GameConfig.Ship ship1 : config.getShips())
                            if (ship1.id == ints[0]) {
                                shipName = ints[3] == World.ROTATION_VERTICAL ? ship1.name : ship1.rotatedName();
                                shipL = ship1.length;
                                break;
                            }
                        World.Ship ship = new World.Ship(shipL, ints[0], shipName, ints[1], ints[2], ints[3]);
                        inetField.placeShip(ship, ship.idx, ship.idy, ship.rotation);
                    }
                    if (opponent == null)
                        opponent = action.getOtherName();
                    callback.turnFinished(action.getPos().x, action.getPos().y);
                }
                break;
            case CONNECTED:
                if (staticContent.deviceId.equals(action.getOtherName()))
                    break;
                opponent = action.getOtherName();
                Gdx.app.error(TAG, "new opponent: " + opponent);
                if (localField.getShips().size() == config.getShips().size())
                    enemyShipsPlaced();
                break;
            case JOIN:
                GameConfig config1 = GameConfig.fromJSON(action.getConfig());
                if (config1 != null) {
                    config1.duplicate(config);
                    if (callback != null)
                        callback.gotConfig();
                }
                break;
            case INFO:
                Action action1 = new Action(Action.ActionType.JOIN, staticContent.deviceId);
                action1.setOtherName(action.getOtherName());
                action1.setMsg(passwd);
                client.send(action1);
                break;
            case GAME_END:
            case DISCONNECT:
                client.close();
                client = null;
                if (callback != null)
                    callback.getCallback().setStep(StepsDirector.STEP_AFTERMATH);
                break;
            case START_GAME:

                if (callback != null) {
                    GameConfig config2 = GameConfig.fromJSON(action.getConfig());
                    if (config2 != null)
                        config2.duplicate(config);
                    callback.gotConfig();
                }
                break;
            case SYNC:
                if (!isSync) {
                    i = 0;
                    sync();
                }
                isSync = false;
//                World e = World.fromJSON(action.getMsg());
//                e.duplicate(inetField);
                break;
        }
    }

    private void sync() {
        if (client != null) {
            if (opponent != null) {
                isSync = true;
                Action action = new Action(Action.ActionType.SYNC, staticContent.deviceId);
                action.setMsg(localField.toJson());
                client.send(action);
            }
        }
    }

    void onOpen() {
        if (isHost && client != null) {
            Action action = new Action(Action.ActionType.HOST, staticContent.deviceId);
            action.setMsg(passwd);
            action.setConfig(config.toJSON());
            client.send(action);
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && !client.isClosed();
    }

    void reconnect(@Nullable Action action) {
        isReconnect = true;
        pending = action;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }
}
