package dev.ky3he4ik.battleship;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);

            BufferedReader reader = new BufferedReader(inputStreamReader);
            try {
                StringBuilder stringBuilder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                return stringBuilder.toString();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            Log.e("PlatformSpecificAndroid", e.getMessage(), e);
        }
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
            Log.e("PlatformSpecificAndroid", e.getMessage(), e);
        }
        return false;
    }

    @NotNull
    @Override
    public String platformName() {
        return "Android" + Build.VERSION.SDK_INT;
    }
}
