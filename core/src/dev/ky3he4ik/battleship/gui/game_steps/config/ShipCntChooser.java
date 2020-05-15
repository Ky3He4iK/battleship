package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import org.jetbrains.annotations.NotNull;

import dev.ky3he4ik.battleship.gui.ActorWithSprite;
import dev.ky3he4ik.battleship.logic.GameConfig;

public class ShipCntChooser extends WidgetGroup {

    @NotNull
    private Table content;

    @NotNull
    private ShipImage shipImage;

    @NotNull
    private ActorWithSprite moreBtn;

    @NotNull
    private ActorWithSprite lessBtn;

    @NotNull
    private Label cntLabel;

    private int count = 0;

    ShipCntChooser(@NotNull GameConfig.Ship ship) {
        content = new Table();

        addActor(content);
    }

    public int getCount() {
        return count;
    }
}
