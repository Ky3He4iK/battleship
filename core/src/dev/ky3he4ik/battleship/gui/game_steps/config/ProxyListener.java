package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;

public class ProxyListener implements EventListener {
    @NotNull
    private ProxyListenerInterface callback;

    public ProxyListener(@NotNull ProxyListenerInterface callback) {
        this.callback = callback;
    }

    @Override
    public boolean handle(Event e) {
        if (e instanceof InputEvent) {
            InputEvent event = (InputEvent) e;
            String actorName = e.getTarget().getName();

            Vector2 tmpCoords = new Vector2();
            event.toCoordinates(event.getListenerActor(), tmpCoords);

            switch (event.getType()) {
                case touchDown:
                    return callback.proxyPressed(event, actorName);
                case touchUp:
                    callback.proxyReleased(event, actorName);
                    return true;
                case touchDragged:
                    callback.proxyDragged(event, actorName);
                    return true;
                case scrolled:
                    callback.proxyScrolled(event, actorName);
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }
}
