package dev.ky3he4ik.battleship.logic.inet;

import com.badlogic.gdx.Gdx;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

import dev.ky3he4ik.battleship.utils.Constants;

public class Socket extends WebSocketClient {
    @NotNull
    private MultiplayerInet callback;

    @NotNull
    private String name;

    private long uuid;

    Socket(@NotNull MultiplayerInet callback, @NotNull String name, long uuid) throws URISyntaxException {
        super(new URI(Constants.HOST_ADDRESS));
        this.callback = callback;
        this.name = name;
        this.uuid = uuid;
        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send(new Action(Action.ActionType.CONNECT, name, uuid));
        callback.onOpen();
    }

    @Override
    public void onMessage(String message) {
        Action action = Action.fromJson(message);
        if (action != null && action.getActionType() != Action.ActionType.PING)
            Gdx.app.debug("Socket", "Receive: " + message);
        if (action != null && action.getActionType() != Action.ActionType.OK && action.getActionType() != Action.ActionType.NO)
            callback.onAction(action);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
//        if (ex instanceof ConnectException) {
//            callback.reconnect(null);
//        }
        Gdx.app.error("Socket", ex.getMessage(), ex);
    }

    void send(@NotNull Action action) {
        if (action.getActionType() != Action.ActionType.PING)
            Gdx.app.debug("Socket", "Sending: " + action.toJson());
        if (!isConnected())
            callback.reconnect(action);
        else
            send(action.toJson());
    }

    void disconnect(@NotNull String name, long uuid) {
        send(new Action(Action.ActionType.DISCONNECT, name, uuid));
        close();
    }

    private boolean isConnected() {
        return getSocket().isConnected();
    }
}
