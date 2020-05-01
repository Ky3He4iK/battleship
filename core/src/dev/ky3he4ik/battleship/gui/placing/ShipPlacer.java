package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

public class ShipPlacer extends Group {

    private ArrayList<GameConfig.Ship> availableShips;
    private ArrayList<AloneShip> ships;
    private BitSet usedShips;
    private boolean process = false;

    public ShipPlacer(ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());

        int posY = 0, maxLen = 0, posX = 0;
        for (GameConfig.Ship ship : availableShips) {
            if (ship.length > maxLen)
                maxLen = ship.length;
            Sprite sprite = SpriteManager.getInstance().initSprite(ship.name);
            sprite.setSize(1, ship.length);
            sprite.setOrigin(.5f, .5f);
            sprite.setRotation(0);
            Sprite sprite_rot = SpriteManager.getInstance().cloneSprite(ship.name, ship.name + Constants.ROTATED_SUFFIX);
            sprite_rot.setOrigin(.5f, .5f);
            sprite_rot.setSize(1, ship.length);
            sprite_rot.setRotation(-90);
            sprite_rot.setFlip(true, false);
            AloneShip aShip = new AloneShip(this, ship.name);
            aShip.setBounds(posX * cellSize, posY * cellSize, cellSize * ship.length, cellSize);
            addActor(aShip);
            ships.add(aShip);
            posY += 2;
            if ((posY + 1) * cellSize > getHeight()) {
                posY = 0;
                posX += maxLen;
                maxLen = 0;
            }
        }

    }

    public void dispose() {
        for (GameConfig.Ship ship : availableShips)
            SpriteManager.getInstance().dispose(ship.name);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void restart() {
        usedShips.set(0, availableShips.size(), false);
        process = false;
    }

    public void start() {
        process = true;
    }

    public void released(float x, float y, AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Released at " + x + "x" + y);
    }
}
