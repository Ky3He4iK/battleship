package dev.ky3he4ik.battleship.logic;

public interface PlayerFinished {
    void turnFinished(int playerId, int i, int j);

    void shipsPlaced(int playerId);
}
