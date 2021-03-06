package dev.ky3he4ik.battleship.gui.game_steps;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.game_steps.scene2d.NameClient;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.inet.MultiplayerInet;


public class StepNameClient extends BaseStep {
    private NameClient nameClient;


    StepNameClient(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        nameClient = new NameClient(this);
    }

    @Override
    public void stepBegin() {
        nameClient.init();
    }

    @Override
    public void act() {
        nameClient.act();
    }


    @Override
    public void draw() {
        nameClient.draw();
    }

    @Override
    public int stepEnd() {
        if (callback.isInetClient) {
            callback.gotConfig = false;
            callback.p1Ready = false;
            callback.p2Ready = false;
            callback.rightPlayer.removeCommunication();
            callback.config.setGameType(GameConfig.GameType.GLOBAL_INET);
            Communication leftComm = new MultiplayerInet(callback.rightPlayer.getWorld(), callback.leftPlayer.getWorld(), callback.config, callback.name, callback.uuid, false);
            leftComm.init();
            callback.leftPlayer.setCommunication(leftComm);
            callback.rightPlayer.removeCommunication();
            callback.isP2 = true;
            callback.leftPlayer.init();
            callback.rightPlayer.init();

            callback.setChildrenEnabled(false, false);
        } else {
            Communication rightComm = new MultiplayerInet(callback.leftPlayer.getWorld(), callback.rightPlayer.getWorld(),
                    callback.config, callback.name, callback.uuid, true);
            rightComm.init();
            callback.rightPlayer.setCommunication(rightComm);
        }
        if (callback.name.equals("admin"))
            return StepsDirector.STEP_GET_INFO;
        else if (callback.isInetClient)
            return StepsDirector.STEP_CONNECTING_CLIENT;
        else
            return StepsDirector.STEP_PLACEMENT_L;
    }

    @NotNull
    @Override
    public String getName() {
        return "Enter nickname";
    }

    public void onOk(@NotNull String name) {
        callback.name = name;
        callback.nextStep();
    }
}
