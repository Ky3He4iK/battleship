package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.SpriteManager;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class ShipPlacer extends Group {

    @NotNull
    private ArrayList<GameConfig.Ship> availableShips;
    @NotNull
    private ArrayList<AloneShip> ships;
    @NotNull
    private BitSet usedShips;
    @Nullable
    private Field field = null;

    private boolean process = false;
    private float cellSize;

    public ShipPlacer(@NotNull ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        this.cellSize = cellSize;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());
    }

    public void dispose() {
        for (GameConfig.Ship ship : availableShips)
            SpriteManager.getInstance().dispose(ship.name);
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void restart() {
        usedShips.set(0, availableShips.size(), false);
        for (AloneShip ship : ships)
            removeActor(ship);
        ships.clear();
        process = false;
    }

    public void start(@NotNull Field field) {
        process = true;
        this.field = field;
        int posY = 0, maxLen = 0, posX = 0;
        for (GameConfig.Ship ship : availableShips) {
            if (ship.length > maxLen)
                maxLen = ship.length;

            AloneShip aShip = new AloneShip(this, ship.name, ship.length);
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

    public void released(float x, float y, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Release at " + x + "x" + y);
        if (field != null)
            field.unHighlight();
    }

    public void hover(float x, float y, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Hover at " + x + "x" + y);
        if (field != null)
            field.highlight(x, y, ship.getShipRotation(), ship.length);
    }
}
