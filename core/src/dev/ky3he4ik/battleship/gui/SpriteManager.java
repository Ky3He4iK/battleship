package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class SpriteManager {
    private HashMap<String, Sprite> sprites;
    private HashMap<String, Integer> usageCnt;
    private static SpriteManager manager;

    private SpriteManager() {
        sprites = new HashMap<>();
        usageCnt = new HashMap<>();
    }

    public Sprite getSprite(String name) {
        return sprites.get(name);
    }

    /**
     * Loads a new sprite from disk and scale it to (width x height)
     * if this sprite had already loaded then do nothing
     */
    public Sprite initSprite(String name, int width, int height) {
        if (sprites.containsKey(name))
            usageCnt.put(name, usageCnt.get(name) + 1);
        else {
            Sprite sprite = new Sprite(new Texture(name));
            if (width != -1)
                sprite.setScale(width / sprite.getX(), height / sprite.getY());
            sprites.put(name, sprite);
            usageCnt.put(name, 1);
        }
        return getSprite(name);
    }

    public Sprite cloneSprite(String currentName, String newName) {
        if (!sprites.containsKey(currentName))
            throw new NoSuchElementException("No sprite with name " + currentName);
        if (sprites.containsKey(newName))
            throw new IllegalArgumentException("Sprite with name " + newName + " is already here");
        sprites.put(newName, new Sprite(sprites.get(currentName)));
        usageCnt.put(newName, 1);
        return getSprite(newName);
    }

    public Sprite initSprite(String name) {
        return initSprite(name, -1, -1);
    }

    public void dispose(String name) {
        int cnt = usageCnt.get(name);
        if (cnt <= 1) {
            usageCnt.remove(name);
            sprites.remove(name);
        } else
            usageCnt.put(name, cnt - 1);
    }

    public boolean contains(String name) {
        return sprites.containsKey(name);
    }

    public int getUsageCount(String name) {
        return usageCnt.get(name);
    }

    public static SpriteManager getInstance() {
        if (manager == null)
            manager = new SpriteManager();
        return manager;
    }
}
