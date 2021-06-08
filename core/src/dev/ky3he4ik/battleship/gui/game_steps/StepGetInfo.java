package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.AnimationManager;
import dev.ky3he4ik.battleship.logic.inet.MultiplayerInet;
import dev.ky3he4ik.battleship.utils.Constants;

public class StepGetInfo extends BaseStep implements ActorWithSpriteListener {
    private final int OK_BUTTON_ID = 1;

    @NotNull
    private BitmapFont font;

    @NotNull
    private ActorWithSprite okButton;

    @NotNull
    private Animation<TextureRegion> animation;

    private float cTime;

    private MultiplayerInet player;

    @Nullable
    String info = null;

    float cellSize;


    StepGetInfo(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        animation = AnimationManager.getInstance().getAnimation(Constants.LOADING_ANIMATION);


        font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() / 800f);
        font.setColor(Color.BLACK);

        okButton = new ActorWithSprite(this, Constants.BUTTON_DONE, Constants.BUTTON_DONE_SELECTED, OK_BUTTON_ID);
        okButton.setVisible(true);
        okButton.setName("Config/Done");
        addActor(okButton);
    }

    @Override
    public void stepBegin() {
        cellSize = Math.min(Gdx.graphics.getWidth() / 25f, Gdx.graphics.getHeight() / 12.5f);


        okButton.setBounds(Gdx.graphics.getWidth() - callback.getRedundantX() - cellSize - callback.getSideWidth(),
                callback.getRedundantY() + callback.getFooterHeight() - cellSize, cellSize, cellSize);

        cTime = 0;

        if (callback.isInetClient)
            player = (MultiplayerInet) callback.leftPlayer.getCommunication();
        else
            player = (MultiplayerInet) callback.rightPlayer.getCommunication();

        if (player == null)
            Gdx.app.error("GetInfo", "Player is null!");
        else
            player.requestInfo();
    }

    @Override
    public void act() {
        if (player == null)
            callback.nextStep();
        if (player.getInfo() != null)
            info = player.getInfo();
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
            font.draw(batch, info, 10, Gdx.graphics.getHeight() - 100);

        batch.end();
        super.draw();
    }

    @Override
    public int stepEnd() {
        if (callback.isInetClient)
            return StepsDirector.STEP_CONNECTING_CLIENT;
        else
            return StepsDirector.STEP_PLACEMENT_L;
    }

    @NotNull
    @Override
    public String getName() {
        return "Get info";
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == OK_BUTTON_ID) {
            callback.nextStep();
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
