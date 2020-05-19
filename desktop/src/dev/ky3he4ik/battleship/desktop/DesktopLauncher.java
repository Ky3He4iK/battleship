package dev.ky3he4ik.battleship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.net.NetworkInterface;
import java.util.Random;

import dev.ky3he4ik.battleship.MyGdxGame;
import dev.ky3he4ik.battleship.utils.Constants;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = Constants.GAME_NAME;
        config.width = Constants.APP_WIDTH;
        config.height = Constants.APP_HEIGHT;
        config.foregroundFPS = 60;
        config.backgroundFPS = 20;
        config.allowSoftwareMode = true;

        long uuid = new Random().nextLong();
        String name = "Desktop#" + uuid;
        try {
            if (NetworkInterface.getByIndex(0) != null) {
                byte[] address = NetworkInterface.getByIndex(0).getHardwareAddress();
                uuid = 0;
                for (byte b : address)
                    uuid = (uuid << 8) | b;
                name = "Desktop" + uuid;
            }
        } catch (Exception e) {
            System.err.println("Exception during startup: " + e.getMessage());
            e.printStackTrace();
        }

        new LwjglApplication(new MyGdxGame(name, uuid), config);
    }
}
