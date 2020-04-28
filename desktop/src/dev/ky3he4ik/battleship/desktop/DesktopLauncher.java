package dev.ky3he4ik.battleship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import dev.ky3he4ik.battleship.MyGdxGame;
import dev.ky3he4ik.battleship.utils.Constants;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "Battleship";
        config.width = Constants.APP_WIDTH;
        config.height = Constants.APP_HEIGHT;
        config.foregroundFPS = 60;
        config.backgroundFPS = 20;
        config.allowSoftwareMode = true;


        new LwjglApplication(new MyGdxGame(), config);
    }
}
