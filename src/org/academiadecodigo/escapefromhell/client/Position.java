package org.academiadecodigo.escapefromhell.client;

/**
 * EscapeFromHell Created by BrunoM24 on 07/11/2017.
 */


public class Position {
    private int col;
    private int row;
    private boolean state;

    Position(int col, int row){
        this.col = col;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
