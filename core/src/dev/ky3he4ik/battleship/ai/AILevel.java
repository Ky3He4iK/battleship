package dev.ky3he4ik.battleship.ai;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AILevel {
    NOVICE("infant"),
    EASY("easy"),
    MIDDLE("middle"),
    HARD("hard"),
    UNFAIR("unfair"),
    IMPOSSIBLE("impossible");

    @NotNull
    public final String name;

    AILevel(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public static AILevel getById(int id) {
        if (id < 0 || id >= values().length)
            return null;
        return values()[id];
    }

    public static int getId(@NotNull AILevel aiLevel) {
        AILevel[] values = values();
        for (int i = 0; i < values.length; i++)
            if (values[i].name.equals(aiLevel.name))
                return i;
        return -1;
    }
}
