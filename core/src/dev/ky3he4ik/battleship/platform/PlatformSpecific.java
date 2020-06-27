package dev.ky3he4ik.battleship.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс для работы с платформо-специфичными вещами
 */
public interface PlatformSpecific {
    @Nullable
    String read(String filename);

    boolean write(@NotNull String filename, @NotNull String content);

    @NotNull String platformName();
}
