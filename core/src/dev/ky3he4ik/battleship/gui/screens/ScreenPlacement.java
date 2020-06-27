package dev.ky3he4ik.battleship.gui.screens;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.utils.Constants;

/**
 * Этап расстановки кораблей (а потом еще и доп. элементы?)
 * todo: do
 */
public class ScreenPlacement extends BaseScreen {
    private boolean isP1 = true;

    ScreenPlacement(@NotNull ScreensDirector callback, int screenId) {
        super(callback, screenId);
    }

    @Override
    public void stepBegin() {
        if (staticContent.config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            callback.nextStep();
            return;
        }

        callback.setChildrenEnabled(true, false, false, false, true, true);

        callback.shipPlacer.restart(callback.middleGap, staticContent.config.getShips());
        callback.shipPlacer.start(callback.leftPlayer);
        callback.leftPlayer.setPlaceShips();
        callback.leftPlayer.setPosition(callback.redundantX + callback.sideWidth, callback.redundantY + callback.footerHeight);

        callback.rightPlayer.setPosition(callback.redundantX + callback.sideWidth, callback.redundantY + callback.footerHeight);
        isP1 = true;
    }

    @Override
    public void act() {
        super.act();
        if (isP1) {
            if (callback.p1Ready) {
                isP1 = false;
                callback.leftPlayer.setShowShips((!callback.isP2 && staticContent.config.getGameType() != GameConfig.GameType.LOCAL_2P) || Constants.DEBUG_MODE);

                callback.shipPlacer.restart(callback.middleGap, staticContent.config.getShips());
                callback.shipPlacer.start(callback.rightPlayer);
                callback.setChildrenEnabled(false, false, true, false, true, true);
                callback.rightPlayer.setPosition(callback.redundantX + callback.sideWidth, callback.redundantY + callback.footerHeight);
                callback.rightPlayer.setPlaceShips();
            }
        } else if (callback.p2Ready) {
            callback.nextStep();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "ScreenPlacement";
    }
}
