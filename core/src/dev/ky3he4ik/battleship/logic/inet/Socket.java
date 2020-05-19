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

    Socket(@NotNull MultiplayerInet callback) throws URISyntaxException {
        super(new URI(Constants.HOST_ADRESS));
        this.callback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
        Action action = Action.fromJson(message);
        if (action != null && action.getActionType() != Action.ActionType.OK && action.getActionType() != Action.ActionType.NO)
            callback.onAction(action);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        Gdx.app.error("Socket", ex.getMessage(), ex);
    }

    void send(@NotNull Action action) {
        send(action.toJson());
    }

    void disconnect(@NotNull String name, long uuid) {
        send(new Action(Action.ActionType.DISCONNECT, name, uuid));
        close();
    }
}
