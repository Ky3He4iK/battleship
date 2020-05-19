package dev.ky3he4ik.battleship.logic;

public interface PlayerFinished {
    void turnFinished( int i, int j);

    void shipsPlaced();

    void gotConfig();
}
