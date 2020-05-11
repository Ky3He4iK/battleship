package dev.ky3he4ik.battleship.gui.game_steps.config;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages a group of buttons to enforce a minimum and maximum number of checked buttons. This enables "radio button"
 * functionality and more. A button may only be in one group at a time.
 * <p>
 * The {@link #canCheck(TextButton, boolean)} method can be overridden to control if a button check or uncheck is allowed.
 *
 * @author Nathan Sweet
 */
public class NormalButtonGroup extends Container<HorizontalGroup> {
    @NotNull
    private final Array<TextButton> buttons = new Array();
    @NotNull
    private Array<TextButton> checkedButtons = new Array(1);
    private int minCheckCount = 1, maxCheckCount = 1;
    private boolean uncheckLast = true;
    @NotNull
    private TextButton lastChecked;
    @NotNull
    private HorizontalGroup group;

    public NormalButtonGroup() {
        minCheckCount = 1;
        group = new HorizontalGroup();
        setActor(group);
    }

    public void add(@NotNull TextButton button) {
        boolean shouldCheck = button.isChecked() || buttons.size < minCheckCount;
        button.setChecked(false);
        buttons.add(button);
        button.setChecked(shouldCheck);
    }

    public void remove(@NotNull TextButton button) {
        buttons.removeValue(button, true);
        checkedButtons.removeValue(button, true);
    }

    public void clear() {
        buttons.clear();
        checkedButtons.clear();
    }

    /**
     * Sets the first {@link TextButton} with the specified text to checked.
     */
    public void setChecked(@NotNull String text) {
        for (int i = 0, n = buttons.size; i < n; i++) {
            TextButton button = buttons.get(i);
            if (text.contentEquals(button.getText())) {
                button.setChecked(true);
                return;
            }
        }
    }

    /**
     * Called when a button is checked or unchecked. If overridden, generally changing button checked states should not be done
     * from within this method.
     *
     * @return True if the new state should be allowed.
     */
    protected boolean canCheck(@NotNull TextButton button, boolean newState) {
        if (button.isChecked() == newState) return false;

        if (!newState) {
            // Keep button checked to enforce minCheckCount.
            if (checkedButtons.size <= minCheckCount)
                return false;
            checkedButtons.removeValue(button, true);
        } else {
            // Keep button unchecked to enforce maxCheckCount.
            if (maxCheckCount != -1 && checkedButtons.size >= maxCheckCount) {
                if (uncheckLast) {
                    int old = minCheckCount;
                    minCheckCount = 0;
                    lastChecked.setChecked(false);
                    minCheckCount = old;
                } else
                    return false;
            }
            checkedButtons.add(button);
            lastChecked = button;
        }

        return true;
    }

    /**
     * Sets all buttons' {@link Button#isChecked()} to false, regardless of {@link #setMinCheckCount(int)}.
     */
    public void uncheckAll() {
        int old = minCheckCount;
        minCheckCount = 0;
        for (int i = 0, n = buttons.size; i < n; i++) {
            TextButton button = buttons.get(i);
            button.setChecked(false);
        }
        minCheckCount = old;
    }

    /**
     * @return The first checked button, or null.
     */
    @Nullable
    public TextButton getChecked() {
        if (checkedButtons.size > 0) return checkedButtons.get(0);
        return null;
    }

    /**
     * @return The first checked button index, or -1.
     */
    public int getCheckedIndex() {
        if (checkedButtons.size > 0) return buttons.indexOf(checkedButtons.get(0), true);
        return -1;
    }

    @NotNull
    public Array<TextButton> getAllChecked() {
        return checkedButtons;
    }

    @NotNull
    public Array<TextButton> getButtons() {
        return buttons;
    }

    /**
     * Sets the minimum number of buttons that must be checked. Default is 1.
     */
    public void setMinCheckCount(int minCheckCount) {
        this.minCheckCount = minCheckCount;
    }

    /**
     * Sets the maximum number of buttons that can be checked. Set to -1 for no maximum. Default is 1.
     */
    public void setMaxCheckCount(int maxCheckCount) {
        if (maxCheckCount == 0) maxCheckCount = -1;
        this.maxCheckCount = maxCheckCount;
    }

    /**
     * If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked
     * is unchecked so that the maximum is not exceeded. If false, additional buttons beyond the maximum are not allowed to be
     * checked. Default is true.
     */
    public void setUncheckLast(boolean uncheckLast) {
        this.uncheckLast = uncheckLast;
    }
}
