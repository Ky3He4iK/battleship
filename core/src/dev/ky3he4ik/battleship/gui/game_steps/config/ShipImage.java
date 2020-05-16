package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    public final GameConfig.Ship ship;

    private float cellSize;

    ShipImage(@NotNull GameConfig.Ship ship, float cellSize) {
        shipSprite = SpriteManager.getInstance().getSprite(ship.name);
        cellSprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
        this.ship = ship;
        this.cellSize = cellSize;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(shipSprite, getX(), getY(), shipSprite.getOriginX(), shipSprite.getOriginY(),
                ship.length * cellSize, cellSize, 1, 1, shipSprite.getRotation());
        for (int i = 0; i < ship.length; i++)
            batch.draw(shipSprite, getX() + i * cellSize, getY(), cellSize, cellSize);
    }
}
