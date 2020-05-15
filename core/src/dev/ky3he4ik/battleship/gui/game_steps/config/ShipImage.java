package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ShipImage extends Widget {
    @NotNull
    private Sprite cellSprite;
    @NotNull
    private Sprite shipSprite;

    @NotNull
    private GameConfig.Ship ship;

    ShipImage(@NotNull GameConfig.Ship ship) {
        shipSprite = SpriteManager.getInstance().getSprite(ship.name);
        cellSprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
        this.ship = ship;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
