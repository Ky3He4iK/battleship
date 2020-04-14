package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class MyGdxGame extends Game {
    SpriteBatch batch;
    BitmapFont font;

    @Override
//    @SuppressWarnings()
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
//        this.setScreen(new MainScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
