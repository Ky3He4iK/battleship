package dev.ky3he4ik.battleship.logic.inet;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.PlayerFinished;

public class MultiplayerInet extends Thread implements Communication {
    @Nullable
    private PlayerFinished callback = null;

    @NotNull
    private String name;

    private long uuid;

    private long gameId;

    @NotNull
    private String passwd = "";

    private int i = 0;

    private boolean running = true;

    @Nullable
    private Socket client;

    MultiplayerInet(@NotNull String name, long uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public void setTurn() {
    }

    @Override
    public void setPlaceShips() {
        //todo
    }

    @Override
    public void init() {
        start();
        //todo
    }

    @Override
    public void dispose() {
        running = true;
        //todo
    }

    @Override
    public void setCallback(@NotNull PlayerFinished callback) {
        this.callback = callback;
    }

    @Override
    public void restart() {
        //todo
    }

    @Override
    public void run() {
        try {
            client = new Socket(this);
        } catch (Exception e) {
            Gdx.app.error("MultiplayerInet", e.getMessage(), e);
        }
        while (running) {
            try {
                //todo
                i++;
                if (i > 10) {
                    i = 0;
                    if (client != null)
                        client.send(Action.ping(name, uuid).toJson());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Gdx.app.error("MultiplayerInet", e.getMessage(), e);
            }
        }
    }

    @Override
    public void enemyTurned(int x, int y) {
        //todo
    }

    @Override
    public void enemyShipsPlaced() {
        //todo
    }

    @Override
    public void finish() {
        //todo
    }

    void onAction(@NotNull Action action) {
        switch (action.getActionType()) {
            case HOST:
            case PING:
            case NO:
            case OK:
            case CONNECT:
            case PLACE_SHIPS:
                break;
            case GET_HOSTS:
                //todo
                break;
            case TURN:
                //todo
                break;
            case CONNECTED:
                //todo
                break;
            case JOIN:
                //todo
                break;
            case INFO:
                //todo
                break;
            case GAME_END:
                //todo
                break;
            case DISCONNECT:
                //todo
                break;
            case START_GAME:
                //todo
                break;
        }
    }
}
