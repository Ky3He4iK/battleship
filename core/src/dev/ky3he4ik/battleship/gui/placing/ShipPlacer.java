package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.gui.Field;
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
    @NotNull
    private RotateButton rotateButton;

    private boolean process = false;
    private float cellSize;

    private int lastAcessId = -1;

    public ShipPlacer(@NotNull ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        this.cellSize = cellSize;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());

        rotateButton = new RotateButton(this);
        rotateButton.setBounds(-cellSize * 1.5f, getHeight() / 2 - cellSize / 2, cellSize, cellSize);
        addActor(rotateButton);
    }

    public void dispose() {
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

            AloneShip aShip = new AloneShip(this, ship.name, ship.length, ship.id);
            aShip.setBounds(posX * cellSize, posY * cellSize, cellSize * ship.length, cellSize);
            addActor(aShip);
            ships.add(ship.id - 1, aShip);
            posY += 2;
            if ((posY + 1) * cellSize > getHeight()) {
                posY = 0;
                posX += maxLen + 1;
                maxLen = 0;
            }
        }
    }

    public void released(float x, float y, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Release at " + x + "x" + y);
        lastAcessId = ship.id - 1;
        if (availableShips.get(ship.id - 1).id != ship.id) {
            Gdx.app.error("ShipPlacer", "Error: ship with id " + availableShips.get(ship.id - 1).id + " at pos " + ship.id);
            Gdx.app.debug("ShipPlacer", "Ship id:");
            for (int i = 0; i < availableShips.size(); i++) {
                Gdx.app.debug("ShipPlacer", "" + i + ": " + availableShips.get(i).id);
            }
        }
        if (field != null)
            field.unHighlight(availableShips.get(ship.id - 1));
    }

    public void pressed(float x, float y, @NotNull AloneShip ship) {
        lastAcessId = ship.id - 1;
        if (field != null)
            field.removeShip(x, y, ship.id);
    }

    public void hover(float x, float y, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Hover at " + x + "x" + y);
        lastAcessId = ship.id - 1;
        if (field != null)
            field.highlight(x + getX(), y + getY(), ship.getShipRotation(), ship.length);
    }

    public void rotate() {
        if (lastAcessId != -1) {
            Gdx.app.debug("ShipPlacer", "rotating " + lastAcessId);
            if (availableShips.get(lastAcessId).id != lastAcessId + 1) {
                Gdx.app.error("ShipPlacer", "Error: ship with id " + availableShips.get(lastAcessId).id + " at pos " + lastAcessId);
                Gdx.app.debug("ShipPlacer", "Ship id:");
                for (int i = 0; i < availableShips.size(); i++) {
                    Gdx.app.debug("ShipPlacer", "" + i + ": " + availableShips.get(i).id);
                }
            }
            ships.get(lastAcessId).rotate();
            if (field != null)
                field.rotateAt(ships.get(lastAcessId).getGlobalX(), ships.get(lastAcessId).getGlobalY());
        }
    }
}
