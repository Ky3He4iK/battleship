package dev.ky3he4ik.battleship.gui.placing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.gui.ActorWithSpriteListener;
import dev.ky3he4ik.battleship.gui.Field;
import dev.ky3he4ik.battleship.gui.GameStage;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;

public class ShipPlacer extends Group implements AloneShipListener, ActorWithSpriteListener {
    @NotNull
    private ArrayList<GameConfig.Ship> availableShips;
    @NotNull
    private ArrayList<AloneShip> ships;
    @NotNull
    private BitSet usedShips;
    @Nullable
    private Field field = null;
    @NotNull
    private ArrayList<ActorWithSprite> childrens;
    @NotNull
    private GameStage callback;

    private static final int BUTTON_ROTATE = 1;
    private static final int BUTTON_RANDOM = 2;
    private static final int BUTTON_DONE = 3;

    private boolean process = false;
    private float cellSize;

    private int lastAcessId = 0;

    public ShipPlacer(@NotNull GameStage callback, @NotNull ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        this.cellSize = cellSize;
        this.callback = callback;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());
        childrens = new ArrayList<>();

        ActorWithSprite button = new ActorWithSprite(this, Constants.ARROW_ROTATE, BUTTON_ROTATE);
        childrens.add(button);
        addActor(button);

        button = new ActorWithSprite(this, Constants.BUTTON_RND, BUTTON_RANDOM);
        H.setBoundsByHeight(button, 0, getY() - cellSize, cellSize);
        childrens.add(button);
        addActor(button);

        button = new ActorWithSprite(this, Constants.BUTTON_DONE, BUTTON_DONE);
        button.setBounds(cellSize * 4, getY() - cellSize, cellSize, cellSize);
        childrens.add(button);
        addActor(button);

//        if (Constants.DEBUG_MODE)
//            setDebug(true, true);
    }

    public void dispose() {
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void restart(float middleGap) {
        usedShips.set(0, availableShips.size(), false);
        for (AloneShip ship : ships) {
            ship.setVisible(false);
            removeActor(ship);
        }
        ships.clear();
        process = false;
        float shift = middleGap * 0.1f;
        middleGap -= shift * 2;
        for (ActorWithSprite child : childrens) {
            switch (child.getButtonId()) {
                case BUTTON_ROTATE:
                    H.setBoundsByWidth(child, -middleGap - shift, getHeight() / 2 - middleGap / 2, middleGap);
                    break;
                case BUTTON_RANDOM:
                    H.setBoundsByHeight(child, 0, -cellSize, cellSize);
                    break;
                case BUTTON_DONE:
                    child.setBounds(getWidth() - cellSize, -cellSize, cellSize, cellSize);
                    break;
            }
        }
    }

    public void start(@NotNull Field field) {
        process = true;
        this.field = field;
        int posY = 1, maxLen = 0, posX = 0;
        for (GameConfig.Ship ship : availableShips) {
            if (ship.length > maxLen)
                maxLen = ship.length;
            AloneShip aShip = new AloneShip(this, ship.name, ship.length, ship.id);
            aShip.setPlaced(true);
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

    @Override
    public boolean shipPressed(@NotNull float[] pos, @NotNull AloneShip ship) {
        lastAcessId = ship.id - 1;
        if (field != null)
            field.removeShip(pos[0], pos[1], ship.id);
        return true;
    }

    @Override
    public void shipReleased(@NotNull float[] pos, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Release at " + pos[0] + "x" + pos[1]);
        lastAcessId = ship.id - 1;
        if (availableShips.get(ship.id - 1).id != ship.id) {
            Gdx.app.error("ShipPlacer", "Error: ship with id " + availableShips.get(ship.id - 1).id + " at pos " + ship.id);
            Gdx.app.debug("ShipPlacer", "Ship id:");
            for (int i = 0; i < availableShips.size(); i++) {
                Gdx.app.debug("ShipPlacer", "" + i + ": " + availableShips.get(i).id);
            }
        }
        if (field != null) {
            float[] newPos = field.unHighlight(availableShips.get(ship.id - 1), pos[0], pos[1], ship.getShipRotation());
            float[] curPos = H.getAbsCoord(this);
            if (newPos != null) {
                ship.setPlaced(true);
                Gdx.app.debug("ShipPlacer", "Ship placed: " + ship.id + " (" + ship.getShipName() + ")");
                ship.setPosition(newPos[0] - curPos[0], newPos[1] - curPos[1]);
            } else
                ship.setPlaced(false);
        }
    }

    @Override
    public void shipMoved(@NotNull float[] pos, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Hover at " + pos[1] + "x" + pos[1]);
        lastAcessId = ship.id - 1;
        if (field != null)
            field.highlight(pos[0], pos[1], ship.getShipRotation(), ship.length);
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        switch (buttonId) {
            case BUTTON_ROTATE:

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
                    float[] res = field.rotate(availableShips.get(lastAcessId), H.getAbsCoord(ship), ship.getShipRotation());
                    if (res == null) {
                        if (ship.getShipRotation() == World.ROTATION_VERTICAL)
                            ship.moveBy(cellSize / 4, -cellSize / 4);
                        else
                            ship.moveBy(-cellSize / 4, cellSize / 4);
                        ship.setPlaced(false);
                    } else {
                        ship.setPlaced(true);
                        if (Math.abs(res[0] + 99999) > .1f) {
                            ship.setPosition(res[0] - getX(), res[1] - getY());
                        }
                    }
                }
                return true;
            case BUTTON_RANDOM:
                if (field != null) {
                    H.placeShipsRandom(field.getWorld(), availableShips);
                    for (World.Ship ship : field.getWorld().getShips()) {
                        ships.get(ship.code - 1).setPosition(field.globalCellX(ship.idx) - getX(), field.globalCellY(ship.idy) - getY());
                        ships.get(ship.code - 1).setShipRotation(ship.rotation);
                        ships.get(ship.code - 1).setPlaced(true);
                    }
                    Gdx.app.debug("ShipPlacer", "Placed randomly");
                    return true;
                }
                return false;
            case BUTTON_DONE:
                if (field != null && field.getWorld().getShips().size() == availableShips.size()) {
                    callback.shipsPlaced(field.getPlayerId());
                    return true;
                }
                return false;
            default:
                Gdx.app.debug("ShipPlacer", "Unknown button: " + buttonId);
                return false;
        }
    }

    @Override
    public void buttonReleased(int buttonId) {
        switch (buttonId) {
            case BUTTON_ROTATE:
                //todo
                break;
            case BUTTON_RANDOM:
                //todo
                break;
            case BUTTON_DONE:
                //todo
                break;
            default:
                Gdx.app.debug("ShipPlacer", "Unknown button: " + buttonId);
        }
    }

    @Override
    public void buttonMoved(int buttonId) {
        switch (buttonId) {
            case BUTTON_ROTATE:
                //todo
                break;
            case BUTTON_RANDOM:
                //todo
                break;
            case BUTTON_DONE:
                //todo
                break;
            default:
                Gdx.app.debug("ShipPlacer", "Unknown button: " + buttonId);
        }
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }
}
