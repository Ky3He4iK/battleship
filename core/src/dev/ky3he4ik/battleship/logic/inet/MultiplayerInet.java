package dev.ky3he4ik.battleship.logic.inet;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;

public class MultiplayerInet extends Thread implements Communication {
    @NotNull
    private World enemy;

    @NotNull
    private World my;

    @NotNull
    private GameConfig config;
    @Nullable
    private Field callback = null;

    @NotNull
    private String name;

    private long uuid;

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
    public String opponent;

    @Nullable
    private Action pending;
    private boolean isReconnect;

    public MultiplayerInet(@NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config, @NotNull String name, long uuid, boolean isHost) {
        this.name = name;
        this.uuid = uuid;
        this.enemy = enemy;
        this.my = my;
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
            client.disconnect(name, uuid);
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
            client.disconnect(name, uuid);
        try {
            client = new Socket(this, name, uuid);
            if (!isHost) {
                client.send(new Action(Action.ActionType.GET_HOSTS, name, uuid));
            }
        } catch (Exception e) {
            Gdx.app.error("MultiplayerInet", e.getMessage(), e);
        }
        shipsSent = false;
        while (running) {
            try {
                if (isReconnect && client != null) {
                    client.reconnect();
                    if (pending != null)
                        client.send(pending);
                    isReconnect = false;
                }
                i++;
                if (i % 10 == 0 && client != null)
                    client.send(Action.ping(name, uuid));

                if (i > 100) {
                    i = 0;
                    sync();
                }
                if (i % 50 == 0) {
                    if (!isHost && opponent == null && client != null)
                        client.send(new Action(Action.ActionType.GET_HOSTS, name, uuid));
                }
                Thread.sleep(100);
            } catch (Exception e) {
                Gdx.app.error("MultiplayerInet", e.getMessage(), e);
            }
        }

    }

    @Override
    public void enemyTurned(int x, int y) {
        if (client != null) {
            ArrayList<World.Ship> ships = enemy.getShips();
            int[][] shipsA = new int[ships.size()][];
            for (int i = 0; i < ships.size(); i++) {
                World.Ship ship = ships.get(i);
                shipsA[i] = new int[]{ship.code, ship.idx, ship.idy, ship.rotation};
            }
            Action action = new Action(Action.ActionType.TURN, name, uuid);
            action.setPos(new int[]{x, y});
            action.setShips(shipsA);
            action.setGameId(gameId);
            action.setOtherName(opponent);
            client.send(action);
        }
    }

    @Override
    public void enemyShipsPlaced() {
        if (client != null) {
            ArrayList<World.Ship> ships = enemy.getShips();
            int[][] shipsA = new int[ships.size()][];
            for (int i = 0; i < ships.size(); i++) {
                World.Ship ship = ships.get(i);
                shipsA[i] = new int[]{ship.code, ship.idx, ship.idy, ship.rotation};
            }
            Action action = new Action(Action.ActionType.PLACE_SHIPS, name, uuid);
            action.setShips(shipsA);
            action.setGameId(gameId);
            action.setOtherName(opponent);
            client.send(action);
        }
        Gdx.app.debug("MultyplayerInet", "ships sent");
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
                        my.placeShip(ship, ship.idx, ship.idy, ship.rotation);
                    }
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
                    Action action1 = new Action(Action.ActionType.JOIN, name, uuid);
                    action1.setOtherName(m);
                    action1.setMsg(passwd);
                    client.send(action1);
                    opponent = m;
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
                        my.placeShip(ship, ship.idx, ship.idy, ship.rotation);
                    }
                    callback.turnFinished(action.getPos()[0], action.getPos()[1]);
                }
                break;
            case CONNECTED:
                opponent = action.getOtherName();
                if (enemy.getShips().size() == config.getShips().size())
                    enemyShipsPlaced();
                break;
            case JOIN:
                GameConfig config1 = new Gson().fromJson(action.getConfig(), GameConfig.class);
                if (config1 != null) {
                    config1.duplicate(config);
                    if (callback != null)
                        callback.gotConfig();
                }
                break;
            case INFO:
                Action action1 = new Action(Action.ActionType.JOIN, name, uuid);
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
                if (callback != null)
                    callback.gotConfig();
                break;
            case SYNC:
                if (!isSync) {
                    i = 0;
                    sync();
                }
                isSync = false;
                World e = new Gson().fromJson(action.getMsg(), World.class);
                e.duplicate(enemy);
                break;
        }
    }

    private void sync() {
        if (client != null) {
            if (opponent != null) {
                isSync = true;
                Action action = new Action(Action.ActionType.SYNC, name, uuid);
                action.setMsg(new Gson().toJson(my));
                client.send(action);
            }
        }
    }

    void onOpen() {
        if (isHost && client != null) {
            Action action = new Action(Action.ActionType.HOST, name, uuid);
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
}
