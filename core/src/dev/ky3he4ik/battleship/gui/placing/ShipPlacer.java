package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.World;
import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.GameStage;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.H;

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
    private ArrayList<Actor> childrens;
    @NotNull
    private GameStage callback;

    private boolean process = false;
    private float cellSize;

    private int lastAcessId = -1;

    public ShipPlacer(@NotNull GameStage callback, @NotNull ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        this.cellSize = cellSize;
        this.callback = callback;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());
        childrens = new ArrayList<>();

        Actor button = new RotateButton(this);
        button.setBounds(-cellSize * 1.5f, getY() + getHeight() / 2 + cellSize / 2, cellSize, cellSize);
        childrens.add(button);
        addActor(button);

        button = new ButtonRandom(this);
        button.setBounds(0, getY() - cellSize, cellSize * 3, cellSize);
        childrens.add(button);
        addActor(button);
        button = new ButtonDone(this);
        button.setBounds(cellSize * 4, getY() - cellSize, cellSize, cellSize);
        childrens.add(button);
        addActor(button);
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
        if (field != null) {
            float[] pos = field.unHighlight(availableShips.get(ship.id - 1));
            if (pos != null) {
                Gdx.app.debug("ShipPlacer", "Ship placed: " + ship.id + " (" + ship.getShipName() + ")");
                ship.setPosition(pos[0] - getX(), pos[1] - getY());
            }
        }
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
            if (field != null) {
                AloneShip ship = ships.get(lastAcessId);
                float[] res = field.rotate(availableShips.get(lastAcessId), ship.getGlobalX(), ship.getGlobalY(), ship.getShipRotation());
                if (res == null) {
                    if (ship.getShipRotation() == World.ROTATION_VERTICAL)
                        ship.moveBy(cellSize / 4, cellSize / 4);
                    else
                        ship.moveBy(-cellSize / 4, -cellSize / 4);
                } else {
                    if (Math.abs(res[0] + 99999) > .1f) {
                        ship.setPosition(res[0] - getX(), res[1] - getY());
                    }
                }
            }
        }
    }

    public void randomPressed() {
        if (field != null) {
            H.placeShipsRandom(field.getWorld(), availableShips);
            for (World.Ship ship : field.getWorld().getShips()) {
                ships.get(ship.code - 1).setPosition(field.globalCellX(ship.idx) - getX(), field.globalCellY(ship.idy) - getY());
                ships.get(ship.code - 1).setShipRotation(ship.rotation);
            }
        }
        Gdx.app.debug("ShipPlacer", "Placed randomly");
    }

    public void donePressed() {
        if (field != null && field.getWorld().getShips().size() == availableShips.size())
            callback.shipsPlaced(field.getPlayerId());
    }
}
