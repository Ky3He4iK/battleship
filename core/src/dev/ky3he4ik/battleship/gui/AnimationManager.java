package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class AnimationManager {
    @NotNull
    private static AnimationManager animationManager = new AnimationManager();
    @NotNull
    private HashMap<String, Animation<TextureRegion>> animations;

    @NotNull
    private HashMap<String, Integer> usageCnt;

    private AnimationManager() {
        animations = new HashMap<>();
        usageCnt = new HashMap<>();
    }

    @NotNull
    public static AnimationManager getInstance() {
        return animationManager;
    }

    @NotNull
    public Animation<TextureRegion> initAnimation(@NotNull AnimationInfo animationInfo) {
        if (animations.containsKey(animationInfo.name))
            usageCnt.put(animationInfo.name, usageCnt.get(animationInfo.name) + 1);
        else {
            Animation<TextureRegion> animation = new Animation<>(animationInfo.frameLength, loadFrames(animationInfo.name, animationInfo.colons, animationInfo.rows));
            animation.setPlayMode(animationInfo.isLooped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
            animations.put(animationInfo.name, animation);

            usageCnt.put(animationInfo.name, 1);
        }
        return animations.get(animationInfo.name);
    }

    @Nullable
    private Animation<TextureRegion> getAnimation(@NotNull String name) {
        return animations.get(name);
    }

    @NotNull
    public Animation<TextureRegion> getAnimation(@NotNull AnimationInfo animationInfo) {
        if (contains(animationInfo.name))
            return animations.get(animationInfo.name);
        return initAnimation(animationInfo);
    }

    @Nullable
    public Animation<TextureRegion> cloneAnimation(@NotNull String currentName, @NotNull String newName) {
        if (animations.containsKey(newName))
            usageCnt.put(newName, usageCnt.get(newName) + 1);
        else if (contains(currentName)) {
            Animation<TextureRegion> animation = animations.get(currentName);
            animations.put(newName, new Animation<>(animation.getFrameDuration(), animation.getKeyFrames()));
            usageCnt.put(newName, 1);
        }
        return getAnimation(newName);
    }

    @NotNull
    public Animation<TextureRegion> cloneAnimation(@NotNull AnimationInfo animationInfo, @NotNull String newName) {
        if (animations.containsKey(newName))
            usageCnt.put(newName, usageCnt.get(newName) + 1);
        else {
            Animation<TextureRegion> animation;
            if (contains(animationInfo.name)) {
                animation = animations.get(animationInfo.name);
                animation = new Animation<>(animation.getFrameDuration(), animation.getKeyFrames());
            } else
                animation = new Animation<>(animationInfo.frameLength, loadFrames(animationInfo.name, animationInfo.colons, animationInfo.rows));
            animation.setPlayMode(animationInfo.isLooped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);

            animations.put(newName, animation);
            usageCnt.put(newName, 1);
        }
        return animations.get(newName);
    }

    private boolean contains(@NotNull String name) {
        return animations.containsKey(name);
    }

    public boolean contains(@NotNull AnimationInfo animationInfo) {
        return animations.containsKey(animationInfo.name);
    }

    private TextureRegion[] loadFrames(@NotNull String name, int colons, int rows) {
        Texture sheet = new Texture(name);
        TextureRegion[][] tmp = TextureRegion.split(sheet,
                sheet.getWidth() / colons,
                sheet.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[colons * rows];
        int index = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < colons; j++)
                frames[index++] = tmp[i][j];
        return frames;
    }

    public void dispose(@NotNull String name) {
        dispose(name, false);
    }

    private void dispose(@NotNull String name, boolean force) {
        if (!contains(name))
            return;
        int cnt = usageCnt.get(name);
        if (cnt <= 1 || force) {
            usageCnt.remove(name);
            TextureRegion[] frames = animations.remove(name).getKeyFrames();
            for (TextureRegion frame : frames)
                frame.getTexture().dispose();
        } else
            usageCnt.put(name, cnt - 1);
    }

    public void dispose() {
        for (String name : usageCnt.keySet())
            dispose(name, true);
        usageCnt.clear();
        animations.clear();
    }

    public static class AnimationInfo {
        final float frameLength;
        public final String name;
        final int colons;
        final int rows;
        final boolean isLooped;

        AnimationInfo(float frameLength, @NotNull String name, int colons, int rows, boolean isLooped) {
            this.frameLength = frameLength;
            this.name = name;
            this.colons = colons;
            this.rows = rows;
            this.isLooped = isLooped;
        }

        @NotNull
        public static AnimationInfo byFPS(float fps, @NotNull String name, int colons, int rows, boolean isLooped) {
            return new AnimationInfo(1 / fps, name, colons, rows, isLooped);
        }

        @NotNull
        public static AnimationInfo byDuration(float duration, @NotNull String name, int colons, int rows, boolean isLooped) {
            return new AnimationInfo(duration / (colons * rows), name, colons, rows, isLooped);
        }
    }
}
