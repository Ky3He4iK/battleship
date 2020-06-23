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
import dev.ky3he4ik.battleship.gui.game_steps.StepsDirector;
import dev.ky3he4ik.battleship.gui.utils.GH;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;
import dev.ky3he4ik.battleship.utils.Constants;
import dev.ky3he4ik.battleship.utils.H;
import dev.ky3he4ik.battleship.utils.vectors.Vec2d;

public class ShipPlacer extends Group implements AloneShipListener, ActorWithSpriteListener {
    private final int BUTTON_ROTATE;
    private final int BUTTON_RANDOM;
    private final int BUTTON_DONE;
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
    private StepsDirector callback;
    private float cellSize;

    private int lastAccessId = 0;

    public ShipPlacer(@NotNull StepsDirector callback, @NotNull ArrayList<GameConfig.Ship> availableShips, float cellSize) {
        this.availableShips = availableShips;
        this.cellSize = cellSize;
        this.callback = callback;
        usedShips = new BitSet(availableShips.size());
        ships = new ArrayList<>(availableShips.size());
        childrens = new ArrayList<>();

        BUTTON_ROTATE = childrens.size();
        ActorWithSprite button = new ActorWithSprite(this, Constants.ARROW_ROTATE, Constants.ARROW_ROTATE_SELECTED, BUTTON_ROTATE);
        childrens.add(button);
        addActor(button);

        BUTTON_RANDOM = childrens.size();
        button = new ActorWithSprite(this, Constants.BUTTON_RND, Constants.BUTTON_RND_SELECTED, BUTTON_RANDOM);
        GH.setBoundsByHeight(button, 0, getY() - cellSize, cellSize);
        childrens.add(button);
        addActor(button);

        BUTTON_DONE = childrens.size();
        button = new ActorWithSprite(this, Constants.BUTTON_DONE, Constants.BUTTON_DONE_SELECTED, BUTTON_DONE);
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

    public void restart(float middleGap, @NotNull ArrayList<GameConfig.Ship> availableShips) {
        this.availableShips = availableShips;
        usedShips.set(0, availableShips.size(), false);
        for (AloneShip ship : ships) {
            ship.setVisible(false);
            removeActor(ship);
        }
        ships.clear();
        float shift = middleGap * 0.1f;
        middleGap -= shift * 2;

        childrens.get(BUTTON_ROTATE).setBounds(-middleGap - shift, getHeight() / 2 - middleGap / 2, middleGap, middleGap);
        GH.setBoundsByHeight(childrens.get(BUTTON_RANDOM), 0, -cellSize, cellSize);
        childrens.get(BUTTON_DONE).setBounds(getWidth() - cellSize, -cellSize, cellSize, cellSize);
    }

    public void start(@NotNull Field field) {
        this.field = field;
        int posY = 0, posX = 0;
        for (GameConfig.Ship ship : availableShips) {
            if ((posX + ship.length) * cellSize > getWidth()) {
                posX = 0;
                posY += 2;
                if (posY * cellSize > getHeight())
                    Gdx.app.error("ShipPlacer", "Too many ships to place");
            }
            AloneShip aShip = new AloneShip(this, ship);
            aShip.setPlaced(true);
            aShip.setBounds(posX * cellSize, posY * cellSize, cellSize * ship.length, cellSize);
            addActor(aShip);
            ships.add(ship.id - 1, aShip);
            posX += ship.length + 1;
        }
    }

    @Override
    public boolean shipPressed(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        lastAccessId = ship.id - 1;
        if (field != null)
            field.removeShip(ship.id);
        return true;
    }

    @Override
    public void shipReleased(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Release at " + pos.x + "x" + pos.y);
        lastAccessId = ship.id - 1;
        if (availableShips.get(ship.id - 1).id != ship.id) {
            Gdx.app.error("ShipPlacer", "Error: ship with id " + availableShips.get(ship.id - 1).id + " at pos " + ship.id);
            Gdx.app.debug("ShipPlacer", "Ship id:");
            for (int i = 0; i < availableShips.size(); i++) {
                Gdx.app.debug("ShipPlacer", "" + i + ": " + availableShips.get(i).id);
            }
        }
        if (field != null) {
            Vec2d newPos = field.unHighlight(availableShips.get(ship.id - 1), pos.x, pos.y, ship.getShipRotation());
            Vec2d curPos = GH.getAbsCoord(this);
            if (newPos != null) {
                ship.setPlaced(true);
                Gdx.app.debug("ShipPlacer", "Ship placed: " + ship.id + " (" + ship.getShipName() + ")");
                ship.setPosition(newPos.x - curPos.x, newPos.y - curPos.y);
            } else
                ship.setPlaced(false);
        }
    }

    @Override
    public void shipMoved(@NotNull Vec2d pos, @NotNull AloneShip ship) {
        Gdx.app.debug("ShipPlacer", "Hover at " + pos.x + "x" + pos.y);
        lastAccessId = ship.id - 1;
        if (field != null)
            field.highlight(pos.x, pos.y, ship.getShipRotation(), ship.length);
    }

    @Override
    public boolean isPlaced(int shipId) {
        return true;
    }

    @Override
    public boolean buttonPressed(int buttonId) {
        if (buttonId == BUTTON_ROTATE) {
            Gdx.app.debug("ShipPlacer", "rotating " + lastAccessId);
            if (availableShips.get(lastAccessId).id != lastAccessId + 1) {
                Gdx.app.error("ShipPlacer", "Error: ship with id " + availableShips.get(lastAccessId).id + " at pos " + lastAccessId);
                Gdx.app.debug("ShipPlacer", "Ship id:");
                for (int i = 0; i < availableShips.size(); i++) {
                    Gdx.app.debug("ShipPlacer", "" + i + ": " + availableShips.get(i).id);
                }
            }
            ships.get(lastAccessId).rotate();
            if (field != null) {
                AloneShip ship = ships.get(lastAccessId);
                Vec2d res = field.rotate(ship, GH.getAbsCoord(ship), ship.getShipRotation());
                if (res == null)
                    ship.setPlaced(false);
                else {
                    ship.setPlaced(true);
                    if (Math.abs(res.x + 99999) > .1f)
                        ship.setPosition(res.x - getX(), res.y - getY());
                }
            }
            return true;
        } else if (buttonId == BUTTON_RANDOM) {
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
        } else if (buttonId == BUTTON_DONE) {
            if (field != null && field.getWorld().getShips().size() == availableShips.size()) {
                callback.shipsPlaced(field.getPlayerId());
                return true;
            }
            return false;
        } else {
            Gdx.app.debug("ShipPlacer", "Unknown button: " + buttonId);
            return false;
        }
    }

    @Override
    public void buttonReleased(int buttonId) {
    }

    @Override
    public void buttonMoved(int buttonId) {
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }
}
