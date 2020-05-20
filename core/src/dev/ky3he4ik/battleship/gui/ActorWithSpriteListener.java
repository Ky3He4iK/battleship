package dev.ky3he4ik.battleship.gui;

public interface ActorWithSpriteListener {
    boolean buttonPressed(int buttonId);

    void buttonReleased(int buttonId);

    void buttonMoved(int buttonId);
}
