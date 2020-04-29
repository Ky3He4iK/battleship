package dev.ky3he4ik.battleship.logic;

public interface PlayerFinished {
    void aiTurnFinished(int playerId, int i, int j);

    void aiShipsPlaced(int playerId);
}
