package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ShipImage extends Widget {
    @NotNull
    public final GameConfig.Ship ship;
    @NotNull
    private Sprite cellSprite;
    @NotNull
    private Sprite shipSprite;
    private float cellSize;

    ShipImage(@NotNull GameConfig.Ship ship, float cellSize) {
        shipSprite = SpriteManager.getInstance().getSprite(ship.rotatedName());
        cellSprite = SpriteManager.getInstance().getSprite(Constants.CELL_CLOSED_IMG);
        this.ship = ship;
        this.cellSize = cellSize;
    }

    void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float shift = (getWidth() - ship.length * cellSize) / 2;

        Color batchColor = batch.getColor();
        batchColor.a = 1;
        batch.setColor(1, 1, 1, .7f);

        for (int i = 0; i < ship.length; i++)
            batch.draw(cellSprite, getX() + i * cellSize + shift, getY(), cellSize, cellSize);
        batch.draw(shipSprite, getX() + shift, getY(), ship.length * cellSize, cellSize);
        batch.setColor(batchColor);
    }
}
