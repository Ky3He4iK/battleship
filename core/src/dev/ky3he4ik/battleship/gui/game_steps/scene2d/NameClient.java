package dev.ky3he4ik.battleship.gui.game_steps.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.gui.game_steps.StepNameClient;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.gui.game_steps.config.ProxyListenerInterface;
import dev.ky3he4ik.battleship.utils.Constants;

public class NameClient extends Stage implements ActorWithSpriteListener {
    private final int OK_BUTTON_ID = 1;

    @NotNull
    private final StepNameClient callback;

    @NotNull
    private BitmapFont font;

    @NotNull
    private Label.LabelStyle labelStyle;

    @NotNull
    private TextField nameInput;

    @NotNull
    private Label nameLabel;

    @NotNull
    private ActorWithSprite okButton;

    private InputProcessor inputProcessor;


    public NameClient(@NotNull StepNameClient callback) {
        this.callback = callback;

        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 100f);
        font.setColor(Color.BLACK);

        labelStyle = new Label.LabelStyle(font, font.getColor());

        Skin skin = SpriteManager.getInstance().getSkin();
        nameInput = new TextField("Anon #" + Math.abs(new Random().nextInt() % 10000), skin);
        addActor(nameInput);

        nameLabel = new Label("Enter your name:", skin);
        addActor(nameLabel);

        okButton = new ActorWithSprite(this, Constants.BUTTON_DONE, Constants.BUTTON_DONE_SELECTED, OK_BUTTON_ID);
        okButton.setVisible(true);
        okButton.setName("Config/Done");
        addActor(okButton);
    }

    public void init() {
        StepsDirector callbackCallback = callback.getCallback();

        inputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);

        float cellSize = Math.min(Gdx.graphics.getWidth() / 25f, Gdx.graphics.getHeight() / 12.5f);

        nameLabel.setPosition((Gdx.graphics.getWidth() - nameLabel.getWidth()) / 2, Gdx.graphics.getHeight() - nameLabel.getHeight());
        nameLabel.setVisible(true);
        nameInput.setPosition((Gdx.graphics.getWidth() - nameInput.getWidth()) / 2, Gdx.graphics.getHeight() - nameLabel.getHeight() - nameInput.getHeight());
        nameInput.setVisible(true);

        okButton.setBounds(Gdx.graphics.getWidth() - callbackCallback.getRedundantX() - cellSize - callbackCallback.getSideWidth(),
                callbackCallback.getRedundantY() + callbackCallback.getFooterHeight() - cellSize, cellSize, cellSize);
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == OK_BUTTON_ID) {
            if (nameInput.getText().length() > 0) {
                Gdx.input.setInputProcessor(inputProcessor);
                callback.onOk(nameInput.getText());
                return true;
            }
        }
        return false;
    }

    @Override
    public void buttonReleased(int buttonId) {

    }

    @Override
    public void buttonMoved(int buttonId) {

    }
}
