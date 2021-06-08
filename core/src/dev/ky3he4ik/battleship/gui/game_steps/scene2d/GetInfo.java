package dev.ky3he4ik.battleship.gui.game_steps.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.gui.game_steps.StepGetInfo;
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.logic.inet.MultiplayerInet;
import dev.ky3he4ik.battleship.utils.Constants;

public class GetInfo extends Stage implements ActorWithSpriteListener {
    private final int OK_BUTTON_ID = 1;

    @NotNull
    private final StepGetInfo callback;

    @NotNull
    private BitmapFont font;

    @NotNull
    private Label.LabelStyle labelStyle;

    @NotNull
    private TextArea infoArea;

    @NotNull
    private Label infoLabel;

    @NotNull
    private ActorWithSprite okButton;

    @NotNull
    private ScrollPane scrollPane;

    @NotNull
    private Container<ScrollPane> textContainer;

    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    private InputProcessor inputProcessor;

    private MultiplayerInet player;

    @Nullable
    String info = null;

    float cellSize;


    public GetInfo(@NotNull StepGetInfo callback) {
        this.callback = callback;

        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);


        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 800f);
        font.setColor(Color.BLACK);

        labelStyle = new Label.LabelStyle(font, font.getColor());

        Skin skin = SpriteManager.getInstance().getSkin();
        infoArea = new TextArea("", skin);
        infoArea.setDisabled(true);

        infoLabel = new Label("Enter your name:", skin);

        okButton = new ActorWithSprite(this, Constants.BUTTON_DONE, Constants.BUTTON_DONE_SELECTED, OK_BUTTON_ID);
        okButton.setVisible(true);
        okButton.setName("Config/Done");
        addActor(okButton);

        scrollPane = new ScrollPane(infoArea);

        textContainer = new Container<>(scrollPane);
//        tableContainer.setFillParent(true);
        addActor(textContainer);

    }


    public void init() {
        StepsDirector callbackCallback = callback.getCallback();

        inputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);

        cellSize = Math.min(Gdx.graphics.getWidth() / 25f, Gdx.graphics.getHeight() / 12.5f);

        infoLabel.setPosition(callbackCallback.getRedundantX(), Gdx.graphics.getHeight() - callbackCallback.getRedundantY());
        infoLabel.setVisible(true);


        textContainer.setBounds(callbackCallback.getRedundantX(),
                callbackCallback.getRedundantY() + callbackCallback.getFooterHeight(),
                Gdx.graphics.getWidth() - callbackCallback.getRedundantX() * 2 - callbackCallback.getSideWidth() * 2,
                Gdx.graphics.getHeight() - callbackCallback.getRedundantY() * 2 - callbackCallback.getFooterHeight() - callbackCallback.getHeaderHeight() - infoLabel.getHeight());
        infoArea.setBounds(textContainer.getX(), textContainer.getY(), textContainer.getWidth(), textContainer.getHeight());
        scrollPane.setBounds(0, 0, infoArea.getWidth(), infoArea.getHeight());
        textContainer.setVisible(false);
        scrollPane.setVisible(false);

        okButton.setBounds(Gdx.graphics.getWidth() - callbackCallback.getRedundantX() - cellSize - callbackCallback.getSideWidth(),
                callbackCallback.getRedundantY() + callbackCallback.getFooterHeight() - cellSize, cellSize, cellSize);

        cTime = 0;

        if (callbackCallback.isInetClient)
            player = (MultiplayerInet) callbackCallback.leftPlayer.getCommunication();
        else
            player = (MultiplayerInet) callbackCallback.rightPlayer.getCommunication();

        if (player == null)
            Gdx.app.error("GetInfo", "Player is null!");
        else
            player.requestInfo();
    }

    @Override
    public void act() {
//        if (player == null)
//            callback.nextStep();
        if (player.getInfo() != null) {
            textContainer.setVisible(true);
            scrollPane.setVisible(true);
            info = player.getInfo();
            infoArea.setText(info);
        }
        super.act();
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        batch.begin();
        if (info == null) {
            cTime += Gdx.graphics.getDeltaTime();
            TextureRegion frame = animation.getKeyFrame(cTime);
            batch.draw(frame, callback.getWidth() / 2 - cellSize / 2f, callback.getHeight() / 2 - cellSize / 2f, cellSize, cellSize);
        } else
            font.draw(batch, info, 10, Gdx.graphics.getHeight() - 10);

        batch.end();
        super.draw();
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == OK_BUTTON_ID) {
            Gdx.input.setInputProcessor(inputProcessor);
//            callback.onOk();
            return true;
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
