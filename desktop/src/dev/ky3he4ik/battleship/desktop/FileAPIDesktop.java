package dev.ky3he4ik.battleship.desktop;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import dev.ky3he4ik.battleship.platform.FileAPI;

public class FileAPIDesktop implements FileAPI {
    @Nullable
    @Override
    public String read(@NotNull String filename) {
        try (FileReader fileReader = new FileReader(filename)) {
            try (BufferedReader reader = new BufferedReader(fileReader)) {
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = reader.readLine();
                }
                return sb.toString();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean write(@NotNull String filename, @NotNull String content) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
