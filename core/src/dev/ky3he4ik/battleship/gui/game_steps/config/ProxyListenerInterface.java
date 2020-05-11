package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProxyListenerInterface {
    boolean proxyPressed(@NotNull InputEvent event, @Nullable String actorName);

    void proxyReleased(@NotNull InputEvent event, @Nullable String actorName);

    void proxyDragged(@NotNull InputEvent event, @Nullable String actorName);

    void proxyScrolled(@NotNull InputEvent event, @Nullable String actorName);
}
