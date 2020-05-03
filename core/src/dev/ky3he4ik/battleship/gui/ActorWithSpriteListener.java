package dev.ky3he4ik.battleship.gui;

public interface ActorWithSpriteListener {
    public boolean buttonPressed(int buttonId);

    public void buttonReleased(int buttonId);

    public void buttonMoved(int buttonId);
}
