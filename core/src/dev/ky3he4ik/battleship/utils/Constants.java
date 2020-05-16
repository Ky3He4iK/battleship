package dev.ky3he4ik.battleship.utils;

import dev.ky3he4ik.battleship.gui.AnimationManager;

public class Constants {
    public static final float HEADER_PART = .1f;
    public static final float FOOTER_PART = .1f;
    public static final float SIDE_PART = .05f;
    public static final float MIDDLE_GAP_PART = .1f;

    public static final boolean DEBUG_MODE = Boolean.getBoolean("debug");

    public static final String GAME_NAME = "Advanced sea battle";

    public static final int APP_WIDTH = 1400;
    public static final int APP_HEIGHT = 700;

    public static final String CELL_CLOSED_IMG = "cell_closed_2.png";
    public static final String CELL_EMPTY_IMG = "cell_empty_2.png";
    public static final String CELL_UNDAMAGED_IMG = "cell_undamaged.png";
    public static final String CELL_DAMAGED_IMG = "cell_hit_2.png";
    public static final String CELL_SUNK_IMG = "cell_sunk_2.png";

    public static final String SHIP_CARRIER_IMG = "Carrier.png";
    public static final String SHIP_BATTLESHIP_IMG = "Battleship.png";
    public static final String SHIP_DESTROYER_IMG = "Destroyer.png";
    public static final String SHIP_SUBMARINE_IMG = "Submarine.png";
    public static final String SHIP_PATROL_BOAT_IMG = "Patrol_boat.png";
    public static final String SHIP_RUBBER_BOAT_IMG = "Rubber_boat.png";


    public static final String ARROW_ROTATE = "rotate_arrow.png";
    public static final String ARROW_ROTATE_SELECTED = "rotate_arrow_selected.png";
    public static final String ARROW_TURN = "arrow_simplest_white.png";
    public static final String BUTTON_RND = "Button_rnd.png";
    public static final String BUTTON_RND_SELECTED = "Button_rnd_selected.png";
    public static final String BUTTON_DONE = "Button_done.png";
    public static final String BUTTON_DONE_SELECTED = "Button_done_selected.png";
    public static final String BUTTON_DONE_FRAME = "Button_done_frame.png";
    public static final String BUTTON_FRAME = "Button_frame_selected.png";
    public static final String BUTTON_FRAME_SELECTED = "Button_frame_selected.png";
    public static final String BUTTON_FRAME_SELECTED_2 = "Button_frame_selected_2.png";

    public static final String SLIDER_BACKGROUND = "slider_background.png";
    public static final String SLIDER_KNOB = "slider_knob.png";

    public static final String ARROW_RIGHT = "arrow_right.png";
    public static final String ARROW_RIGHT_PRESSED = "arrow_right_pressed.png";
    public static final String ARROW_LEFT = "arrow_left.png";
    public static final String ARROW_LEFT_PRESSED = "arrow_left_pressed.png";

    public static final String ROTATED_SUFFIX = "_rot.png";

    public static final AnimationManager.AnimationInfo BLOW_ANIMATION = AnimationManager.AnimationInfo.byDuration(1, "explosion_animation_4_3.png", 4, 3, false);
    public static final AnimationManager.AnimationInfo WATER_BLOW_ANIMATION = AnimationManager.AnimationInfo.byDuration(.8f, "water_blow_animation_6_4.png", 6, 4, false);
    public static final AnimationManager.AnimationInfo WATER_ANIMATION = AnimationManager.AnimationInfo.byFPS(60, "water_animation_8_4.jpg", 8, 4, true);
    public static final AnimationManager.AnimationInfo LOADING_ANIMATION = AnimationManager.AnimationInfo.byDuration(1, "loading_animation_4_3.png", 4, 3, true);
}
