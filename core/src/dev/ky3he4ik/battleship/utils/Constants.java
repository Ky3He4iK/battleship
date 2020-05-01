package dev.ky3he4ik.battleship.utils;

import dev.ky3he4ik.battleship.gui.AnimationManager;

public class Constants {
    public static final int CELL_MARGIN = 2;
    public static final int CELLS_CNT_X = 25;
    public static final int CELLS_CNT_Y = 15;
    public static final int MIDDLE_GAP = 3;

    public static final float HEADER_PART = .25f;
    public static final float FOOTER_PART = .1f;
    public static final float SIDE_PART = .1f;
    public static final float MIDDLE_GAP_PART = .15f;

    public static final boolean DEBUG_MODE = Boolean.getBoolean("debug");

    public static final String GAME_NAME = "Advanced sea battle";

    public static final int APP_WIDTH = 1400;
    public static final int APP_HEIGHT = 700;

    public static final String CELL_CLOSED_IMG = "cell_closed.png";
    public static final String CELL_EMPTY_IMG = "cell_empty.png";
    public static final String CELL_UNDAMAGED_IMG = "cell_undamaged.png";
    public static final String CELL_DAMAGED_IMG = "cell_hit.png";
    public static final String CELL_SUNK_IMG = "cell_sunk.png";

    public static final String SHIP_CARRIER_IMG = "Carrier.png";
    public static final String SHIP_BATTLESHIP_IMG = "Battleship.png";
    public static final String SHIP_DESTROYER_IMG = "Destroyer.png";
    public static final String SHIP_SUBMARINE_IMG = "Submarine.png";
    public static final String SHIP_PATROL_BOAT_IMG = "Patrol_boat.png";
    public static final String SHIP_RUBBER_BOAT_IMG = "Rubber_boat.png";

    public static final String ROTATED_SUFFIX = "_rot";

    public static final AnimationManager.AnimationInfo BLOW_ANIMATION = AnimationManager.AnimationInfo.byDuration(1, "explosion_animation_4_3.png", 4, 3, false);
    public static final AnimationManager.AnimationInfo WATER_BLOW_ANIMATION = AnimationManager.AnimationInfo.byDuration(.8f, "water_blow_animation_6_4.png", 6, 4, false);
    public static final AnimationManager.AnimationInfo WATER_ANIMATION = AnimationManager.AnimationInfo.byFPS(60, "water_animation_8_4.jpg", 8, 4, true);
}
