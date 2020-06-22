package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;

public class MainView implements Screen {
    private StepsDirector stage;
    private Music music;

    public MainView() {
        stage = new StepsDirector();
        Gdx.input.setInputProcessor(stage);
        music = Gdx.audio.newMusic(Gdx.files.internal("TRG_Banks_-_Grandpas_great_escape.ogg"));
        music.setLooping(true);
        music.play();
        music.setVolume(.4f);
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
