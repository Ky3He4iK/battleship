package dev.ky3he4ik.battleship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.gui.screens.ScreensDirector;
import dev.ky3he4ik.battleship.logic.StaticContent;
import dev.ky3he4ik.battleship.platform.PlatformSpecific;
import dev.ky3he4ik.battleship.utils.Constants;

/**
 * Класс игры и экрана. В конструктор передается платформо-зависимые API.
 * Из логики только запуск музыки на повторе
 * todo: перенести музыку в ScreensDirector
 */
public class MyGdxGame extends Game implements Screen {
    private ScreensDirector stage;
    private Music music;

    public MyGdxGame(@NotNull final PlatformSpecific platformSpecific) {
        StaticContent.createInstance(platformSpecific);
    }

    @Override
    public void create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.app.setLogLevel(Constants.DEBUG_MODE ? Application.LOG_DEBUG : Application.LOG_ERROR);
        stage = new ScreensDirector();
        Gdx.input.setInputProcessor(stage);
        music = Gdx.audio.newMusic(Gdx.files.internal("TRG_Banks_-_Grandpas_great_escape.ogg"));
        music.setLooping(true);
        music.play();
        music.setVolume(.4f);

        setScreen(this);
    }

    @Override
    public void resize(int width, int height) {
        stage.resize(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        music.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void pause() {
        Gdx.graphics.setContinuousRendering(false);
    }

    @Override
    public void resume() {
        Gdx.graphics.setContinuousRendering(true);
    }

    @Override
    public void hide() {
    }
}
