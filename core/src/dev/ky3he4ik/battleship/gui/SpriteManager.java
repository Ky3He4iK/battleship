package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpriteManager {
    @NotNull
    private static SpriteManager manager = new SpriteManager();
    @NotNull
    private HashMap<String, Sprite> sprites;
    @NotNull
    private HashMap<String, Integer> usageCnt;

    @NotNull
    private final Skin skin;

    private SpriteManager() {
        sprites = new HashMap<>();
        usageCnt = new HashMap<>();
        TextureAtlas atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.addRegions(atlas);
    }

    @NotNull
    public Skin getSkin() {
        return skin;
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

    boolean contains(@NotNull String name) {
        return sprites.containsKey(name);
    }

    public int getUsageCount(@NotNull String name) {
        return usageCnt.get(name);
    }
}
