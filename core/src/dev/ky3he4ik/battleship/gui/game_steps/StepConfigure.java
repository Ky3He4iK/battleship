package dev.ky3he4ik.battleship.gui.game_steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.ai.AIDummy;
import dev.ky3he4ik.battleship.ai.AILevel;
import dev.ky3he4ik.battleship.gui.game_steps.config.ConfigGroup;
import dev.ky3he4ik.battleship.logic.Communication;
import dev.ky3he4ik.battleship.logic.GameConfig;
import dev.ky3he4ik.battleship.logic.World;

public class StepConfigure extends BaseStep {
    private ConfigGroup configGroup;

    StepConfigure(@NotNull StepsDirector callback, int stepId) {
        super(callback, stepId);
        configGroup = new ConfigGroup(this);
//        configGroup.setVisible(false);
    }

    @Override
    public void stepBegin() {
//        callback.nextStep();
        configGroup.init();
    }

    @Override
    public Viewport getViewport() {
        return callback.getViewport();
    }

    @Override
    public void act() {
        configGroup.act();
    }

    @Override
    public void draw() {
        configGroup.draw();
    }

    @Override
    public int stepEnd() {
        configGroup.finish().duplicate(callback.config);
        callback.calcCellSize();

        GameConfig config = callback.config;

        World leftWorld = callback.leftPlayer.getWorld();
        leftWorld.reset(config.getWidth(), config.getHeight());
        World rightWorld = callback.rightPlayer.getWorld();
        rightWorld.reset(config.getWidth(), config.getHeight());

        if (config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            AILevel aiLevel = AILevel.getById(config.getAiLevel2());
            Communication leftComm;
            if (aiLevel == null) {
                leftComm = new AIDummy(null, rightWorld, leftWorld, config);
                Gdx.app.error("StepsDirector", "Invalid aiLevel2: " + config.getAiLevel2() + "; using fallback (AIDummy)");
            } else
                leftComm = aiLevel.getAI(null, rightWorld, leftWorld, config);
            leftComm.init();
            callback.leftPlayer.setCommunication(leftComm);
        }
        callback.leftPlayer.init();

        if (config.getGameType() == GameConfig.GameType.AI || config.getGameType() == GameConfig.GameType.AI_VS_AI) {
            Communication rightComm;
            AILevel aiLevel = AILevel.getById(config.getAiLevel());
            if (aiLevel == null) {
                rightComm = new AIDummy(null, leftWorld, rightWorld, config);
                Gdx.app.error("StepsDirector", "Invalid aiLevel: " + config.getAiLevel() + "; using fallback (AIDummy)");
            } else
                rightComm = aiLevel.getAI(null, leftWorld, rightWorld, config);
            rightComm.init();
            callback.rightPlayer.setCommunication(rightComm);
        }
        callback.rightPlayer.init();

        return super.stepEnd();
    }

    @NotNull
    @Override
    public String getName() {
        return "Configure";
    }

    @NotNull
    public GameConfig getConfig() {
        return callback.config;
    }

    @NotNull
    public StepsDirector getCallback() {
        return callback;
    }

    public void ConfigIsDone() {
        callback.nextStep();
    }
}
