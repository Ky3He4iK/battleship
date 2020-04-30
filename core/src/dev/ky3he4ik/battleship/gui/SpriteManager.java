package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpriteManager {
    @NotNull
    private HashMap<String, Sprite> sprites;

    @NotNull
    private HashMap<String, Integer> usageCnt;

    @NotNull
    private static SpriteManager manager = new SpriteManager();

    private SpriteManager() {
        sprites = new HashMap<>();
        usageCnt = new HashMap<>();
    }

    @NotNull
    public Sprite getSprite(@NotNull String name) {
        Sprite sprite = sprites.get(name);
        if (sprite == null)
            sprite = initSprite(name);
        return sprite;
    }

    /**
     * Loads a new sprite from disk and scale it to (width x height)
     * if this sprite had already loaded then do nothing
     */
    @NotNull
    public Sprite initSprite(@NotNull String name, int width, int height) {
        if (sprites.containsKey(name))
            usageCnt.put(name, usageCnt.get(name) + 1);
        else {
            Sprite sprite = new Sprite(new Texture(name));
            if (width != -1)
                sprite.setScale(width / sprite.getX(), height / sprite.getY());
            sprites.put(name, sprite);
            usageCnt.put(name, 1);
        }
        return sprites.get(name);
    }

    @NotNull
    public Sprite cloneSprite(@NotNull String currentName, @NotNull String newName) {
        Sprite sprite;
        if (!contains(currentName))
            sprite = new Sprite(new Texture(currentName));
        else
            sprite = sprites.get(currentName);
        if (sprites.containsKey(newName))
            usageCnt.put(newName, usageCnt.get(newName) + 1);
        else {
            sprites.put(newName, new Sprite(sprite));
            usageCnt.put(newName, 1);
        }
        return getSprite(newName);
    }

    @NotNull
    public Sprite initSprite(@NotNull String name) {
        return initSprite(name, -1, -1);
    }

    public void dispose(@NotNull String name) {
        int cnt = usageCnt.get(name);
        if (cnt <= 1) {
            usageCnt.remove(name);
            sprites.remove(name);
        } else
            usageCnt.put(name, cnt - 1);
    }

    public boolean contains(@NotNull String name) {
        return sprites.containsKey(name);
    }

    public int getUsageCount(@NotNull String name) {
        return usageCnt.get(name);
    }

    @NotNull
    public static SpriteManager getInstance() {
        return manager;
    }
}
