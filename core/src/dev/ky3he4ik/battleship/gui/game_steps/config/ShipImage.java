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
        shipSprite = SpriteManager.getInstance().getSprite(ship.rotatedName());
        cellSprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
        this.ship = ship;
        this.cellSize = cellSize;
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        for (int i = 0; i < ship.length; i++)
            batch.draw(cellSprite, getX() + i * cellSize, getY(), cellSize, cellSize);
        batch.draw(shipSprite, getX(), getY(), ship.length * cellSize, cellSize);
    }
}
