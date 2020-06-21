package dev.ky3he4ik.battleship.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.gui.SpriteManager;

public class StaticContent {
    private static @Nullable
    StaticContent instance = null;
    private @NotNull
    GameConfig config;
    private @NotNull
    SpriteManager spriteManager = SpriteManager.getInstance();
    private @NotNull
    AnimationManager animationManager = AnimationManager.getInstance();

    public synchronized @NotNull
    StaticContent getInstance() {
        if (instance == null)
            instance = new StaticContent();
        return instance;
    }

    private StaticContent() {
        GameConfig tmp = GameConfig.load();
        if (tmp == null)
            config = GameConfig.getSampleConfigEast();
        else
            config = tmp;


    }
}
