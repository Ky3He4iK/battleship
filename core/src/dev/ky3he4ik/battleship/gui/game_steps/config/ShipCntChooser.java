package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ShipCntChooser extends WidgetGroup implements ActorWithSpriteListener {

    @NotNull
    private Table content;

    @NotNull
    private ShipImage shipImage;

    @NotNull
    private ActorWithSprite moreBtn;

    private final int MORE_BTN_ID = 1;

    @NotNull
    private ActorWithSprite lessBtn;

    private final int LESS_BTN_ID = 2;

    @NotNull
    private Label cntLabel;

    private int count = 0;

    ShipCntChooser(@NotNull GameConfig.Ship ship, @NotNull BitmapFont font, int countStart, float cellSize) {
        lessBtn = new ActorWithSprite(this, Constants.ARROW_LEFT, Constants.ARROW_LEFT_PRESSED, LESS_BTN_ID);
        lessBtn.setColor(1, 1, 1, .7f);
        moreBtn = new ActorWithSprite(this, Constants.ARROW_RIGHT, Constants.ARROW_RIGHT_PRESSED, MORE_BTN_ID);
        shipImage = new ShipImage(ship, cellSize);
        cntLabel = new Label("" + countStart, new Label.LabelStyle(font, font.getColor()));
        count = countStart;

        content = new Table();
        content.setFillParent(true);
        setHeight(cellSize + font.getCapHeight());
        setWidth(cellSize * (ship.length + 2));

        setDebug(Constants.DEBUG_MODE);
        addActor(content);
    }

    public void buildTable(float cellSize) {
        shipImage.setCellSize(cellSize);
        content.clearChildren();
        content.row().height(cellSize);
        content.add(lessBtn).width(cellSize);
        content.add(shipImage).width(cellSize * shipImage.ship.length);
        content.add(moreBtn).width(cellSize);
        content.row();//.height(cellSize);
        content.add(cntLabel).colspan(content.getColumns()).align(Align.center);
        content.align(Align.center);
    }

    public int getCount() {
        return count;
    }

    @NotNull
    public GameConfig.Ship getShip() {
        return shipImage.ship;
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == MORE_BTN_ID)
            count = Math.min(count + 1, 10);
        else if (buttonId == LESS_BTN_ID)
            count = Math.max(count - 1, 0);
        cntLabel.setText(count);
        return true;
    }

    @Override
    public void buttonReleased(int buttonId) {
    }

    @Override
    public void buttonMoved(int buttonId) {
    }
}
