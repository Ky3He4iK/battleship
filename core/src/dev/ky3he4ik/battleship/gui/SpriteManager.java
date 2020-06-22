package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpriteManager {
    @NotNull
    private static SpriteManager manager = new SpriteManager();
    @NotNull
    private HashMap<String, Sprite> sprites;
    @NotNull
    private HashMap<String, Integer> usageCnt;

    private SpriteManager() {
        sprites = new HashMap<>();
        usageCnt = new HashMap<>();
    }

    @NotNull
    public static SpriteManager getInstance() {
        return manager;
    }

    @NotNull
    public Sprite getSprite(@NotNull String name) {
        Sprite sprite = sprites.get(name);
        if (sprite == null)
            sprite = initSprite(name);
        return sprite;
    }

    @NotNull
    Sprite initSprite(@NotNull String name) {
        if (sprites.containsKey(name))
            usageCnt.put(name, usageCnt.get(name) + 1);
        else {
            Sprite sprite = new Sprite(new Texture(name));
            sprites.put(name, sprite);
            usageCnt.put(name, 1);
        }
        return sprites.get(name);
    }

    public void dispose(@NotNull String name) {
        dispose(name, false);
    }

    public void dispose(@NotNull String name, boolean force) {
        if (!contains(name))
            return;
        int cnt = usageCnt.get(name);
        if (cnt <= 1 || force) {
            usageCnt.remove(name);
            sprites.remove(name).getTexture().dispose();
        } else
            usageCnt.put(name, cnt - 1);
    }

    public void dispose() {
        for (String name : usageCnt.keySet())
            dispose(name, true);
        usageCnt.clear();
        sprites.clear();
    }

    private boolean contains(@NotNull String name) {
        return sprites.containsKey(name);
    }
}
