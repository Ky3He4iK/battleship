package dev.ky3he4ik.battleship.ai;

import com.badlogic.gdx.Gdx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.PlayerFinished;
import dev.ky3he4ik.battleship.logic.World;

public enum AILevel {
    NOVICE(0, "Infant") {
        @NotNull
        @Override
        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
            return new AITraining(callback, enemy, my, config);
        }
    },
    EASY(1, "Easy") {
        @NotNull
        @Override
        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
            return new AIDummy(callback, enemy, my, config);
        }
    },
//    MIDDLE(2, "Middle") {
//        @NotNull
//        @Override
//        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
//            return new AIDummy(callback, enemy, my, config);
//        }
//    },
//    HARD(3, "Hard") {
//        @NotNull
//        @Override
//        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
//            return new AIDummy(callback, enemy, my, config);
//        }
//    },
    UNFAIR(2, "Unfair") {
        @NotNull
        @Override
        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
            return new AIDummy(callback, enemy, my, config);
        }
    },
    IMPOSSIBLE(3, "Impossible") {
        @NotNull
        @Override
        public AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config) {
            return new AIImpossible(callback, enemy, my, config);
        }
    };

    public final int id;

    @NotNull
    public final String name;

    AILevel(int id, @NotNull String name) {
        this.name = name;
        this.id = id;
    }

    @Nullable
    public static AILevel getById(int id) {
        if (id < 0 || id >= values().length)
            return null;
        if (values()[id].id != id)
            Gdx.app.error("AILevel/getById", "Level at " + id + " has incorrect id");
        return values()[id];
    }

    @Nullable
    public static AILevel getByName(@NotNull String name) {
        for (AILevel level : values())
            if (level.name.equals(name))
                return level;
        return null;
    }

    public static int getId(@NotNull AILevel aiLevel) {
        AILevel[] values = values();
        for (int i = 0; i < values.length; i++)
            if (values[i].name.equals(aiLevel.name))
                return i;
        return -1;
    }

    @NotNull
    public abstract AI getAI(@Nullable PlayerFinished callback, @NotNull final World enemy, @NotNull final World my, @NotNull GameConfig config);
}
