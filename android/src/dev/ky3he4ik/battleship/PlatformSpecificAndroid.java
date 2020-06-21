package dev.ky3he4ik.battleship;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;

import dev.ky3he4ik.battleship.platform.PlatformSpecific;

public class PlatformSpecificAndroid implements PlatformSpecific {
    private @NotNull
    Context context;

    PlatformSpecificAndroid(@NotNull Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public String read(@NotNull String filename) {
        return null;
    }

    @Override
    public boolean write(@NotNull String filename, @NotNull String content) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
            return true;
        } catch (IOException e) {
            Log.e("FileAPIAndroid", e.getMessage(), e);
        }
        return false;
    }

    @NotNull
    @Override
    public String platformName() {
        return "Android" + Build.VERSION.SDK_INT;
    }
}
