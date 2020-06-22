package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.MainView;
import dev.ky3he4ik.battleship.logic.StaticContent;
import dev.ky3he4ik.battleship.platform.PlatformSpecific;
import dev.ky3he4ik.battleship.utils.Constants;

public class MyGdxGame extends Game {
    public MyGdxGame(@NotNull final PlatformSpecific platformSpecific) {
        StaticContent.createInstance(platformSpecific);
    }

    @Override
    public void create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.app.setLogLevel(Constants.DEBUG_MODE ? Application.LOG_DEBUG : Application.LOG_ERROR);
        setScreen(new MainView());
    }
}
