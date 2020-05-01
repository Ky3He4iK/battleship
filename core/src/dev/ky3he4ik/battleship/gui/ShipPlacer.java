package dev.ky3he4ik.battleship.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.logic.GameConfig;

public class ShipPlacer extends Actor {
    private ArrayList<GameConfig.Ship> availableShips;
    private BitSet usedShips;
    private boolean process = false;

    public ShipPlacer(ArrayList<GameConfig.Ship> availableShips) {
        this.availableShips = availableShips;
        usedShips = new BitSet(availableShips.size());

        for (GameConfig.Ship ship: availableShips)
            SpriteManager.getInstance().initSprite(ship.name);

    }

    public void dispose() {
        for (GameConfig.Ship ship: availableShips)
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
}
