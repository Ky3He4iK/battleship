package dev.ky3he4ik.battleship.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileAPI {
    @Nullable
    String read(String filename);

    boolean write(@NotNull String filename, @NotNull String content);
}
