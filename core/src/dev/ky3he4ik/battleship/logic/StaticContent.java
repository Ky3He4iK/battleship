package dev.ky3he4ik.battleship.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.platform.PlatformSpecific;
import dev.ky3he4ik.battleship.utils.Constants;

public class StaticContent {
    private static @Nullable
    StaticContent instance = null;

    public final @NotNull
    GameConfig config;
    public final @NotNull
    SpriteManager spriteManager = SpriteManager.getInstance();
    public final @NotNull
    AnimationManager animationManager = AnimationManager.getInstance();
    public final @NotNull
    String deviceId;

    public final @NotNull
    PlatformSpecific platformSpecific;


    public static synchronized @NotNull
    StaticContent getInstance() {
        if (instance == null)
            throw new RuntimeException("Access to StaticContent before creation");
        return instance;
    }

    public static synchronized @NotNull
    StaticContent createInstance(@NotNull final PlatformSpecific platformSpecific) {
        if (instance == null)
            instance = new StaticContent(platformSpecific);
        return instance;
    }

    private StaticContent(@NotNull final PlatformSpecific platformSpecific) {
        GameConfig tmp = GameConfig.load();
        if (tmp == null)
            config = GameConfig.getSampleConfigEast();
        else
            config = tmp;

        this.platformSpecific = platformSpecific;
        String tmpStr = platformSpecific.read(Constants.FILENAME_DEVICEID);
        if (tmpStr == null) {
            tmpStr = UUID.randomUUID().toString();
            platformSpecific.write(Constants.FILENAME_DEVICEID, tmpStr);
        }
        deviceId = tmpStr;
    }
}
