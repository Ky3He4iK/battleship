package dev.ky3he4ik.battleship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import dev.ky3he4ik.battleship.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Battleship";
		config.width = 800;
		config.height = 480;
		config.foregroundFPS = 60;
		config.backgroundFPS = 30;


		new LwjglApplication(new MyGdxGame(), config);
	}
}
