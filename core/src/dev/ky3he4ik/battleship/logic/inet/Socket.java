package dev.ky3he4ik.battleship.logic.inet;

import com.badlogic.gdx.Gdx;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import dev.ky3he4ik.battleship.utils.Constants;

public class Socket extends WebSocketClient {
    @NotNull
    private MultiplayerInet callback;

    @NotNull
    private String name;

    private long uuid;

    Socket(@NotNull MultiplayerInet callback, @NotNull String name, long uuid) throws URISyntaxException, InterruptedException {
        super(new URI(Constants.HOST_ADDRESS));
        this.callback = callback;
        this.name = name;
        this.uuid = uuid;
        connectBlocking();
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
            Gdx.app.error("Socket", "Receive: " + message);
        if (action != null && action.getActionType() != Action.ActionType.OK && action.getActionType() != Action.ActionType.NO)
            callback.onAction(action);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        callback.reconnect(null);
    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof ConnectException || ex instanceof WebsocketNotConnectedException) {
            callback.reconnect(null);
        }
        Gdx.app.error("Socket", ex.getMessage(), ex);
    }

    void send(@NotNull Action action) {
        if (action.getActionType() != Action.ActionType.PING)
            Gdx.app.error("Socket", "Sending: " + action.toJson());
        if (!isConnected())
            callback.reconnect(action);
        else
            try {
                send(action.toJson());
            } catch (WebsocketNotConnectedException e) {
                callback.reconnect(action);
            }
    }

    void disconnect(@NotNull String name, long uuid) {
        send(new Action(Action.ActionType.DISCONNECT, name, uuid));
        close();
    }

    private boolean isConnected() {
        if (getSocket() != null)
            return getSocket().isConnected();
        return false;
    }
}
